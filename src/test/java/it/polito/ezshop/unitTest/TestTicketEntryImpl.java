package it.polito.ezshop.unitTest;

import org.junit.Before;
import org.junit.Test;

import it.polito.ezshop.data.TicketEntryImpl;

import static org.junit.Assert.*;

public class TestTicketEntryImpl {
    private TicketEntryImpl ticketEntry;

    @Before
    public void constructor() {
        ticketEntry = new TicketEntryImpl("6291041500213", "ABC", 2, 1.99, 0.0);
    }

    @Test
    public void testSetBarCode() {
        ticketEntry.setBarCode("5701234567899");
        assertEquals("5701234567899", ticketEntry.getBarCode());

        ticketEntry.setBarCode("5701234567899");
        ticketEntry.setBarCode(null);
        assertEquals("5701234567899", ticketEntry.getBarCode());
    }

    @Test
    public void testSetProductDescription() {
        ticketEntry.setProductDescription("ZZZ");
        assertEquals("ZZZ", ticketEntry.getProductDescription());

        ticketEntry.setProductDescription("ZZZ");
        ticketEntry.setProductDescription(null);
        assertEquals("ZZZ", ticketEntry.getProductDescription());
    }

    @Test
    public void testSetAmount() {
        ticketEntry.setAmount(10);
        assertEquals(10, ticketEntry.getAmount());
    }

    @Test
    public void testSetPricePerUnit() {
        ticketEntry.setPricePerUnit(2.49);
        assertEquals(2.49, ticketEntry.getPricePerUnit(), 0);
    }

    @Test
    public void testSetDiscountRate() {
        ticketEntry.setDiscountRate(0.50);
        assertEquals(0.50, ticketEntry.getDiscountRate(), 0);
    }
}
