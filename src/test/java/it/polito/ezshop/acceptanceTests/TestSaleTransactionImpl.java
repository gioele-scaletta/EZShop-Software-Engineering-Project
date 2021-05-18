package it.polito.ezshop.acceptanceTests;

import org.junit.Test;

import it.polito.ezshop.data.SaleTransactionImpl;

import static org.junit.Assert.*;

public class TestSaleTransactionImpl {
    @Test
    public void testSaleTransactionConstructorGetters() {
        SaleTransactionImpl saleTransaction = new SaleTransactionImpl(1, "INPROGRESS", null, 0.0, 0.0, null, null, null);

        saleTransaction.setTicketNumber(2);
        assertEquals(2, (int) saleTransaction.getTicketNumber());

        saleTransaction.setPrice(1.29);
        assertEquals(1.29, saleTransaction.getPrice(), 0);

        saleTransaction.setDiscountRate(0.20);
        assertEquals(0.20, saleTransaction.getDiscountRate(), 0);
    }

    @Test
    public void testIsInProgress() {
        SaleTransactionImpl saleTransaction = new SaleTransactionImpl(1, "INPROGRESS", null, 0.0, 0.0, null, null, null);

        assertTrue(saleTransaction.isInProgress());
        assertFalse(saleTransaction.isPayed());
        assertFalse(saleTransaction.isClosed());
    }

    @Test
    public void testIsPayed() {
        SaleTransactionImpl saleTransaction = new SaleTransactionImpl(1, "PAYED", null, 0.0, 0.0, null, null, null);

        assertFalse(saleTransaction.isInProgress());
        assertTrue(saleTransaction.isPayed());
        assertFalse(saleTransaction.isClosed());
    }

    @Test
    public void testIsClosed() {
        SaleTransactionImpl saleTransaction = new SaleTransactionImpl(1, "CLOSED", null, 0.0, 0.0, null, null, null);

        assertFalse(saleTransaction.isInProgress());
        assertFalse(saleTransaction.isPayed());
        assertTrue(saleTransaction.isClosed());
    }
}
