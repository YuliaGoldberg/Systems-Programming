package bgu.spl.mics.application;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static AtomicInteger countUp;
    public static AtomicInteger countDown;

    public static void main(String[] args) {
        Vector<MicroService> microServices = new Vector<>();
        JsonParser parser = new JsonParser();
        countUp=new AtomicInteger(0);
        String s0 = args[0];
        String s1 = args[1];
        String s2 = args[2];
        String s3 = args[3];
        String s4 = args[4];
        try {

            //get information
            JsonObject json = (JsonObject) parser.parse(new FileReader(s0));//import json file
            //initialize inventory
            JsonArray booksArray = json.get("initialInventory").getAsJsonArray();
            BookInventoryInfo[] books = new BookInventoryInfo[booksArray.size()];//the books array we need to load to Inventory
            for (int i = 0; i < booksArray.size(); i++) {
                JsonObject book = (JsonObject) booksArray.get(i);
                String bookTitle = book.get("bookTitle").getAsString();
                int amount = book.get("amount").getAsInt();
                int price = book.get("price").getAsInt();
                books[i] = new BookInventoryInfo(bookTitle, amount, price);
            }

            Inventory.getInstance().load(books);

            //initialize resources:vehicles
            JsonArray vehicleArray = json.get("initialResources").getAsJsonArray().get(0).getAsJsonObject().get("vehicles").getAsJsonArray();
            DeliveryVehicle[] vehicles = new DeliveryVehicle[vehicleArray.size()];
            for (int i = 0; i < vehicleArray.size(); i++) {
                JsonObject vehicle = (JsonObject) vehicleArray.get(i);
                int license = vehicle.get("license").getAsInt();
                int speed = vehicle.get("speed").getAsInt();
                vehicles[i] = new DeliveryVehicle(license, speed);
            }
            ResourcesHolder.getInstance().load(vehicles);

            //initialise services
            JsonObject services = json.get("services").getAsJsonObject();
            int countServices = 1;

            //initialize TimeService
            JsonObject TimeService = (JsonObject) services.get("time");

            int speed = TimeService.get("speed").getAsInt();
            int duration = TimeService.get("duration").getAsInt();
            MicroService timeService=new TimeService(speed,duration);

            //initialize SellingService
            while (countServices <= services.get("selling").getAsInt()) {
                MicroService sellingService = new SellingService("selling " + countServices);
                microServices.add(sellingService);
                countServices++;
            }
            countServices = 1;

            //initialize InventoryService
            while (countServices <= services.get("inventoryService").getAsInt()) {
                MicroService inventoryService = new InventoryService("inventory " + countServices);
                microServices.add(inventoryService);
                countServices++;
            }
            countServices = 1;

            //initialize LogisticsService
            while (countServices <= services.get("logistics").getAsInt()) {
                MicroService logisticService = new LogisticsService("logistics " + countServices);
                microServices.add(logisticService);
                countServices++;
            }
            countServices = 1;

            //initialize ResourceService
            while (countServices <= services.get("resourcesService").getAsInt()) {
                MicroService resourcesService = new ResourceService("resource " + countServices);
                microServices.add(resourcesService);
                countServices++;
            }
            countServices = 1;
            //initialize Customer& APIService
            JsonArray customersArray = services.get("customers").getAsJsonArray();
            Customer[] customersToHash= new Customer[customersArray.size()] ;
            for(int i=0;i<customersArray.size();i++){
                JsonObject customer=(JsonObject)customersArray.get(i);
                int id=customer.get("id").getAsInt();
                String name=customer.get("name").getAsString();
                String address=customer.get("address").getAsString();
                int distance=customer.get("distance").getAsInt();
                JsonObject credit=(JsonObject)customer.get("creditCard");
                int creditCard=credit.get("number").getAsInt();
                int availableAmountInCreditCard=credit.get("amount").getAsInt();
                JsonArray orderListArray=customer.get("orderSchedule").getAsJsonArray();
                ArrayList<Pair<String,Integer>> orderList=new ArrayList<>();
                for(int j=0;j<orderListArray.size();j++) {//initialize customer's orders
                    JsonObject customerOrder = (JsonObject) orderListArray.get(j);
                    String bookName=customerOrder.get("bookTitle").getAsString();
                    int tick=customerOrder.get("tick").getAsInt();
                    orderList.add(new Pair<>(bookName,tick));
                }
                Customer c=new Customer(id, name, address, distance, creditCard,availableAmountInCreditCard);
                customersToHash[i]= c;
                //customerList.put(id, c);

                //add to microServices list
                MicroService apiService=new APIService("APIService "+countServices,countServices,c, orderList);
                microServices.add(apiService);
                countServices++;
            }

            //initialize all the services except timeService
            ArrayList<Thread> threads=new ArrayList<>();
            for(MicroService mc : microServices){
                Thread t=new Thread(mc);
                threads.add(t);
                t.start();
            }
            //wait for all the microServices to initialize
            while (countUp.get()!=microServices.size()){
                try{
                    Thread.sleep(500);
                }
                catch (Exception e){
                }
            }
            //time service won't be initialized until all the other MC will.
            //initialize timeService
            microServices.add(timeService);
            Thread t = new Thread(timeService);
            t.start();//initialize timeService

            countDown=new AtomicInteger(microServices.size());

            while (countUp.get()!=microServices.size()){//wait for all the threads to terminate
                try{
                    Thread.sleep(500);
                }
                catch (Exception e){}
            }

            for(int i=0;i<threads.size();i++){//main won't die until all the other threads died
                threads.get(i).join();
            }
            t.join();

            while(countDown.get()!= 0){
                try{
                    Thread.sleep(500);
                }
                catch (Exception e){}

            }
            //print to file
            print(s1, customersToHash(customersToHash));
            Inventory.getInstance().printInventoryToFile(s2);
            MoneyRegister.getInstance().printOrderReceipts(s3);
            print(s4, MoneyRegister.getInstance());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //copy all the customers list to an Hash map in order to print
    private static HashMap<Integer,Customer> customersToHash(Customer[] customersToHash){
        HashMap<Integer,Customer> customerList = new HashMap<>();
        for (Customer c: customersToHash){
            customerList.put(c.getId(), c);
        }
        return customerList;

    }

    //print to file
    public static void print(String s, Object o){
        try {
            FileOutputStream fos = new FileOutputStream(s);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.close();
            fos.close();
        } catch (IOException e) {
        }
    }

}