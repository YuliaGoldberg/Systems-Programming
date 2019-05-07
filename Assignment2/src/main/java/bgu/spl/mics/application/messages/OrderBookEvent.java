package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;


public class OrderBookEvent implements Event<OrderReceipt>   {
    private String bookName;
    private Customer customer;
    private int currentTick; //process tick
    private int APInumber;
    private int orderIdCounter;


    public OrderBookEvent(int APInumber,int orderIdCounter,String bookName, Customer customer, int currentTick){
        this.bookName=bookName;
        this.customer=customer;
        this.currentTick=currentTick;
        this.APInumber=APInumber;
        this.orderIdCounter=orderIdCounter;
    }
    public String getBookName(){
        return bookName;

    }
    public Customer getCustomer(){
        return customer;
    }
    public int getCurrentTick(){
        return currentTick;
    }
    public int getAPInumber(){ return APInumber;}
    public int getOrderIdCounter(){ return orderIdCounter;}

}
