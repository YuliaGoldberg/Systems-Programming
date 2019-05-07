package java;

import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * This is a Unit Test for the {@link Future} Class.
 *
 * @author noytourg, yuliagol
 *
 */
public class FutureTest {
    /** Object under test.*/
    private Future<String> futureTest;

    /**
     * Set up for a test.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        futureTest= new Future<>();
    }

    /**
     * Test method for {@link Future#get()} :
     *Retrieves the result of the operation
     */
    @Test
    public void get() {
        futureTest.resolve("success");
        String res="success";
        assertEquals(res,futureTest.get()); // make sure resolve updated the result
    }
    /**
     * Test method for {@link Future#resolve(Object)}:
     *this method sets the result of the operation to a new value
     */
    @Test
    public void resolve() {
        futureTest.resolve("success");
        assertTrue(futureTest.isDone());//make sure "resolve" works properly, which means there was a completion of the computation
    }

    /**
     * Test method for {@link Future#isDone()} :
     *returns true if this object has been resolved.
     *if the object didn't resolve yet- it will return false.
     * else-it will return true.
     */
    @Test
    public void isDone() {
        assertFalse(futureTest.isDone()); // futureTest not yet resolved
        futureTest.resolve("success");
        assertTrue(futureTest.isDone()); // make sure "isDone" works properly after "futureTest" was resolved
    }
    /**
     * This method "undoes" what the @Before method did.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        futureTest = null;
    }

}
