package it.polito.ezshop.acceptanceTests;

import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.data.SaleTransactionImpl;

import static org.junit.Assert.*;

public class TestSaleTransactionImpl {
    private SaleTransactionImpl saleTransaction;

    @Before
    public void constructor() {
        saleTransaction = new SaleTransactionImpl(1);
    }

    @Test
    public void testSetTicketNumber() {
        saleTransaction.setTicketNumber(10);
        assertEquals(Integer.valueOf(10), saleTransaction.getTicketNumber());

        saleTransaction.setTicketNumber(10);
        saleTransaction.setTicketNumber(null);
        assertEquals(Integer.valueOf(10), saleTransaction.getTicketNumber());
    }

    @Test
    public void testSetPrice() {
        saleTransaction.setPrice(4.99);
        assertEquals(4.99, saleTransaction.getPrice(), 0);
    }

    @Test
    public void testSetDiscountRate() {
        saleTransaction.setDiscountRate(0.15);
        assertEquals(0.15, saleTransaction.getDiscountRate(), 0);
    }

    @Test
    public void testIsInProgress() {
        SaleTransactionImpl saleTransaction1 = new SaleTransactionImpl(1, "INPROGRESS", null, 0.0, 0.0, null, null, null);
        SaleTransactionImpl saleTransaction2 = new SaleTransactionImpl(1, "PAYED", null, 0.0, 0.0, null, null, null);
        SaleTransactionImpl saleTransaction3 = new SaleTransactionImpl(1, "CLOSED", null, 0.0, 0.0, null, null, null);

        assertTrue(saleTransaction1.isInProgress());

        assertFalse(saleTransaction2.isInProgress());

        assertFalse(saleTransaction3.isInProgress());
    }

    @Test
    public void testIsPayed() {
        SaleTransactionImpl saleTransaction1 = new SaleTransactionImpl(1, "INPROGRESS", null, 0.0, 0.0, null, null, null);
        SaleTransactionImpl saleTransaction2 = new SaleTransactionImpl(1, "PAYED", null, 0.0, 0.0, null, null, null);
        SaleTransactionImpl saleTransaction3 = new SaleTransactionImpl(1, "CLOSED", null, 0.0, 0.0, null, null, null);

        assertFalse(saleTransaction1.isPayed());

        assertTrue(saleTransaction2.isPayed());

        assertFalse(saleTransaction3.isPayed());
    }

    @Test
    public void testIsClosed() {
        SaleTransactionImpl saleTransaction1 = new SaleTransactionImpl(1, "INPROGRESS", null, 0.0, 0.0, null, null, null);
        SaleTransactionImpl saleTransaction2 = new SaleTransactionImpl(1, "PAYED", null, 0.0, 0.0, null, null, null);
        SaleTransactionImpl saleTransaction3 = new SaleTransactionImpl(1, "CLOSED", null, 0.0, 0.0, null, null, null);

        assertFalse(saleTransaction1.isClosed());

        assertFalse(saleTransaction2.isClosed());

        assertTrue(saleTransaction3.isClosed());
    }
}
