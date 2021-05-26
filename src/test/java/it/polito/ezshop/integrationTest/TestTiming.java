package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.model.*;
import it.polito.ezshop.exceptions.*;
import static org.junit.Assert.*;

import org.junit.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class TestTiming {

    private static EZShop ezshop;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ezshop = new EZShop();
    }

    @AfterClass
    public static void cleanUpAfterClass(){
        ezshop.reset();
        ezshop.closeDB();
    }

    @Before
    public void setUp() throws Exception {
        ezshop.reset();
        //ezshop.logout();
        ezshop.createUser("admin","password","Administrator");
       ezshop.login("admin","password");
    }

    @Test(timeout = 500)
    public void testTimeLogin() {
        try {

            ezshop.login("admin", "password");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testTimeLogout() {
        try {
            ezshop.logout();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testTimeCreateDeleteUser() {
        try {
            Integer id1 = ezshop.createUser("user1","password", "Administrator");
            Boolean deleted1 = ezshop.deleteUser(id1);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    @Test(timeout = 500)
    public void testTimeGetUsers() {
        try {
            ezshop.createUser("admin","password","Administrator");
            ezshop.createUser("shopmanager","password","ShopManager");
            ezshop.createUser("cashier","password","Cashier");
            ezshop.getAllUsers();
            ezshop.getUser(1);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testTimeUpdateUserRights() {
        try {
            ezshop.createUser("admin","password","Administrator");

            ezshop.updateUserRights(4,"Cashier");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


    @Test(timeout = 500)
    public void testTimeCreateUpdateDeleteProductType() {
        try {
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            ezshop.updateProduct(id1,"spaghettini", "9780072125757", 2.5, "note" );
            ezshop.deleteProductType(id1);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


    @Test(timeout = 500)
    public void testTimeGetProductTypes() {
        try {
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            Integer id2= ezshop.createProductType("spaghettini", "9780072125757", 2.5, "note" );
            ezshop.getAllProductTypes();
            ezshop.getProductTypeByBarCode("5701234567899");
            ezshop.getProductTypesByDescription("spagh");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    @Test(timeout = 500)
    public void testTimeUpdateProduct() {
        try {
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            ezshop.updateQuantity(1,3);
            ezshop.updatePosition(1,"1-a-2");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testTimeOrders() {
        try {
            Integer id1= ezshop.createProductType("spaghetti", "9780072125757", 5.0, "nota" );
            //order one valid
            Integer oid1= ezshop.issueOrder("9780072125757",5,10);
            ezshop.payOrder(oid1);
            ezshop.payOrderFor("9780072125757",5,10);
            ezshop.recordOrderArrival(oid1);
            ezshop.getAllOrders();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testTimeRecordBalanceUpdate() {
        try {
            ezshop.recordBalanceUpdate(50);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testTimeGetCreditsAndDebits() {
        try {
            ezshop.recordBalanceUpdate(50);
            ezshop.getCreditsAndDebits(null,null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testComputeBalance() {
        try {
            ezshop.recordBalanceUpdate(50);
            ezshop.computeBalance();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }






}
