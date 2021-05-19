package it.polito.ezshop.unitTest;

import it.polito.ezshop.model.ReturnTransactionImpl;
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
        assertTrue(r.getReturnId().equals(100));
    }

    @Test
    public void testSetState() {
        r.setState("INPROGRESS");
        assertTrue(r.getState().equals("INPROGRESS"));
        r.setState("CLOSED");
        assertTrue(r.getState().equals("CLOSED"));
        r.setState("PAYED");
        assertTrue(r.getState().equals("PAYED"));
        r.setState(null);
        assertTrue(r.getState().equals("PAYED"));
        assertThrows(IllegalArgumentException.class, () -> r.setState(""));
        assertThrows(IllegalArgumentException.class, () -> r.setState("rAnDoM VaLuE"));
    }

    @Test
    public void testSetAmount() {
        r.setAmount(23.90);
        assertTrue(r.getAmount().equals(23.90));
        r.setAmount(null);
        assertTrue(r.getAmount().equals(23.90));
    }

    @Test
    public void testSetPaymentType() {
        r.setPaymentType("CARD");
        assertTrue(r.getPaymentType().equals("CARD"));
        r.setPaymentType("CASH");
        assertTrue(r.getPaymentType().equals("CASH"));
        r.setState(null);
        assertTrue(r.getPaymentType().equals("CASH"));
        assertThrows(IllegalArgumentException.class, () -> r.setPaymentType(""));
        assertThrows(IllegalArgumentException.class, () -> r.setPaymentType("rAnDoM VaLuE"));
    }

    @Test
    public void testIsInProgress() {
        r.setState("INPROGRESS");
        assertTrue(r.isInProgress());
        r.setState("PAYED");
        assertFalse(r.isInProgress());
        r.setState("CLOSED");
        assertFalse(r.isInProgress());
    }

    @Test
    public void testIsPayed() {
        r.setState("INPROGRESS");
        assertFalse(r.isPayed());
        r.setState("PAYED");
        assertTrue(r.isPayed());
        r.setState("CLOSED");
        assertFalse(r.isPayed());
    }

    @Test
    public void testIsClosed() {
        r.setState("INPROGRESS");
        assertFalse(r.isClosed());
        r.setState("PAYED");
        assertFalse(r.isClosed());
        r.setState("CLOSED");
        assertTrue(r.isClosed());
    }
}
