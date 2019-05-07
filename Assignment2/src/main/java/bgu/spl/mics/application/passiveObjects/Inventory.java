package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.HashMap;

import static bgu.spl.mics.application.BookStoreRunner.print;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {

	private BookInventoryInfo[] bookInventoryInfos;

	/**
	 * creating a singleton of Inventory
	 */
	private static class SingletonHolder{
		private static Inventory instance=new Inventory();// inventory singleton implementation
	}
	/**
	 * Constructor.
	 */
	private Inventory() {
	}
	/**
	 * Retrieves the single instance of this class.
	 */

	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (BookInventoryInfo[ ] inventory ) {
		try { // check input validity
			bookInventoryInfos = new BookInventoryInfo[inventory.length];
			for (int i=0;i<inventory.length;i++) {
				bookInventoryInfos[i]= new BookInventoryInfo(inventory[i].getBookTitle(), inventory[i].getAmountInInventory(), inventory[i].getPrice());
			}
		}
		catch(NullPointerException e) {
		}
	}

	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 */
	public OrderResult take (String book) {
		boolean bookFound = false;
		for (int i = 0; !bookFound && i < bookInventoryInfos.length; i++) {
			if (bookInventoryInfos[i].getBookTitle().equals(book) ) {//if the book exists in the inventory
				BookInventoryInfo takeBook = bookInventoryInfos[i];
				if (takeBook.getBookSemaphore().tryAcquire()) {
					if (bookInventoryInfos[i].getAmountInInventory() > 0) {//if the book's amount in the inventory is larger than 1
						bookInventoryInfos[i].decreaseQuantity();//take one
						takeBook.getBookSemaphore().decreasePermits();
						takeBook.getBookSemaphore().release();
						return OrderResult.SUCCESSFULLY_TAKEN;
					}
					bookFound = true;
				}
			}
		}
		return OrderResult.NOT_IN_STOCK;
	}

		/**
		 * Checks if a certain book is available in the inventory.
		 * <p>
		 * @param book 		Name of the book.
		 * @return the price of the book if it is available, -1 otherwise.
		 */
		public int checkAvailabiltyAndGetPrice (String book){
			boolean found=false;
			int bookPrice=-1;
			for(int i=0; !found && i<bookInventoryInfos.length; i++){
				if(bookInventoryInfos[i].getBookTitle().equals(book)){
					bookPrice = bookInventoryInfos[i].getPrice();
					found = true;
				}
			}
			return bookPrice;
		}

		/**
		 *
		 * <p>
		 * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
		 * should be the titles of the books while the values (type {@link Integer}) should be
		 * their respective available amount in the inventory.
		 * This method is called by the main method in order to generate the output.
		 */
		public void printInventoryToFile(String filename) {
			HashMap<String, Integer> printThis = new HashMap<>();
			for (int i = 0; i < bookInventoryInfos.length; i++) {
				printThis.put(bookInventoryInfos[i].getBookTitle(), bookInventoryInfos[i].getAmountInInventory());
			}
			print(filename,printThis);
		}


}

