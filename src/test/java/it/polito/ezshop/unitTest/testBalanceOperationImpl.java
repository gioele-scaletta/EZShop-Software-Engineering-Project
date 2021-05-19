package it.polito.ezshop.unitTest;

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
        assertFalse(b.getBalanceId()==28);
    }

    @Test
    public void testSetDate() {
        LocalDate l = LocalDate.of(2021, 5, 19);
        b.setDate(l);
        assertTrue(b.getDate().equals(l));
        b.setDate(null);
        assertTrue(b.getDate().equals(l));
    }

    @Test
    public void testSetMoney() {
        b.setMoney(12.50);
        assertTrue(b.getMoney() == 12.50);
        assertFalse(b.getMoney()== 0.12);
    }

    @Test
    public void testSetType() {
        b.setType("CREDIT");
        assertTrue(b.getType().equals("CREDIT"));
        b.setType("DEBIT");
        assertTrue(b.getType().equals("DEBIT"));
        b.setType(null);
        assertTrue(b.getType().equals("DEBIT"));
        assertThrows(IllegalArgumentException.class,()->b.setType(""));
        assertThrows(IllegalArgumentException.class,()->b.setType("random Value"));
    }
}
