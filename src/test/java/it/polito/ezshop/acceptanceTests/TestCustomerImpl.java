package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.CustomerImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCustomerImpl {

    private CustomerImpl c;

        @Before
        public void setup() {
            c = new CustomerImpl("Name1", "0000000001", 1, 5);
        }

        @Test
        public void testSetCustomerName() {
            c.setCustomerName("Name2");
            assertEquals (c.getCustomerName(), "Name2");
            c.setCustomerName(null);
            assertEquals (c.getCustomerName(), "Name2");
        }

        @Test
        public void testSetCustomerCard() {
            c.setCustomerCard("0123456789");
            assertEquals (c.getCustomerCard(), "0123456789");
            c.setCustomerCard(null);
            assertEquals (c.getCustomerCard(), "0123456789");
        }

        @Test
        public void testSetCustomerId() {
            c.setId(2);
            assertTrue (c.getId().equals(2));
            c.setId(null);
            assertTrue (c.getId().equals(2));
        }

        @Test
        public void testSetCustomerPoints() {
            c.setPoints(10);
            assertTrue (c.getPoints().equals(10));
            c.setPoints(null);
            assertTrue (c.getPoints().equals(10));
        }
}
