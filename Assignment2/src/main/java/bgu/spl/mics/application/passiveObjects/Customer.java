package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
	private int id;
	private String name;
	private String address;
	private int distance;
	private List<OrderReceipt> Receipts ;
	private int creditCard;//the number of the credit card
	private int availableAmountInCreditCard;
	private Semaphore semCust;
	/**
	 * Constructor.
	 */
	public Customer(int id, String name, String address, int distance,int creditCard, int availableAmountInCreditCard ) {
		this.id=id;
		this.name=name;
		this.address=address;
		this.distance=distance;
		Receipts = new LinkedList<>();
		this.creditCard=creditCard;
		this.availableAmountInCreditCard=availableAmountInCreditCard;
		this.semCust = new Semaphore(1);
	}
	/**
	 * Retrieves the name of the customer.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the ID of the customer  .
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retrieves the address of the customer.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Retrieves the distance of the customer from the store.
	 */
	public int getDistance() {
		return distance;
	}


	/**
	 * Retrieves a list of receipts for the purchases this customer has made.
	 * <p>
	 * @return A list of receipts.
	 */
	public List<OrderReceipt> getCustomerReceiptList() {
		return Receipts;
	}

	/**
	 * Retrieves the amount of money left on this customers credit card.
	 * <p>
	 * @return Amount of money left.
	 */
	public int getAvailableCreditAmount() {
		return availableAmountInCreditCard;
	}

	/**
	 * Retrieves this customers credit card serial number.
	 */
	public int getCreditNumber() {
		return creditCard;
	}

	/**
	 * decreases the amount in credit card by {@code reduce}
	 * @param reduce
	 */

	public void setAvailableCreditAmount(int reduce){
		availableAmountInCreditCard=availableAmountInCreditCard-reduce;
	}
}
