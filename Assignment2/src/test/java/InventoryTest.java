
import static org.junit.Assert.*;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This is a Unit Test for the {@link Inventory} Class.
 *
 * @author noytourg, yuliagol
 *
 */
public class InventoryTest {
    /** Object under test.*/
    private Inventory inventoryTest = null;
    private BookInventoryInfo[ ] otherBookInventory = null;//this is helpful for testing this class.

    /**
     * Set up for a test.
     * @throws Exception
     */
    @Before
    protected void setUp() throws Exception {
        this.inventoryTest = inventoryTest.getInstance();
        otherBookInventory = new BookInventoryInfo[3];//create an input array
        otherBookInventory[0] =  new BookInventoryInfo("Diary of a Wimpy Kid 1", 0 , 70);
        otherBookInventory[1] =  new BookInventoryInfo("The Curious Incident of the Dog in the Night-Time", 5 , 60);
        otherBookInventory[2] =  new BookInventoryInfo("The Kite Runner", 1 , 90);
    }

    /**
     * Test method for {@link bgu.spl.mics.application.passiveObjects.Inventory#load(BookInventoryInfo[])} ()}:
     * after an Inventory is created, it is initially empty.
     * After we "load" the BookInventoryInfo of Inventory with books, it is supposed to contain books.
     */
    @Test
    public void load() {
        assertNull("In the beginning the inventory is null", inventoryTest );//the array is still not loaded
        inventoryTest.load(otherBookInventory);//insert the info of the input array into this.array
        for(int i=0; i<otherBookInventory.length; i++){//check equality after insertion
            assertEquals(inventoryTest.checkAvailabiltyAndGetPrice(otherBookInventory[i].getBookTitle()), otherBookInventory[i].getPrice());
        }
    }
    /**
     * Test method for {@link Inventory#load(BookInventoryInfo[])}  :
     * This is a negative test - cause an exception to be thrown.
     * inventory class supposed to contain BookInventoryInfo list-in this case it won't. we expect the method to throw an exception.
     */
    @Test(expected = Exception.class)
    public void loadException() {//in case that load gets a null BookInventoryInfo
        inventoryTest.load(null);
    }
    /**
     * Test method for {@link Inventory#getInstance()}:
     * This is a negative test - cause an exception to be thrown.
     * inventory class supposed to contain BookInventoryInfo list-in this case it won't. we expect the method to throw an exception.
     */
    @Test
    public void getInstance() {
        assertNotNull(inventoryTest); // this should have created a new inventory
        Inventory inventory2=inventoryTest.getInstance();//this should return "inventoryTest" because we already created inventory and it should be created only once
        assertSame(inventoryTest,inventory2);
    }

    /**
     * Test method for {@link Inventory#take(String)} :
     * This method supposed to find a requested book by it's name(using the String in the header).
     * the method checks if a book is available= it's amount in the inventory is at least 1.
     *if so- it will reduce the amount by 1 and will return SUCCESSFULLY_TAKEN result.
     * else- it will return NOT_IN_STOCK result.
     */
    @Test
    public void take() {
        String bookName=otherBookInventory[2].getBookTitle();
        OrderResult result=inventoryTest.take(bookName);//if the book exist, it should decrease its amount by one
        assertEquals(result,OrderResult.SUCCESSFULLY_TAKEN);
        //now this book quantity in inventory is 0
        result=inventoryTest.take(bookName);
        assertEquals(result,OrderResult.NOT_IN_STOCK);
    }

    /**
     * Test method for {@link Inventory#checkAvailabiltyAndGetPrice(String)} ()} :
     *the method checks if a book is available= it's amount in the inventory is at least 1.
     *if a book is available it will return it's price
     * else- it will return -1.
     */
    @Test
    public void checkAvailabiltyAndGetPrice() {
        assertEquals(-1, inventoryTest.checkAvailabiltyAndGetPrice(otherBookInventory[0].getBookTitle()));// check if for a book that doesn't exist, we get the expected result (true)
        assertEquals(60, inventoryTest.checkAvailabiltyAndGetPrice(otherBookInventory[1].getBookTitle())); // positive check
    }

    /**
     * This method "undoes" what the @Before method did.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        inventoryTest = null; // after the function completed, inventory should be null
    }
}


