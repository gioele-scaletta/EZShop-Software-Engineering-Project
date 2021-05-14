package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class testProductTypeImpl {

    ProductTypeImpl prod;

    @Test
    public void testProductImpl(){
        prod =new ProductTypeImpl(1, "a", "b",0.2, 3, 0.5, "ciao", 2, "a", 5 );



       //TO FINISH with get and set

    }


    @Test
    public void testisValidCode (){

        assertTrue(prod.isValidCode("5701234567899"));
        assertFalse(prod.isValidCode("111111111111"));
        assertFalse(prod.isValidCode("5701234a67899"));
        assertFalse(prod.isValidCode("570167899"));
        assertFalse(prod.isValidCode(""));
        assertFalse(prod.isValidCode(null));
    }

    @Test
    public void testisValidLocation (){

        assertTrue(prod.isValidLocation("3-a-2"));

        assertFalse(prod.isValidLocation("a-2-1"));
        assertFalse(prod.isValidLocation("0-2-b"));
        assertFalse(prod.isValidLocation(""));
        assertFalse(prod.isValidLocation(null));

    }

}
