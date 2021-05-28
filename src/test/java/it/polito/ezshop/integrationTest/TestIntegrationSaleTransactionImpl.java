package it.polito.ezshop.integrationTest;


import it.polito.ezshop.data.*;
import it.polito.ezshop.model.*;
 import it.polito.ezshop.exceptions.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TestIntegrationSaleTransactionImpl {




    @Test
    public void testEditProductInSale( ){

        SaleTransactionImpl sale= new SaleTransactionImpl(1, "INPROGRESS", null, 0.0, 0.0, null, null, null);
        TicketEntryImpl t = new TicketEntryImpl("6291041500213", "ABC", 0, 1.99, 0.0);
        List<TicketEntry> l=new ArrayList<>();
        l.add(t);
        sale.setEntries(l);
        ProductTypeImpl p= new ProductTypeImpl(2, "6291041500213", "ABC", 1.99, "ciao");

        assertTrue(sale.EditProductInSale(p, 3));

        assertEquals(sale.getListOfProductsEntries().get(p.getBarCode()).getAmount(),3);

        assertTrue(sale.EditProductInSale(p, -2));

        assertEquals(sale.getListOfProductsEntries().get(p.getBarCode()).getAmount(),1);

        assertTrue(sale.EditProductInSale(p, -1));

        assertNull(sale.getListOfProductsEntries().get(p.getBarCode()));

        assertTrue(sale.EditProductInSale(p,3));

        assertTrue(sale.getListOfProductsEntries().get(p.getBarCode()).getAmount()==3);

    }


    @Test
    public void testApplyDiscountToProductAndSale(){

        SaleTransactionImpl sale= new SaleTransactionImpl(1, "INPROGRESS", null, 0.0, 0.0, null, null, null);
        TicketEntryImpl t = new TicketEntryImpl("6291041500213", "ABC", 0, 2.0, 0.0);
        List<TicketEntry> l=new ArrayList<>();
        l.add(t);
        sale.setEntries(l);
        ProductTypeImpl p= new ProductTypeImpl(2, "6291041500213", "ABC", 2.0, "ciao");

        sale.EditProductInSale(p, 3);
        Double amount=sale.getPrice();
        sale.ApplyDiscountToSaleProduct(0.6,p);

        assertEquals(sale.getPrice(),amount*0.4,0);

       amount= sale.getPrice();


        ProductTypeImpl p1= new ProductTypeImpl(3, "6291041500212", "ABC", 4.0, "ciao");

        sale.EditProductInSale(p1,2);
        sale.ApplyDiscountToSaleProduct(0.7,p1);

        amount=amount+2*4*0.3;


        assertEquals(sale.getPrice(),amount,0);

        amount= sale.getPrice();

        sale.ApplyDiscountToSaleAll(0.3);

        assertEquals(amount*0.7, sale.getPrice(),0);


    }

    @Test
    public void testPaySaleAndReturnChange(){
        SaleTransactionImpl sale= new SaleTransactionImpl(1, "INPROGRESS", null, 10.0, 0.0, null, null, null);

        assertEquals(1.0,sale.PaySaleAndReturnChange(11.0,true),0);
        assertTrue(sale.getPayString().equals("CASH"));
        assertEquals(-1, sale.PaySaleAndReturnChange(3.0,true),0);
        assertEquals(3, sale.PaySaleAndReturnChange(13.0,false),0);
        assertTrue(sale.getPayString().equals("CARD"));
    }


    @Test
    public void testSetEntries(){

        SaleTransactionImpl sale= new SaleTransactionImpl(1, "INPROGRESS", null, 0.0, 0.0, null, null, null);
        TicketEntryImpl t = new TicketEntryImpl("6291041500213", "ABC", 0, 2.0, 0.0);
        TicketEntryImpl t1 = new TicketEntryImpl("6291041500212", "ABC", 0, 4.0, 0.0);
        List<TicketEntry> l=new ArrayList<>();
        l.add(t);
        l.add(t1);
        sale.setEntries(l);
        List<TicketEntry> lc = new ArrayList<>();
        lc=sale.getEntries();
        int i=0;
        while(i<l.size()) {
            assertEquals(l.get(i).getBarCode(),lc.get(i).getBarCode());
            i++;
        }

   }


}
