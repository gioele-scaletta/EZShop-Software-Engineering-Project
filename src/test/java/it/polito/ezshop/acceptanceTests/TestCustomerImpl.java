package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class TestCustomerImpl {

    private CustomerImpl c;

    @Test
    public void testCustomerConstructorGetters() {

        // Constructor
        c = new CustomerImpl("Name1", "0000000001", 1, 5);

        // Customer name
        c.setCustomerName("Name2");
        assertEquals (c.getCustomerName(), "Name2");

        // Customer card
        c.setCustomerCard("0123456789");
        assertEquals (c.getCustomerCard(), "0123456789");

        // Customer id
        c.setId(2);
        assertTrue (c.getId() == 2);

        // Customer points
        c.setPoints(10);
        assertTrue (c.getPoints() == 10);
    }
}
