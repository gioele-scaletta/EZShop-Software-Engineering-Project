package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.EZShop;
import it.polito.ezshop.data.*;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class testReturnTransactionImpl {

    ReturnTransactionImpl r;

    //Executed before each test
    @Before
    public void middleReset() {
        r = new ReturnTransactionImpl(1,null,null,null,0.0,null,null);
    }

    @Test
    public void testSetReturnId() {
        r.setReturnId(100);
        assertTrue(r.getReturnId().equals(100));
        r.setReturnId(null);
        assertNull(r.getReturnId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetState() {
        r.setState("INPROGRESS");
        assertTrue(r.getState().equals("INPROGRESS"));
        r.setState("CLOSED");
        assertTrue(r.getState().equals("CLOSED"));
        r.setState("PAYED");
        assertTrue(r.getState().equals("PAYED"));
        r.setState(null);
        assertTrue(r.getState().equals(null));
        assertThrows(IllegalArgumentException.class, () -> r.setState(""));
        assertThrows(IllegalArgumentException.class, () -> r.setState("rAnDoM VaLuE"));
    }

    @Test
    public void testSetAmount() {
        r.setAmount(23.90);
        assertTrue(r.getAmount().equals(23.90));
        r.setAmount(null);
        assertTrue(r.getAmount().equals(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPaymentType() {
        r.setPaymentType("CARD");
        assertTrue(r.getPaymentType().equals("CARD"));
        r.setPaymentType("CASH");
        assertTrue(r.getPaymentType().equals("CASH"));
        r.setState(null);
        assertTrue(r.getPaymentType().equals(null));
        assertThrows(IllegalArgumentException.class, () -> r.setPaymentType(""));
        assertThrows(IllegalArgumentException.class, () -> r.setPaymentType("rAnDoM VaLuE"));
    }
}
