package it.polito.ezshop.unitTest;
import it.polito.ezshop.model.OrderImpl;
import org.junit.Before;


import org.junit.Test;



import static org.junit.Assert.*;

public class testOrderImpl {

    String productCode="570123456789";
    double pricePerUnit=0.5;
    int quantity=5;
    String status="ok";
    Integer orderId=1;
    OrderImpl order=null;
    Integer balanceId=2;

    @Before
    public void constructor() {
    order=new OrderImpl( 3, "570123456788", 0.4, 4, "no", 3 );
    }

    @Test
    public void testSetBalanceId(){
        order.setBalanceId(balanceId);
        assertTrue(order.getBalanceId().equals(balanceId));
        order.setBalanceId(null);
        assertTrue(order.getBalanceId().equals(balanceId));
    }

   @Test
    public void testSetProductCode() {
       order.setProductCode(productCode);
       assertTrue(order.getProductCode().equals(productCode));
       order.setProductCode(null);
       assertTrue(order.getProductCode().equals(productCode));
   }

   @Test
    public void testSetPricePerUnit(){
        order.setPricePerUnit(pricePerUnit);
        assertEquals(pricePerUnit, order.getPricePerUnit(), 0);
    }

    @Test
    public void testSetQuantity() {
        order.setQuantity(quantity);
        assertEquals(quantity, order.getQuantity(), 0);
    }

    @Test
    public void testSetStatus(){
        order.setStatus(status);
        assertTrue(order.getStatus().equals(status));
        order.setStatus(null);
        assertTrue(order.getStatus().equals(status));
    }

    @Test
    public void testOrderID(){
        order.setOrderId(orderId);
        assertEquals(orderId, order.getOrderId(), 0);
        order.setOrderId(null);
        assertEquals(orderId, order.getOrderId(), 0);
    }






}
