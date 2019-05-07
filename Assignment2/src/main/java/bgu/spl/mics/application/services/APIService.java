package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.OrderBookEvent;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.Pair;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link OrderBookEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private Customer customer;
	private ArrayList<Future<OrderReceipt>> orderReceipt;//each book has it's own receipt
	private ArrayList<Pair<String,Integer>> orderList;//<bookTitle,orderTick>
	private int currentTick;
	private int APInumber;
	private BlockingQueue<Future> futureList;
	/**
	 * Constructor.
	 */
	public APIService(String APIname, int APInumber, Customer c, ArrayList<Pair<String,Integer>> orderList) {
		super(APIname);//for each service there is an addition to its name with counter
		this.customer=c;
		this.orderReceipt=new ArrayList<>();
		this.orderList=new ArrayList<>();
		for(Pair m:orderList){
			this.orderList.add(m);
		}
		this.APInumber=APInumber;
		this.futureList=new LinkedBlockingQueue<>();
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class,br->{
			int orderIdCounter=1;//counting in order to create a receipt id with this value
			this.currentTick=br.getCurrentTick();
			for(int i=0;i<orderList.size();i++){//for all the book orders the customer created
				if(orderList.get(i).getSecond()==currentTick){//for every OrderTick that equals to currentTick
					Future<OrderReceipt> future=this.sendEvent(new OrderBookEvent(APInumber,orderIdCounter, orderList.get(i).getFirst(), this.customer, this.currentTick));//we send this event to a different selling service
					if(future!=null) {//if the customer could order the book, he has a receipt
						futureList.add(future);//adding the future for it to be resolved later
						orderIdCounter++;
					}

				}
			}
			for(int j=0;j<futureList.size();j++) {
				OrderReceipt receipt=(OrderReceipt)(futureList.poll()).get();//resolving all the futures, expecting to get OrderReceipt
				if(receipt!=null)
					customer.getCustomerReceiptList().add(receipt);//the result of future is a receipt.
			}
		});
		this.subscribeBroadcast(Terminate.class,br-> terminate());

		BookStoreRunner.countUp.getAndIncrement();
	}
}
