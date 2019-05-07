package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.mySemaphore;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo implements Serializable {

	private String bookTitle;
	private AtomicInteger amountInInventory;
	private int price;
	private mySemaphore bookSemaphore;
	/**
	 * Constructor.
	 */
	public BookInventoryInfo(String bookTitle, int amountInInventory, int price){
		this.bookTitle = bookTitle;
		this.amountInInventory=new AtomicInteger(amountInInventory);
		this.price = price;
		bookSemaphore=new mySemaphore(amountInInventory);
	}

	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return amountInInventory.get();
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return price;
	}

	/**
	 * in case a customer ordered a certain book, it will decrease it's amount in the Inventory
	 */
	public void decreaseQuantity(){
		amountInInventory.compareAndSet(amountInInventory.get(), amountInInventory.get()-1);
	}

	public mySemaphore getBookSemaphore(){
		return bookSemaphore;
	}
}
