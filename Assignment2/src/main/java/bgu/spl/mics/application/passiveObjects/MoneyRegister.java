package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static bgu.spl.mics.application.BookStoreRunner.print;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.synchronized
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable{
	private List<OrderReceipt> orderReceiptList;
	private int totalEarnings;
	/**
	 * creating a singleton of MoneyRegister
	 */
	private static class SingletonHolder{
		private static MoneyRegister instance=new MoneyRegister();// MoneyRegister singleton implementation
	}

	/**
	 * Constructor.
	 */
	private MoneyRegister(){
		orderReceiptList=new LinkedList<>();
		totalEarnings=0;
		}

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return SingletonHolder.instance;
	}

	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		orderReceiptList.add(r);
		totalEarnings=totalEarnings+r.getPrice();
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return totalEarnings;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		if(c.getAvailableCreditAmount()>=amount){
			c.setAvailableCreditAmount(amount);
		}
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		print (filename, orderReceiptList);
	}

	/**
	 * Retrieves the list that contains all the order receipts
	 * @return order receipt list
	 */
	public List<OrderReceipt> getOrderReceiptList(){
		return orderReceiptList;
	}
}
