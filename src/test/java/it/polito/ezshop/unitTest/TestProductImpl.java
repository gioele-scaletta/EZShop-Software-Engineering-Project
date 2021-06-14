package it.polito.ezshop.unitTest;

import it.polito.ezshop.model.CustomerImpl;
import it.polito.ezshop.model.ProductImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestProductImpl {

    private ProductImpl p;

    @Before
    public void setup() {
        p = new ProductImpl();
    }

    @Test
    public void testIsValidRFID() {

        // RFID is valid
        boolean valid1 = p.isValidRFID("000000001000");

        // RFID is equal to null
        boolean valid2 = p.isValidRFID(null);

        // The length is not correct
        boolean valid3 = p.isValidRFID("00000000100");

        // RFID does not contain an integer
        boolean valid4 = p.isValidRFID("00000000100a");

        // RFID is empty
        boolean valid5 = p.isValidRFID("");

        // Asserts
        assertTrue(valid1);
        assertFalse(valid2);
        assertFalse(valid3);
        assertFalse(valid4);
        assertFalse(valid5);

    }

    @Test
    public void testNextRFID() {

        // The operation is successful

        String next1 = p.nextRFID("000000001009");
        String expected1 = "000000001010";

        // RFID is not valid
        String next2 = p.nextRFID(null);
        String expected2 = null;
        String next3 = p.nextRFID("");
        String expected3 = null;
        String next4 = p.nextRFID("00000000100a");
        String expected4 = null;
        String next5 = p.nextRFID("00000000100");
        String expected5 = null;

        // If it is the latest RFID, return first RFID
        String next6 = p.nextRFID("999999999999");
        String expected6 = "000000000000";

        // Asserts
        assertEquals(next1, expected1);
        assertEquals(next2, expected2);
        assertEquals(next3, expected3);
        assertEquals(next4, expected4);
        assertEquals(next5, expected5);
        assertEquals(next6, expected6);
    }
}
