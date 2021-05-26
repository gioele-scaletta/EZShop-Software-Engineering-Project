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

    /*
    @Test
    public boolean ApplyDiscountToSaleProduct(){

        SaleTransactionImpl sale= new SaleTransactionImpl(1, "INPROGRESS", null, 0.0, 0.0, null, null, null);
        TicketEntryImpl t = new TicketEntryImpl("6291041500213", "ABC", 0, 1.99, 0.0);
        List<TicketEntry> l=new ArrayList<>();
        l.add(t);
        sale.setEntries(l);
        ProductTypeImpl p= new ProductTypeImpl(2, "6291041500213", "ABC", 1.99, "ciao");

        sale.EditProductInSale(p, 3);
        Integer amount=sale.getPrice();
        sale




    }


    private Double calculateCurrentAmount()

    public boolean ApplyDiscountToSaleAll(Double disc)

    public double PaySaleAndReturnChange(Double amount, Boolean method)

    public Integer getProductQuantity(ProductTypeImpl productType)

    public void updateProductQuantity(ProductTypeImpl productType, Integer quantity)

    getEntries

    setEntries
    */
}
