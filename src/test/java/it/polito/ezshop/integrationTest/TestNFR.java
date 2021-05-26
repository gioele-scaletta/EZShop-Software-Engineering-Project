package it.polito.ezshop.integrationTest;

import it.polito.ezshop.data.*;
import it.polito.ezshop.model.*;
import it.polito.ezshop.exceptions.*;
import static org.junit.Assert.*;

import org.junit.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class TestNFR {

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

    @Test(timeout = 500)
    public void testTimeDefineDeleteCustomer(){
        try {

            Integer id = ezshop.defineCustomer("Name");
            ezshop.deleteCustomer(id);

        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testTimeGetCustomer(){
        try {

            Integer id = ezshop.defineCustomer("Name");
            ezshop.getCustomer(id);

        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testTimeGetAllCustomers(){
        try {

            ezshop.defineCustomer("Name1");
            ezshop.defineCustomer("Name2");
            ezshop.defineCustomer("Name3");
            ezshop.getAllCustomers();

        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testTimeModifyCustomer(){
        try {

            Integer id = ezshop.defineCustomer("Name");
            ezshop.modifyCustomer(id, "Name1","");
            ezshop.modifyCustomer(id, "Name1","0000000010");
            ezshop.modifyCustomer(id, "Name","0000000010");
            ezshop.modifyCustomer(id, "Name","0000000011");
            ezshop.modifyCustomer(id, "Name2","0000000001");
            ezshop.modifyCustomer(id, "Name3",null);

        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testTimeCreateAttachCardToCustomer(){
        try {

            Integer id = ezshop.defineCustomer("Name");
            String card = ezshop.createCard();
            ezshop.attachCardToCustomer(card, id);

        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(timeout = 500)
    public void testTimeModifyPointsOnCard(){
        try {

            Integer id = ezshop.defineCustomer("Name");
            ezshop.attachCardToCustomer("0000000001", id);
            ezshop.modifyPointsOnCard("0000000001", 5);

        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }


    @Test
    public void testNF4isValidCode(){
        ProductTypeImpl prod=null;
        prod= new ProductTypeImpl (2, "b", "c", 0.3, "ciao");
        assertTrue(prod.isValidCode("5701234567899"));
        assertFalse(prod.isValidCode("111111111111"));
        assertFalse(prod.isValidCode("5701234a67899"));
        assertFalse(prod.isValidCode("570167899"));


        assertFalse(prod.isValidCode(""));
        assertFalse(prod.isValidCode(null));
    }


    @Test
    public void testNFR5isValidCreditCard(){
        assertTrue(ezshop.isValidCreditCard("12345674"));
        assertTrue(ezshop.isValidCreditCard("4444333322221111"));
        assertTrue(ezshop.isValidCreditCard("4716258050958645"));
        assertFalse(ezshop.isValidCreditCard("13245674"));
        assertFalse(ezshop.isValidCreditCard("4444333322221110"));
        assertFalse(ezshop.isValidCreditCard(""));
        assertFalse(ezshop.isValidCreditCard(null));

    }



    @Test
    public void testCustomerCard10digits() {
        try{

            String card = ezshop.createCard();
            Integer lenghtExpected = 10;

            // Check if card is a string of length 10
            assertTrue(card.length() == lenghtExpected);

            // Check if it is a number. If it is not a number, an error is thrown
            Integer.parseInt(card);


            Integer id = ezshop.defineCustomer("Name");

            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.modifyCustomer(id, "Name", "123456789");
            });

            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.modifyCustomer(id, "Name", "abcdefghij");
            });

            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.attachCardToCustomer("123456789", id);
            });

            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.attachCardToCustomer("abcdefghij", id);
            });

            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.modifyPointsOnCard("123456789", 5);
            });

            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.modifyPointsOnCard("abcdefghij", 5);
            });


        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


}
