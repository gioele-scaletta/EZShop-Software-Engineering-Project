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
