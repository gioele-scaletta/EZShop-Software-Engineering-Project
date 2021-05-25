package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.model.*;
import it.polito.ezshop.exceptions.*;

import org.junit.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


public class testIntegrationReturnTransactionImpl {

    private static EZShop ezshop;

    @Test
    public void testSetBalanceOperation() {
        ReturnTransactionImpl r = new ReturnTransactionImpl(1,null,null,null,0.0,null,null);
        BalanceOperationImpl b = new BalanceOperationImpl(1,null,10,"CREDIT");

        r.setBalanceOperation(b);
        assertTrue(r.getBalanceOperation().getType() == "CREDIT");
        assertTrue(r.getBalanceOperation().getMoney() == 10);

        r.setBalanceOperation(null);
        assertTrue(r.getBalanceOperation().getType() == "CREDIT");
        assertTrue(r.getBalanceOperation().getMoney() == 10);
    }


    @Test
    public void testSetReturnProducts() {
        ReturnTransactionImpl r = new ReturnTransactionImpl(1,null,null,null,0.0,null,null);
        Map<ProductTypeImpl, Integer> m = new HashMap<>();

        ProductTypeImpl pr1 = new ProductTypeImpl(1,"5701234567899","none",0.50,10,0.0,"none",0,"empty",0);
        ProductTypeImpl pr2 = new ProductTypeImpl(1,"9780072125757","none2",0.70,120,0.0,"none",0,"empty",0);

        m.put(pr1,1);
        m.put(pr2,2);

        r.setReturnProducts(m);

        assertTrue(r.getReturnProducts().containsKey(pr1));
        assertTrue(r.getReturnProducts().containsKey(pr2));

    }

}
