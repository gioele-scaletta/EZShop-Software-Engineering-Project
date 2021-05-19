package it.polito.ezshop.acceptanceTests;

import java.time.LocalDate;
import it.polito.ezshop.data.BalanceOperationImpl;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

public class testBalanceOperationImpl {

    BalanceOperationImpl b;

    @Before
    public void middleReset() {
        b = new BalanceOperationImpl(0,null,0.0,"CREDIT");
    }

    @Test
    public void testSetBalanceId() {
        b.setBalanceId(12);
        assertTrue(b.getBalanceId()==12);
    }

    @Test
    public void testSetDate() {
        LocalDate l = LocalDate.of(2021, 5, 19);
        b.setDate(l);
        assertTrue(b.getDate().equals(l));
        b.setDate(null);
        assertNull(b.getDate());
    }

    @Test
    public void testSetMoney() {
        b.setMoney(12.50);
        assertTrue(b.getMoney() == 12.50);
    }

    @Test(expected = IllegalArgumentException.class, NullPointerException.class)
    public void testSetType() {
        b.setType("CREDIT");
        assertTrue(b.getType().equals("CREDIT"));
        b.setType("DEBIT");
        assertTrue(b.getType().equals("DEBIT"));
        b.setType(null);
        assertNull(b.getType());
        assertThrows(IllegalArgumentException.class, () -> b.setType(""));
        assertThrows(IllegalArgumentException.class, () -> b.setType("rAnDoM VaLuE"));
    }

}
