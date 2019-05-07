package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.CheckAvailability;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService {
	private Inventory inventory;
	/**
	 * Constructor.
	 */
	public InventoryService(String inventoryName) {
		super(inventoryName);
		inventory = Inventory.getInstance();
	}

	@Override
	protected void initialize() {

		this.subscribeBroadcast(Terminate.class, br-> terminate());

		this.subscribeEvent(CheckAvailability.class, ev -> {
			Integer price=inventory.checkAvailabiltyAndGetPrice(ev.getBookName());//check if the book exists in the inventory
			complete(ev,price);
		});

		this.subscribeEvent(TakeBookEvent.class, ev -> {
			OrderResult orderResult=inventory.take(ev.getBookName());//in case the book exists, and the customer has enough money, take the book from inventory
			complete(ev,orderResult);
		});

		BookStoreRunner.countUp.getAndIncrement();
	}
}
