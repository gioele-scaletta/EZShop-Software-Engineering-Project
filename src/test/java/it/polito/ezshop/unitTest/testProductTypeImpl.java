package it.polito.ezshop.unitTest;


import it.polito.ezshop.model.ProductTypeImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


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
    
    @Before
    public void constructor() {
        prod= new ProductTypeImpl (2, "b", "c", 0.3, "ciao");
        prod= new ProductTypeImpl ("s", "b", 0.1, 0.2);
        prod= new ProductTypeImpl(1, "a", "b",0.2, 3, 0.5, "ciao", 2, "a", 5 );

    }

    @Test
    public void testSetId (){
        prod.setId(prodId);
        assertTrue(prod.getId().equals(prodId));
        prod.setId(null);
        assertTrue(prod.getId().equals(prodId));
    }

    @Test
    public void testSetBarCode (){
        prod.setBarCode(barCode);
        assertTrue(prod.getBarCode().equals(barCode));
        prod.setBarCode(null);
        assertTrue(prod.getBarCode().equals(barCode));
    }

    @Test
    public void testSetProductDiscountRate (){
        prod.setProductDiscountRate(discountRate);
        assertTrue(prod.getProductDiscountRate().equals(0.6));
        prod.setProductDiscountRate(null);
        assertEquals(0.6, prod.getProductDiscountRate(), 0);
    }

    @Test
    public void testSetProductDescription (){
        prod.setProductDescription(description);
        assertTrue(prod.getProductDescription().equals(description));
        prod.setProductDescription(null);
        assertTrue(prod.getProductDescription().equals(description));
    }

    @Test
    public void testSetPricePerUnit(){

        prod.setPricePerUnit(sellPrice);
        assertTrue(prod.getPricePerUnit().equals(sellPrice));
        prod.setPricePerUnit(null);
        assertEquals(sellPrice, prod.getPricePerUnit(), 0);
    }

    @Test
    public void testSetQuantity (){
        prod.setQuantity(quantity);
        prod.updateProductQuantity(-5);
        assertTrue(prod.getQuantity().equals(quantity-5));
        prod.setQuantity(null);
        assertTrue(prod.getQuantity().equals(quantity-5));
    }

    @Test
    public void testSetNote(){
        prod.setNote(notes);
        assertTrue(prod.getNote().equals(notes));
        prod.setNote(null);
        assertTrue(prod.getNote().equals(notes));
    }

    @Test
    public void testSetLocation (){
        prod.setLocation(location);
        assertTrue(prod.getLocation().equals(location));
        assertTrue(aisleId.equals(prod.extractAisleId(location)));
        assertTrue(rackId.equals(prod.extractRackId(location)));
        assertTrue(levelID.equals(prod.extractLevelId(location)));

        prod.setLocation(null);
        assertTrue(prod.getLocation().equals(""));
        prod.setLocation(null);
        assertTrue(prod.getLocation().equals(""));

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
        assertTrue(prod.isValidLocation(location));
        assertFalse(prod.isValidLocation("a-2-1"));
        assertFalse(prod.isValidLocation("0-2-b"));
        assertTrue(prod.isValidLocation(""));
        assertTrue(prod.isValidLocation(null));

    }

}
