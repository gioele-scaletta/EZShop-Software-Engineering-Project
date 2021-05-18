package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class testProductTypeImpl {

    ProductTypeImpl prod;
    Integer prodId=2;
    String barCode="5701234567899";
    String description="spaghetti barilla";
    Double sellPrice=5.0;
    Integer quantity=10;
    String notes="nota";
    Integer aisleId=3;
    String rackId="b";
    Integer levelID =6;
    String location=aisleId+"-"+rackId+"-"+levelID;
    Double discountRate=0.6;

    @Test
    public void testProductConstructorGetters(){
        prod =new ProductTypeImpl(1, "a", "b",0.2, 3, 0.5, "ciao", 2, "a", 5 );


        prod.setId(prodId);
        assertTrue(prod.getId().equals(prodId));
        prod.setBarCode(barCode);
        prod.setProductDiscountRate(discountRate);
        assertTrue(prod.getProductDiscountRate().equals(0.6));
        assertTrue(prod.getBarCode().equals(barCode));
        prod.setProductDescription(description);
        assertTrue(prod.getProductDescription().equals(description));
        prod.setPricePerUnit(sellPrice);
        assertTrue(prod.getPricePerUnit().equals(sellPrice));
        prod.setQuantity(quantity);
        prod.updateProductQuantity(-5);
        assertTrue(prod.getQuantity().equals(quantity-5));
        prod.setNote(notes);
        assertTrue(prod.getNote().equals(notes));
        prod.setLocation(location);
        assertTrue(prod.getLocation().equals(location));
        assertTrue(aisleId.equals(prod.extractAisleId(location)));
        assertTrue(rackId.equals(prod.extractRackId(location)));
        assertTrue(levelID.equals(prod.extractLevelId(location)));




    }


    @Test
    public void testisValidCode (){
        prod =new ProductTypeImpl(prodId, barCode, description,sellPrice, quantity, discountRate, notes, aisleId, rackId, levelID );

        assertTrue(prod.isValidCode("5701234567899"));
        assertFalse(prod.isValidCode("111111111111"));
        assertFalse(prod.isValidCode("5701234a67899"));
        assertFalse(prod.isValidCode("570167899"));
        assertFalse(prod.isValidCode(""));
        assertFalse(prod.isValidCode(null));
    }

    @Test
    public void testisValidLocation (){

        prod =new ProductTypeImpl(prodId, barCode, description,sellPrice, quantity, discountRate, notes, aisleId, rackId, levelID );

        assertTrue(prod.isValidLocation("3-a-2"));
        assertTrue(prod.isValidLocation(location));
        assertFalse(prod.isValidLocation("a-2-1"));
        assertFalse(prod.isValidLocation("0-2-b"));
        assertTrue(prod.isValidLocation(""));
        assertTrue(prod.isValidLocation(null));

    }

}
