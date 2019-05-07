package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import static bgu.spl.mics.application.passiveObjects.OrderResult.SUCCESSFULLY_TAKEN;


/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private int currentTick;
	private MoneyRegister moneyRegister;
	private OrderReceipt receipt;
	/**
	 * Constructor.
	 */
	public SellingService(String SellingServiceName) {
		super(SellingServiceName);
		moneyRegister=MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class, br->{
			this.currentTick = br.getCurrentTick();
		});

		this.subscribeBroadcast(Terminate.class, br-> terminate());

		this.subscribeEvent(OrderBookEvent.class,ev->{
			boolean couldBuy=false;
			Future<Integer> futurePrice = this.sendEvent(new CheckAvailability(ev.getBookName()));//future will contain <price>
			if(futurePrice != null){
				Integer price = futurePrice.get();
				synchronized (ev.getCustomer()) {
					if (price != null && price != -1 && (ev.getCustomer().getAvailableCreditAmount() >= price)) {//if it wasn't resolved or the book is out of stock or there's no enough money
						Future<OrderResult> orderResultFuture = this.sendEvent(new TakeBookEvent(ev.getBookName()));//the book exist. we decrease by one it's amount in the inventory
						if(orderResultFuture != null) {
							OrderResult orderResult = orderResultFuture.get();//trying to take a book from the Inventory
							if (orderResult!=null&&orderResult.equals(SUCCESSFULLY_TAKEN)) {//if there was a book to take
								couldBuy = true;//this for to complete the creating of the receipt later
								moneyRegister.chargeCreditCard(ev.getCustomer(), price);//trying to charge the customer
							}
							else
								complete(ev, null);
						}
						else
							complete(ev, null);
					}
					else
						complete(ev, null);
				}
				if (couldBuy) {//creating the receipt
					this.sendEvent(new DeliveryEvent(ev.getCustomer().getAddress(), ev.getCustomer().getDistance()));//delivering the book
					receipt = new OrderReceipt(handleReceiptID(ev.getAPInumber(), ev.getOrderIdCounter()), this.getName(), ev.getCustomer().getId(), ev.getBookName(), price.intValue(), currentTick, ev.getCurrentTick(), currentTick);
					complete(ev, receipt);
					moneyRegister.file(receipt);
				}
			}
			else
			complete(ev, null);
		});
		BookStoreRunner.countUp.getAndIncrement();

	}

	/**
	 * creating a serial number for the receipt ID
	 * @param APInumber
	 * @param orderIdCounter
	 * @return
	 */
	private int handleReceiptID(int APInumber, int orderIdCounter){
		String s1=Integer.toString(APInumber);
		String s2=Integer.toString(orderIdCounter);
		String combine=s1+s2;
		return Integer.valueOf(combine);
	}
}