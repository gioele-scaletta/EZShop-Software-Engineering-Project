package it.polito.ezshop.integrationTest;
import java.io.ByteArrayOutputStream;

import it.polito.ezshop.data.*;
import it.polito.ezshop.model.*;
import it.polito.ezshop.exceptions.*;
import static org.junit.Assert.*;

import org.junit.*;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class TestIntegrationEZShop {

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
       // ezshop = new EZShop();
        ezshop.reset();
        //ezshop.logout();
        ezshop.createUser("admin","password","Administrator");
        ezshop.createUser("shopmanager","password","ShopManager");
        ezshop.createUser("cashier","password","Cashier");
    }

    @Test
    public void testLogin(){
        try {
            User u= ezshop.login("admin","password");
            User u1= ezshop.login("ciso", "nonesisto");

            assertTrue(u.getRole().equals("Administrator"));
            assertTrue(u.getUsername().equals("admin"));
            assertTrue(u.getPassword().equals("password"));
            assertNull(u1);
            // Empty username
            assertThrows(InvalidUsernameException.class, () -> {
                ezshop.login("","password");
            });

            // username is null
            assertThrows(InvalidUsernameException.class, () -> {
                ezshop.login( null, "password");
            });

            // Empty password
            assertThrows(InvalidPasswordException.class, () -> {
                ezshop.login("shopmanager","");
            });

            // password is null
            assertThrows(InvalidPasswordException.class, () -> {
                ezshop.login( "shopmanager", null );
            });





        }  catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testLogout(){
        try{
            assertFalse(ezshop.logout());
            ezshop.login("shopmanager","password");
            assertTrue(ezshop.logout());
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.deleteUser(1);
            });
        }  catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
/*
    @Test
    public void testReset(){
        ezshop.closeDB();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        ezshop.reset();
        assertTrue(outContent.toString().contains("Error with db"));
    }
*/
    @Test
    public void testCreateUser (){
        try {
            // First user
            Integer id1= ezshop.createUser("user1", "password", "Administrator");
            Integer expected1 = 4;

            // Second user
            Integer id2 = ezshop.createUser("user2","password2", "ShopManager");
            Integer expected2 = 5;

            //Third User
            Integer id4= ezshop.createUser("user3","password2", "Cashier");
            Integer expected4 = 6;

            // Username is not unique
            Integer id3 = ezshop.createUser("user1","password3","Administrator");
            Integer expected3 = -1;
            //MISSING DB PROBLEMS return -1

            // Asserts
            assertEquals (expected1, id1);
            assertEquals (expected2, id2);
            assertEquals (expected4, id4);
            assertEquals (expected3, id3);

            // Empty username
            assertThrows(InvalidUsernameException.class, () -> {
                ezshop.createUser("","password", "Cashier");
            });

            // username is null
            assertThrows(InvalidUsernameException.class, () -> {
                ezshop.createUser( null, "password", "Cashier" );
            });

            // Empty password
            assertThrows(InvalidPasswordException.class, () -> {
                ezshop.createUser("user5","", "Cashier");
            });

            // password is null
            assertThrows(InvalidPasswordException.class, () -> {
                ezshop.createUser( "user6", null, "Cashier" );
            });

            // Empty role
            assertThrows(InvalidRoleException.class, () -> {
                ezshop.createUser("user7","password", "");
            });

            // role is null
            assertThrows(InvalidRoleException.class, () -> {
                ezshop.createUser( "user8", "password", null );
            });

            // role is invalid
            assertThrows(InvalidRoleException.class, () -> {
                ezshop.createUser( "user9", "password", "cashier" );
            });




            ezshop.closeDB();

            assertThrows(RuntimeException.class, () -> {
                ezshop.createUser( "user9", "password", "Cashier" );
            });

            ezshop = new EZShop();


        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testDeleteUser(){
        try {
            ezshop.login("admin","password");

            // Delete user with id = id1
            Integer id1 = ezshop.createUser("user1","password", "Administrator");
            Boolean deleted1 = ezshop.deleteUser(id1);
            User usernull = ezshop.getUser(id1);

            // Delete user with id = id2
            Integer id2 = ezshop.createUser("user1","password", "Administrator");
            Boolean deleted2 = ezshop.deleteUser(id2);
            User usernull2 = ezshop.getUser(id2);

            // Customer with id = 3 does not exist
            Boolean deleted3 = ezshop.deleteUser(4);

            // Asserts
            assertTrue(deleted1);
            assertTrue(deleted2);
            assertFalse(deleted3);
            assertNull(usernull);
            assertNull(usernull2);

            // Customer id is null
            assertThrows(InvalidUserIdException.class, () -> {
                ezshop.deleteUser(null);
            });

            // Customer id equal to 0
            assertThrows(InvalidUserIdException.class, () -> {
                ezshop.deleteUser(0);
            });

            // Customer id less than 0
            assertThrows(InvalidUserIdException.class, () -> {
                ezshop.deleteUser(-1);
            });


            Integer id3 = ezshop.createUser("user3","password2", "Cashier");
            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.deleteUser(id3);
            });

            ezshop.login("shopmanager", "password"); //Shopmanager
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.deleteUser(id3);
            });

            ezshop.logout();
            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.deleteUser(id3);
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


    public boolean compareUsers(User u1, User u2){
        if(u1.getId().equals(u2.getId()) &&
                u1.getId().equals(u2.getId()) &&
                u2.getRole().equals(u1.getRole()) &&
                u1.getPassword().equals(u2.getPassword()))
            return true;
        return false;
    }

    @Test
    public void testGetAllusers(){
        try{
            //Check that if there are no users empty list is returned
            List<User> list=new ArrayList<>();
            ezshop.reset();
            //NON FUNZIONA PERCHE OVVIAMENTE LANCIA UNAUTHORIZED
            //assertEquals(ezshop.getAllUsers().size(),0);

            //Check correspondence between list got from db and list created manually
            ezshop.createUser("admin","password","Administrator");
            ezshop.createUser("shopmanager","password","ShopManager");
            ezshop.createUser("cashier","password","Cashier");
            User u1= new UserImpl(1, "admin","password","Administrator");
            User u2= new UserImpl(2, "shopmanager","password","ShopManager");
            User u3= new UserImpl(3, "cashier","password","Cashier");
            list.add(u1);
            list.add(u2);
            list.add(u3);
            ezshop.login("admin","password");

            List<User> listmp=new ArrayList<>();
            listmp=ezshop.getAllUsers();
            int i=0;
            while(i<list.size()) {
                assertTrue(compareUsers(list.get(i),listmp.get(i)));
                i++;
            }

            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getAllUsers();
            });

            ezshop.login("shopmanager", "password"); //Shopmanager
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getAllUsers();
            });

            ezshop.logout();
            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getAllUsers();
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetUser(){
        try {

            ezshop.login("admin", "password");

            // Get User with id = id1
            int id1= ezshop.createUser("user1", "password", "Administrator");
            User u1 = ezshop.getUser(id1);
            User expected1 = new UserImpl(id1, "user1", "password", "Administrator");


            // Get User with id = id2
            int id2= ezshop.createUser("user2", "password2", "Administrator");
            User u2 = ezshop.getUser(id2);
            User expected2 = new UserImpl(id2, "user2", "password2", "Administrator");

            // Customer with id = 3 does not exist
            User u3 = ezshop.getUser(10);

            // Asserts
            assertTrue(compareUsers(u1,expected1));
            assertTrue(compareUsers(u2,expected2));
            assertNull(u3);

            // Customer id is null
            assertThrows(InvalidUserIdException.class, () -> {
                ezshop.getUser(null);
            });

            // Customer id equal to 0
            assertThrows(InvalidUserIdException.class, () -> {
                ezshop.getUser(0);
            });

            // Customer id less than 0
            assertThrows(InvalidUserIdException.class, () -> {
                ezshop.getUser(-1);
            });


            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getUser(2);
            });

            ezshop.login("shopmanager", "password"); //Shopmanager
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getUser(2);
            });

            ezshop.logout();
            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getUser(2);
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testUpdateUserRights(){
        try{
            ezshop.login("admin","password");
            int id=ezshop.createUser("user1", "password","Administrator" );

            assertTrue(ezshop.updateUserRights(id,"Cashier"));
            assertTrue(ezshop.getUser(id).getRole().equals("Cashier"));
            assertFalse(ezshop.updateUserRights(9, "Administrator"));

            // Empty role
            assertThrows(InvalidRoleException.class, () -> {
                ezshop.updateUserRights(id, "");
            });

            // role is null
            assertThrows(InvalidRoleException.class, () -> {
                ezshop.updateUserRights( id,  null );
            });

            // role is invalid
            assertThrows(InvalidRoleException.class, () -> {
                ezshop.updateUserRights( id, "cashier" );
            });


            // User id is null
            assertThrows(InvalidUserIdException.class, () -> {
                ezshop.updateUserRights(null, "Cashier");
            });

            // User id equal to 0
            assertThrows(InvalidUserIdException.class, () -> {
                ezshop.updateUserRights(0,"Administrator");
            });

            // User id less than 0
            assertThrows(InvalidUserIdException.class, () -> {
                ezshop.updateUserRights(-1,"Administrator");
            });


            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.updateUserRights( id, "Cashier" );
            });

            ezshop.login("shopmanager", "password"); //Shopmanager
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.updateUserRights( id, "Cashier" );
            });

            ezshop.logout();
            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.updateUserRights( id, "Cashier" );
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }



    @Test
    public void testCreateProductType(){
        try {
            ezshop.login("shopmanager", "password");
            // First prod
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            Integer expected1 = 1;

            // Second prod
            Integer id2 =ezshop.createProductType("spaghettini", "9780072125757", 2.5, "note" );
            Integer expected2 = 2;

            //Third prod
            Integer id3=ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            Integer expected3 = -1;

            // Asserts
            assertEquals (expected1, id1);
            assertEquals (expected2, id2);
            assertEquals (expected3, id3);

            // Empty description
            assertThrows(InvalidProductDescriptionException.class, () -> {
                ezshop.createProductType("", "5012345678900", 2.5, "note" );
            });

            // description is null
            assertThrows(InvalidProductDescriptionException.class, () -> {
                ezshop.createProductType(null, "5012345678900", 2.5, "note" );
            });

            // Empty productcode
            assertThrows(InvalidProductCodeException.class, () -> {

                ezshop.createProductType("spaghettoni", "", 2.5, "note" );
            });


            // Null productcode
            assertThrows(InvalidProductCodeException.class, () -> {

                ezshop.createProductType("spaghettoni", null, 2.5, "note" );
            });

            // Not number productcode
            assertThrows(InvalidProductCodeException.class, () -> {

                ezshop.createProductType("spaghettoni", "50123456789a0", 2.5, "note" );
            });


            // Not number productcode
            assertThrows(InvalidProductCodeException.class, () -> {

                ezshop.createProductType("spaghettoni", "1234567890", 2.5, "note" );

            });

            // price1
            assertThrows(InvalidPricePerUnitException.class, () -> {
                ezshop.createProductType("spaghettoni", "5012345678900", -1, "note" );
            });

            // price2
            assertThrows(InvalidPricePerUnitException.class, () -> {
                ezshop.createProductType("spaghettoni", "5012345678900", 0, "note" );
            });

            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.createProductType("spaghettoni", "5012345678900", 3, "note" );
            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.createProductType("spaghettoni", "5012345678900", 3, "note" );
            });

        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testUpdateProduct(){
        try {
            ezshop.login("shopmanager", "password");
            // create test prod
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );

            // update prod not found
            boolean update=ezshop.updateProduct(2,"spaghettini", "9780072125757", 2.5, "note" );

            Integer id2=ezshop.createProductType("spaghettini", "9780072125757", 2.5, "note" );
            //update newbarcode already present
            boolean update2=ezshop.updateProduct(id2,"spaghettoni", "5701234567899", 2.5, "note" );

            //update success
            boolean update3=ezshop.updateProduct(id2,"spaghettoni", "5012345678900", 2.5, "note" );

            // Asserts
            assertTrue (update3);
            assertFalse (update2);
            assertFalse (update);

            //id les than 0
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.updateProduct(-1,"kindermaxi", "4012345678901", 2.5, "note" );
            });

            //id equal 0
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.updateProduct(0,"kindermaxi", "4012345678901", 2.5, "note" );
            });

            //id null
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.updateProduct(null,"kindermaxi", "4012345678901", 2.5, "note" );
            });

            // Empty description
            assertThrows(InvalidProductDescriptionException.class, () -> {
                ezshop.updateProduct(id1,"", "4012345678901", 2.5, "note" );
            });

            // description is null
            assertThrows(InvalidProductDescriptionException.class, () -> {
                ezshop.updateProduct(id1,null, "4012345678901", 2.5, "note" );
            });

            // Empty productcode
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.updateProduct(id1,"spaghettoni", "", 2.5, "note" );
            });


            // Null productcode
            assertThrows(InvalidProductCodeException.class, () -> {

                ezshop.updateProduct(id1,"spaghettoni", null, 2.5, "note" );
            });

            // Not number productcode
            assertThrows(InvalidProductCodeException.class, () -> {

                ezshop.updateProduct(id1,"spaghettoni", "50123456789a0", 2.5, "note" );
            });


            // Not valid productcode
            assertThrows(InvalidProductCodeException.class, () -> {

                ezshop.updateProduct(id1,"spaghettoni", "1234567890", 2.5, "note" );

            });

            // price1
            assertThrows(InvalidPricePerUnitException.class, () -> {
                ezshop.updateProduct(id1,"spaghettoni", "4012345678901", -1, "note" );
            });

            // price2
            assertThrows(InvalidPricePerUnitException.class, () -> {
                ezshop.updateProduct(id2,"spaghettoni", "4012345678901", 0, "note" );
            });

            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.updateProduct(id2,"spaghettoni", "4012345678901", 2, "note" );
            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.updateProduct(id2,"spaghettoni", "4012345678901", 2, "note" );
            });

        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testDeleteProductType(){
        try {
            ezshop.login("admin", "password");
            // create test prod
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );

            Integer id2=ezshop.createProductType("spaghettini", "9780072125757", 2.5, "note" );

            boolean delete=ezshop.deleteProductType(id1);
            ProductType p=ezshop.getProductTypeByBarCode("5701234567899");
            boolean delete2=ezshop.deleteProductType(id2);
            boolean delete1=ezshop.deleteProductType(id1);

            Integer id3= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );

            // Asserts
            assertTrue (delete);
            assertFalse (delete1);
            assertTrue(delete2);
            assertNull(p);


            //id les than 0
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.deleteProductType(-1 );
            });

            //id equal 0
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.deleteProductType(0);
            });

            //id null
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.deleteProductType(null);
            });

            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.deleteProductType(id3);
            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.deleteProductType(id3);
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetAllproductTypes() {


        try {
            ezshop.login("admin", "password");
            //Check that if there are no users empty list is returned
            List<ProductType> list = new ArrayList<>();

            assertEquals(ezshop.getAllProductTypes().size(),0);


            //Check correspondence between list got from db and list created manually
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            Integer id2= ezshop.createProductType("spaghettini", "9780072125757", 2.5, "note" );
            ProductType p1= new ProductTypeImpl(id1,  "5701234567899","spaghetti", 5.0, "nota" );
            ProductType p2= new ProductTypeImpl(id2,  "9780072125757","spaghettini", 2.5, "note" );
            list.add(p1);
            list.add(p2);



            List<ProductType> listmp = new ArrayList<>();
            listmp = ezshop.getAllProductTypes();
            int i = 0;
            while (i < list.size()) {
                assertTrue(compareProducts(list.get(i), listmp.get(i)));
                i++;
            }

            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getAllProductTypes();
            });

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    private boolean compareProducts(ProductType p1, ProductType p2) {
        if(p1.getId().equals(p2.getId()) &&
                p1.getBarCode().contentEquals(p2.getBarCode()) &&
                p1.getNote().equals(p2.getNote()) &&
                p1.getProductDescription().equals(p2.getProductDescription()) &&
                p1.getQuantity().equals(p2.getQuantity()) &&
                p1.getLocation().equals(p2.getLocation()) &&
                p1.getPricePerUnit().equals(p2.getPricePerUnit()) )




            return true;
        return false;
    }


    @Test
    public void testGetProductTypeByBarCode(){
        try{
            ezshop.login("shopmanager", "password");
            // create test prod

            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            Integer id2=ezshop.createProductType("spaghettini", "9780072125757", 2.5, "note" );

            ProductType p= new ProductTypeImpl(id1, "5701234567899","spaghetti",  5.0, "nota" );
            ProductType p1= new ProductTypeImpl(id2,  "9780072125757","spaghettini", 2.5, "note" );
            ProductType p2=ezshop.getProductTypeByBarCode("4012345678901");



            assertTrue(compareProducts(p,ezshop.getProductTypeByBarCode("5701234567899")));
            assertTrue(compareProducts(p1,ezshop.getProductTypeByBarCode("9780072125757")));
            assertNull(p2);

            // Empty productcode
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.getProductTypeByBarCode("");
            });


            // Null productcode
            assertThrows(InvalidProductCodeException.class, () -> {

                ezshop.getProductTypeByBarCode(null);
            });

            // Not number productcode
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.getProductTypeByBarCode("57a1234567899");
            });


            // Not valid productcode
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.getProductTypeByBarCode("5701234567");
            });

            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getProductTypeByBarCode("5701234567899");

            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getProductTypeByBarCode("5701234567899");

            });

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetProductByDescription(){
        try{
            ezshop.login("shopmanager", "password");
            // create test prod
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            Integer id2=ezshop.createProductType("spaghettini", "9780072125757", 2.5, "note" );
            Integer id3=ezshop.createProductType("kinder", "4012345678901", 2.5, "note" );

            List<ProductType> list = new ArrayList<>();

            ProductType p1= new ProductTypeImpl(id1, "5701234567899","spaghetti",  5.0, "nota" );
            ProductType p2= new ProductTypeImpl(id2, "9780072125757", "spaghettini", 2.5, "note" );
            ProductType p3= new ProductTypeImpl(id3, "4012345678901","kinder",  2.5, "note" );
            list.add(p1);
            list.add(p2);


            List<ProductType> listmp = new ArrayList<>();
            listmp = ezshop.getProductTypesByDescription("spaghet");
            int i = 0;
            while (i < list.size()) {
                assertTrue(compareProducts(list.get(i), listmp.get(i)));
                i++;
            }

            list.add(p3);
            listmp = ezshop.getProductTypesByDescription("");
            i = 0;
            while (i < list.size()) {
                assertTrue(compareProducts(list.get(i), listmp.get(i)));
                i++;
            }

            listmp = ezshop.getProductTypesByDescription(null);
            i = 0;
            while (i < list.size()) {
                assertTrue(compareProducts(list.get(i), listmp.get(i)));
                i++;
            }

            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getProductTypesByDescription("4012345678901");

            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getProductTypesByDescription("5701234567899");

            });

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testUpdateQuantity(){
        try{
            ezshop.login("shopmanager", "password");
            // create test prod
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            assertFalse(ezshop.updateQuantity(id1, 5));
            ezshop.updatePosition(id1,"1-a-2");

            assertTrue(ezshop.updateQuantity(id1, 5));
            assertTrue(ezshop.updateQuantity(id1, -2));
            assertTrue(ezshop.getProductTypeByBarCode("5701234567899").getQuantity().equals(3));
            assertFalse(ezshop.updateQuantity(id1, -6));
            assertFalse(ezshop.updateQuantity(2, 5));

            //id les than 0
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.updateQuantity(-2, 6);
            });

            //id equal 0
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.updateQuantity(0, 6);
            });

            //id null
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.updateQuantity(null, 6);
            });


            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.updateQuantity(id1, 6);

            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.updateQuantity(id1, 6);

            });

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testUpdatePosition(){
        try{

            ezshop.login("shopmanager", "password");
            // create test prod
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            assertTrue(ezshop.updatePosition(id1,"1-a-2"));
            assertTrue(ezshop.getProductTypeByBarCode("5701234567899").getLocation().equals("1-a-2"));
            assertFalse(ezshop.updatePosition(5,"1-a-2"));

            assertThrows(InvalidLocationException.class, () -> {
                ezshop.updatePosition(id1, "1-2-c");
            });
            assertThrows(InvalidLocationException.class, () -> {
                ezshop.updatePosition(id1, "a-a-c");
            });
            assertThrows(InvalidLocationException.class, () -> {
                ezshop.updatePosition(id1, "a-2-2");
            });
            assertThrows(InvalidLocationException.class, () -> {
                ezshop.updatePosition(id1, "a--c");
            });
            assertThrows(InvalidLocationException.class, () -> {
                ezshop.updatePosition(id1, "a2-e");
            });
            assertThrows(InvalidLocationException.class, () -> {
                ezshop.updatePosition(id1, "a2e");
            });

            //id les than 0
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.updatePosition(-2, "b-2-c");
            });

            //id equal 0
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.updatePosition(0, "b-2-c");
            });

            //id null
            assertThrows(InvalidProductIdException.class, () -> {
                ezshop.updatePosition(null, "b-2-c");
            });


            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.updatePosition(id1, "b-2-c");

            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.updatePosition(id1, "b-2-c");

            });

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testIssueOrder(){
        try{
            ezshop.login("shopmanager", "password");
            // create test prod
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );

            //order one valid
            Integer oid1= ezshop.issueOrder("9780072125757",5,10);
            //prder two inavlid
            Integer oid2= ezshop.issueOrder("5701234567899",5,10);


            Integer id2= ezshop.createProductType("spaghettioni", "9780072125757", 5.0, "nota" );

            //order 3 valid
            Integer oid3= ezshop.issueOrder("9780072125757",5,10);


            assertTrue(oid1.equals(-1));
            assertEquals(ezshop.getAllOrders().stream().filter(e -> e.getOrderId().equals(oid2) ||e.getOrderId().equals(oid3)).count(),2);


            // quantity less than p0
            assertThrows(InvalidQuantityException.class, () -> {
                ezshop.issueOrder( "9780072125757",-1, 3);;
            });

            // quantity 0
            assertThrows(InvalidQuantityException.class, () -> {
                ezshop.issueOrder( "9780072125757",0, 3);;
            });


            // price1
            assertThrows(InvalidPricePerUnitException.class, () -> {
                ezshop.issueOrder( "9780072125757",5, -1);;
            });

            // price2
            assertThrows(InvalidPricePerUnitException.class, () -> {
                ezshop.issueOrder( "9780072125757",5, 0 );
            });

            // Empty productcode
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.issueOrder("",5,0.5);
            });


            // Null productcode
            assertThrows(InvalidProductCodeException.class, () -> {

                ezshop.issueOrder(null,5,0.5);
            });

            // Not number productcode
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.issueOrder("57a1234567899",5,0.5);
            });


            // Not valid productcode
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.issueOrder("5701234567",5,0.5);
            });


            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.issueOrder("5701234567899",5,0.5);

            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.issueOrder("5701234567899",5,0.5);

            });

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testPayOrderFor(){
        try{
            ezshop.login("shopmanager", "password");
            // create test prod
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );

            //order one invalid no balance
            Integer oid0= ezshop.payOrderFor("5701234567899",5,10);

            ezshop.recordBalanceUpdate(200);
            //invalid order because of prod not present
            Integer oid1= ezshop.payOrderFor("9780072125757",5,10);
            //valid order n1
            Integer oid2= ezshop.payOrderFor("5701234567899",5,10);


            Integer id2= ezshop.createProductType("spaghettioni", "9780072125757", 5.0, "nota" );

            //order 3 valid
            Integer oid3= ezshop.payOrderFor("9780072125757",5,10);
            Double bal=ezshop.computeBalance();


            assertTrue(oid1.equals(-1));
            assertEquals(ezshop.getAllOrders().stream().filter(e -> e.getOrderId().equals(oid2) ||e.getOrderId().equals(oid3)).count(),2);
            assertTrue(oid0.equals(-1));
            //check balance has been correctly updated after payments
            assertTrue(bal.equals(100.0));

            // quantity less than p0
            assertThrows(InvalidQuantityException.class, () -> {
                ezshop.payOrderFor( "9780072125757",-1, 3);;
            });

            // quantity 0
            assertThrows(InvalidQuantityException.class, () -> {
                ezshop.payOrderFor( "9780072125757",0, 3);;
            });


            // price1
            assertThrows(InvalidPricePerUnitException.class, () -> {
                ezshop.payOrderFor( "9780072125757",5, -1);;
            });

            // price2
            assertThrows(InvalidPricePerUnitException.class, () -> {
                ezshop.payOrderFor( "9780072125757",5, 0 );
            });

            // Empty productcode
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.payOrderFor("",5,0.5);
            });


            // Null productcode
            assertThrows(InvalidProductCodeException.class, () -> {

                ezshop.payOrderFor(null,5,0.5);
            });

            // Not number productcode
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.payOrderFor("57a1234567899",5,0.5);
            });


            // Not valid productcode
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.payOrderFor("5701234567",5,0.5);
            });


            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.payOrderFor("5701234567899",5,0.5);

            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.payOrderFor("5701234567899",5,0.5);

            });

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }}

    @Test
    public void testPayOrder(){
        try{
            ezshop.login("shopmanager", "password");
            // create test prod
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            Integer id2= ezshop.createProductType("spaghettini",     "5012345678900", 5.0, "nota" );

            //need aditional order to checke exceptions
            Integer oid4=ezshop.issueOrder("5012345678900", 5, 10);

            //order one invalid no balance
            Integer oid0= ezshop.issueOrder("5701234567899",5,10);
            boolean notok=ezshop.payOrder(oid0);

            ezshop.recordBalanceUpdate(200);
            //invalid order because of prod not present
            /*
            Integer oid1= ezshop.issueOrder("9780072125757",5,4);
            boolean notok1=ezshop.payOrder(oid1);

             */
            //valid order n1
            Integer oid2= ezshop.issueOrder("5701234567899",5,4);
            boolean ok=ezshop.payOrder(oid2);

            Integer id3= ezshop.createProductType("spaghettioni", "9780072125757", 5.0, "nota" );
            Double bal=ezshop.computeBalance();
            assertTrue(bal.equals(180.0));

            //check the order results payed
            assertEquals(ezshop.getAllOrders().stream().filter(e -> e.getOrderId().equals(oid2) && e.getStatus().equals("PAYED")).count(),1);


            //order 3 valid
            Integer oid3= ezshop.payOrderFor("9780072125757",5,10);
            boolean notok2=ezshop.payOrder(oid3);
            bal=ezshop.computeBalance();

            //inexistent orderid
            boolean notok3=ezshop.payOrder(5);


            assertTrue(ok);
            assertFalse(notok);
            //assertFalse(notok1);
            assertFalse(notok2);
            assertFalse(notok3);

            //check there are two payed order and the status is correctly set
            assertEquals(ezshop.getAllOrders().stream().filter(e ->  (e.getOrderId().equals(oid2)||e.getOrderId().equals(oid3)) && e.getStatus().equals("PAYED")).count(),2);

            //check balance has been correctly updated after payments
            assertTrue(bal.equals(130.0));

            //  order id less than 0
            assertThrows(InvalidOrderIdException.class, () -> {
                ezshop.payOrder(-5);

            });

            //  order id equals 0
            assertThrows(InvalidOrderIdException.class, () -> {
                ezshop.payOrder(0);

            });

            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.payOrder(oid4);

            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.payOrder(oid4);

            });

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }}

    @Test
    public void testRecordOrderArrival(){
        try{
            ezshop.login("admin", "password");

            // create test prod
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            Integer id2= ezshop.createProductType("spaghettini",     "5012345678900", 5.0, "nota" );
            Integer id3= ezshop.createProductType("spaghettioni", "9780072125757", 5.0, "nota" );


            ezshop.updatePosition(id1,"3-b-5");
            ezshop.updatePosition(id2,"1-a-2");

            ezshop.recordBalanceUpdate(200);

            //create sample orders for testing
            //order not completed
            Integer oid0= ezshop.issueOrder("5701234567899",5,10);
            //ok
            Integer oid1= ezshop.payOrderFor("5701234567899",5,4);
            //no locations set
            Integer oid2= ezshop.payOrderFor("9780072125757",5,10);
            //ok for others
            Integer oid3=ezshop.payOrderFor("5012345678900", 5, 10);


            assertFalse(ezshop.recordOrderArrival(oid0));
            assertTrue(ezshop.recordOrderArrival(oid1));
            int tmp=ezshop.getProductTypeByBarCode("5701234567899").getQuantity();
            assertEquals(tmp,5);


            //has no assigned location
            assertThrows(InvalidLocationException.class, () -> {
                ezshop.recordOrderArrival(oid2);
            });

            //  order id less than 0
            assertThrows(InvalidOrderIdException.class, () -> {
                ezshop.recordOrderArrival(-5);

            });

            //  order id equals 0
            assertThrows(InvalidOrderIdException.class, () -> {
                ezshop.recordOrderArrival(0);

            });

            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.payOrder(oid3);

            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.payOrder(oid3);

            });

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testGetAllOrders(){
        try{
            ezshop.login("admin", "password");

            //Check that if there are no users empty list is returned
            List<Order> list=new ArrayList<>();


            // create test prod
            Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
            Integer id2= ezshop.createProductType("spaghettini",     "5012345678900", 5.0, "nota" );
            Integer id3= ezshop.createProductType("spaghettioni", "9780072125757", 5.0, "nota" );

            ezshop.recordBalanceUpdate(200);

            //create sample orders for testing
            //order not completed
            Integer maxid=ezshop.getCreditsAndDebits(null,null).stream().map(b->b.getBalanceId()).max((Comparator.comparing(Integer::valueOf))).get();
            Integer oid0= ezshop.issueOrder("5701234567899",5,10);
            Order o0 = new OrderImpl(0,  "5701234567899",10,5,"ISSUED", oid0);
            //ok
            Integer oid1= ezshop.payOrderFor("5701234567899",5,5);
            Order o1 = new OrderImpl(maxid+1,  "5701234567899",5,5,"PAYED", oid1);
            //no locations set
            Integer oid2=ezshop.payOrderFor("5012345678900", 5, 10);
            Order o2 = new OrderImpl(maxid+2,  "5012345678900",10,5,"PAYED", oid2);
            //ok for others
            list.add(o0);
            list.add(o1);
            list.add(o2);
            ;

            List<Order> listmp=new ArrayList<>();
            listmp=ezshop.getAllOrders();
            int i=0;
            while(i<list.size()) {
                assertTrue(compareOrders(list.get(i),listmp.get(i)));
                i++;
            }

            ezshop.logout();
            //  null user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getAllOrders();
            });

            ezshop.login("cashier","password"); // Cashier
            // Unauthorized user
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getAllOrders();
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    private boolean compareOrders(Order o1, Order o2) {
        if(o1.getOrderId().equals(o2.getOrderId()) &&
                o1.getProductCode().equals(o2.getProductCode()) &&
                (o1.getQuantity()==o2.getQuantity()) &&
                o1.getStatus().equals(o2.getStatus()) &&
                o1.getPricePerUnit()==o2.getPricePerUnit())
            return true;
        return false;

    }


    @Test
    public void testDefineCustomer() {

        try {

            // Admin
            ezshop.login("admin","password");

            // First customer
            Integer id1 = ezshop.defineCustomer("Name1");
            Integer expected1 = 1;

            // Cashier
            ezshop.logout();
            ezshop.login("cashier","password");

            // Second customer
            Integer id2 = ezshop.defineCustomer("Name2");
            Integer expected2 = 2;

            // Shop Manager
            ezshop.logout();
            ezshop.login("shopmanager","password");

            // Customer name is not unique
            Integer id3 = ezshop.defineCustomer("Name1");
            Integer expected3 = -1;


            // Asserts
            assertEquals (expected1, id1);
            assertEquals (expected2, id2);
            assertEquals (expected3, id3);

            // Empty customer name
            assertThrows(InvalidCustomerNameException.class, () -> {
                ezshop.defineCustomer("");
            });

            // Customer name is null
            assertThrows(InvalidCustomerNameException.class, () -> {
                ezshop.defineCustomer( null );
            });

            ezshop.logout();
            // Unauthorized user or null
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.defineCustomer("Name1");
            });




        }
        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
        }

    }


    @Test
    public void testGetCustomer() {

        try {

            // Admin
            ezshop.login("admin","password");

            // Get customer with id = id1
            Integer id1 = ezshop.defineCustomer("Name1");
            CustomerImpl c1 = ezshop.getCustomer(id1);
            CustomerImpl expected1 = new CustomerImpl("Name1", "", id1, 0);

            // Cashier
            ezshop.logout();
            ezshop.login("cashier","password");

            // Get customer with id = id2
            Integer id2 = ezshop.defineCustomer("Name2");
            CustomerImpl c2 = ezshop.getCustomer(id2);
            CustomerImpl expected2 = new CustomerImpl("Name2", "", id2, 0);

            // Shop Manager
            ezshop.logout();
            ezshop.login("shopmanager","password");

            // Customer with id = 3 does not exist
            CustomerImpl c3 = ezshop.getCustomer(3);

            // Asserts
            assertEquals (expected1.getCustomerName(), c1.getCustomerName());
            assertEquals (expected1.getCustomerCard(), c1.getCustomerCard());
            assertEquals (expected1.getId(), c1.getId());
            assertEquals (expected1.getPoints(), c1.getPoints());

            assertEquals (expected2.getCustomerName(), c2.getCustomerName());
            assertEquals (expected2.getCustomerCard(), c2.getCustomerCard());
            assertEquals (expected2.getId(), c2.getId());
            assertEquals (expected2.getPoints(), c2.getPoints());

            assertNull(c3);

            // Customer id is null
            assertThrows(InvalidCustomerIdException.class, () -> {
                ezshop.getCustomer(null);
            });

            // Customer id equal to 0
            assertThrows(InvalidCustomerIdException.class, () -> {
                ezshop.getCustomer(0);
            });

            // Customer id less than 0
            assertThrows(InvalidCustomerIdException.class, () -> {
                ezshop.getCustomer(-1);
            });

            ezshop.logout();
            // Unauthorized user or null
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getCustomer(1);
            });


        }
        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
        }

    }

    @Test
    public void testDeleteCustomer() {

        try {

            // Admin
            ezshop.login("admin","password");

            // Delete customer with id = id1
            Integer id1 = ezshop.defineCustomer("Name1");
            Boolean deleted1 = ezshop.deleteCustomer(id1);

            // Cashier
            ezshop.logout();
            ezshop.login("cashier","password");

            // Delete customer with id = id1
            Integer id2 = ezshop.defineCustomer("Name2");
            Boolean deleted2 = ezshop.deleteCustomer(id2);

            // Shop Manager
            ezshop.logout();
            ezshop.login("shopmanager","password");

            // Customer with id = 3 does not exist
            Boolean deleted3 = ezshop.deleteCustomer(3);

            // Asserts
            assertTrue (deleted1);
            assertTrue (deleted2);
            assertFalse (deleted3);

            // Customer id is null
            assertThrows(InvalidCustomerIdException.class, () -> {
                ezshop.deleteCustomer(null);
            });

            // Customer id equal to 0
            assertThrows(InvalidCustomerIdException.class, () -> {
                ezshop.deleteCustomer(0);
            });

            // Customer id less than 0
            assertThrows(InvalidCustomerIdException.class, () -> {
                ezshop.deleteCustomer(-1);
            });

            ezshop.logout();
            // Unauthorized user or null
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.deleteCustomer(1);
            });



        }
        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
        }

    }

    @Test
    public void testGetAllCustomers() {

        try {

            // Admin
            ezshop.login("admin","password");

            // The list of customers is empty
            List<Customer> list1 = ezshop.getAllCustomers();
            assertTrue (list1.size() == 0);

            // Cashier
            ezshop.logout();
            ezshop.login("cashier","password");

            // The list of customers has two customers
            Integer id1 = ezshop.defineCustomer("Name1");
            Integer id2 = ezshop.defineCustomer("Name2");
            List<Customer> list2 = ezshop.getAllCustomers();

            // Shop Manager
            ezshop.logout();
            ezshop.login("shopmanager","password");

            // The list of customers has three customers
            Integer id3 = ezshop.defineCustomer("Name3");
            List<Customer> list3 = ezshop.getAllCustomers();

            assertTrue (list2.size() == 2);
            assertTrue (list3.size() == 3);

            assertEquals (list2.get(id1-1).getCustomerName(), ezshop.getCustomer(id1).getCustomerName());
            assertEquals (list2.get(id1-1).getCustomerCard(), ezshop.getCustomer(id1).getCustomerCard());
            assertEquals (list2.get(id1-1).getId(), ezshop.getCustomer(id1).getId());
            assertEquals (list2.get(id1-1).getPoints(), ezshop.getCustomer(id1).getPoints());

            assertEquals (list2.get(id2-1).getCustomerName(), ezshop.getCustomer(id2).getCustomerName());
            assertEquals (list2.get(id2-1).getCustomerCard(), ezshop.getCustomer(id2).getCustomerCard());
            assertEquals (list2.get(id2-1).getId(), ezshop.getCustomer(id2).getId());
            assertEquals (list2.get(id2-1).getPoints(), ezshop.getCustomer(id2).getPoints());

            ezshop.logout();
            // Unauthorized user or null
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getAllCustomers();
            });


        }
        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
        }
    }



    @Test
    public void testModifyCustomer() {

        try {

            // Admin
            ezshop.login("admin","password");

            // Modify only customer card
            Integer id1 = ezshop.defineCustomer("Name1");
            Boolean modified1 = ezshop.modifyCustomer( id1,"Name1", "0000000010");
            String card1 = ezshop.getCustomer(id1).getCustomerCard();
            String card1Expected = "0000000010";

            // Cashier
            ezshop.logout();
            ezshop.login("cashier","password");

            // Modify customer name and detach customer card
            Integer id2 = ezshop.defineCustomer("Name2");
            ezshop.modifyCustomer( id2,"Name2", "0000000020");
            ezshop.modifyPointsOnCard("0000000020",5);
            Integer points1 = ezshop.getCustomer(id2).getPoints();
            Integer points1Expected = 5;

            // Shop Manager
            ezshop.logout();
            ezshop.login("shopmanager","password");

            Boolean modified2 = ezshop.modifyCustomer( id2,"NameModified2", "");
            Integer points2 = ezshop.getCustomer(id2).getPoints();
            Integer points2Expected = 0;

            // Modify only customer name
            Integer id3 = ezshop.defineCustomer("Name3");
            Boolean modified3 = ezshop.modifyCustomer( id3,"Name3", "0000000030");
            String card3 = ezshop.getCustomer(id3).getCustomerCard();
            String card3Expected = "0000000030";

            Boolean modified31 = ezshop.modifyCustomer( id3,"NameModified3", null);
            String name31 = ezshop.getCustomer(id3).getCustomerName();
            String name31Expected = "NameModified3";
            String card31 = ezshop.getCustomer(id3).getCustomerCard();
            String card31Expected = "0000000030";

            // Modify customer name and customer card
            Integer id4 = ezshop.defineCustomer("Name4");
            Boolean modified4 = ezshop.modifyCustomer( id4,"Name4", "0000000040");
            Boolean modified41 = ezshop.modifyCustomer( id4,"NameModified4", "0000000041");
            String name4 = ezshop.getCustomer(id4).getCustomerName();
            String name4Expected = "NameModified4";
            String card4 = ezshop.getCustomer(id4).getCustomerCard();
            String card4Expected = "0000000041";

            // New customer name is not unique
            Integer id5 = ezshop.defineCustomer("Name5");
            Integer id6 = ezshop.defineCustomer("Name6");
            Boolean modified5 = ezshop.modifyCustomer( id5,"Name6", "0000000050");

            // id=7 is not assigned to a customer
            Boolean modified7 = ezshop.modifyCustomer( 7,"Name7", "0000000070");

            // Customer card "0000000010" is already attached to a customer
            Integer id8 = ezshop.defineCustomer("Name8");
            Boolean modified8 = ezshop.modifyCustomer( id8,"Name8", "0000000080");
            Boolean modified81 = ezshop.modifyCustomer( id8,"Name8", "0000000010");



            // Asserts
            assertTrue (modified1);
            assertEquals(card1Expected, card1);
            assertEquals(points1Expected, points1);
            assertTrue (modified2);
            assertEquals(points2Expected, points2);
            assertTrue(modified3);
            assertEquals(card3Expected, card3);
            assertEquals(card31Expected, card31);
            assertEquals(name31Expected, name31);
            assertTrue (modified31);
            assertTrue (modified4);
            assertTrue (modified41);
            assertEquals(name4Expected, name4);
            assertEquals(card4Expected, card4);
            assertFalse (modified5);
            assertFalse (modified7);
            assertTrue (modified8);
            assertFalse (modified81);


            // Empty customer name
            assertThrows(InvalidCustomerNameException.class, () -> {
                ezshop.modifyCustomer( 1, "", "0000000100");
            });

            // Customer name is null
            assertThrows(InvalidCustomerNameException.class, () -> {
                ezshop.modifyCustomer( 1, null, "0000000100");
            });

            // Customer card is not in a valid format
            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.modifyCustomer( 1,"Name1", "012345678");
            });

            // Customer card is not in a valid format (2)
            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.modifyCustomer( 1,"Name1", "abcdefghij");
            });

            ezshop.logout();
            // Unauthorized user or null
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.modifyCustomer( 1,"Name1", "0000000001");
            });



        }
        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
        }
    }

    @Test
    public void testAttachCardToCustomer() {

        try {

            // Admin
            ezshop.login("admin","password");

            // The customer with id=id1 has not a card assigned
            Integer id1 = ezshop.defineCustomer("Name1");
            Boolean attached1 = ezshop.attachCardToCustomer("0000000001", id1);

            // Cashier
            ezshop.logout();
            ezshop.login("cashier","password");

            // The customer with id=id2 has not a card assigned
            Integer id2 = ezshop.defineCustomer("Name2");
            Boolean attached2 = ezshop.attachCardToCustomer("0000000002", id2);

            // Shop Manager
            ezshop.logout();
            ezshop.login("shopmanager","password");

            // The customer with id=id2 has a card assigned
            Boolean attached3 = ezshop.attachCardToCustomer("0000000003", id2);


            // There is not customer with id=4
            Boolean attached4 = ezshop.attachCardToCustomer("0000000004", 4);

            // Customer card "0000000003" already assigned to a customer with id=id2
            Integer id5 = ezshop.defineCustomer("Name5");
            Boolean attached5 = ezshop.attachCardToCustomer("0000000003", id5);

            // Asserts
            assertTrue(attached1);
            assertTrue(attached2);
            assertTrue(attached3);
            assertFalse(attached4);
            assertFalse(attached5);

            // Customer id is null
            assertThrows(InvalidCustomerIdException.class, () -> {
                ezshop.attachCardToCustomer("0000000000", null);
            });

            // Customer id equal to 0
            assertThrows(InvalidCustomerIdException.class, () -> {
                ezshop.attachCardToCustomer("0000000000", 0);
            });

            // Customer id less than 0
            assertThrows(InvalidCustomerIdException.class, () -> {
                ezshop.attachCardToCustomer("0000000000", -1);
            });

            // Empty customer card
            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.attachCardToCustomer("", 1);
            });

            // Customer card is null
            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.attachCardToCustomer(null, 1);
            });

            // Customer card is not in a valid format
            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.attachCardToCustomer("012345678", 1);
            });

            ezshop.logout();
            // Unauthorized user or null
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.attachCardToCustomer("0000000001", 1);
            });


        }
        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
        }
    }


    @Test
    public void testCreateCard() {

        try {

            // Admin
            ezshop.login("admin","password");

            // First card in the database
            String card1 = ezshop.createCard();
            String expected1 = "0000000000";

            // Cashier
            ezshop.logout();
            ezshop.login("cashier","password");

            // The value "0000000000" of card1 is not attached to a customer, so createCard() returns again "0000000000"
            String card2 = ezshop.createCard();
            String expected2 = "0000000000";

            Integer id2 = ezshop.defineCustomer("Name2");
            ezshop.attachCardToCustomer(card2, id2);

            // Shop Manager
            ezshop.logout();
            ezshop.login("shopmanager","password");

            String card3 = ezshop.createCard();
            String expected3 = "0000000001";

            Integer id3 = ezshop.defineCustomer("Name3");
            ezshop.attachCardToCustomer(card3, id3);

            String card4 = ezshop.createCard();
            String expected4 = "0000000002";

            // The value "0000000002" of card4 is not attached to a customer, so createCard() returns again "0000000002"
            String card5 = ezshop.createCard();
            String expected5 = "0000000002";

            Integer id5 = ezshop.defineCustomer("Name5");
            ezshop.attachCardToCustomer(card5, id5);

            // Detach card3(0000000001), so createCard() returns "0000000001"
            ezshop.modifyCustomer(id3, "Name3", "");
            String card6 = ezshop.createCard();
            String expected6 = "0000000001";


            // Asserts
            assertEquals (card1,expected1);
            assertEquals (card2,expected2);
            assertEquals (card3,expected3);
            assertEquals (card4,expected4);
            assertEquals (card5,expected5);
            assertEquals (card6,expected6);

            ezshop.logout();
            // Unauthorized user or null
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.createCard();
            });


        }
        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
        }
    }


    @Test
    public void testModifyPointsOnCard() {

        try {

            // Admin
            ezshop.login("admin","password");

            Integer id1 = ezshop.defineCustomer("Name1");
            ezshop.attachCardToCustomer("0000000001", id1);
            Boolean modifiedPoints1 = ezshop.modifyPointsOnCard("0000000001", 1);

            // Cashier
            ezshop.logout();
            ezshop.login("cashier","password");

            Integer id2 = ezshop.defineCustomer("Name2");
            ezshop.attachCardToCustomer("0000000002", id2);
            Boolean modifiedPoints2 = ezshop.modifyPointsOnCard("0000000002", 1);

            // Shop Manager
            ezshop.logout();
            ezshop.login("shopmanager","password");

            Integer id3 = ezshop.defineCustomer("Name3");
            ezshop.attachCardToCustomer("0000000003", id3);
            Boolean modifiedPoints3 = ezshop.modifyPointsOnCard("0000000003", 1);

            Integer id4 = ezshop.defineCustomer("Name4");
            ezshop.attachCardToCustomer("0000000004", id4);
            Boolean modifiedPoints4 = ezshop.modifyPointsOnCard("0000000004", 1);


            // There is not a card with given code assigned to a customer
            Boolean modifiedPoints5 = ezshop.modifyPointsOnCard("0000000005", 5);

            // The points on a card should always be greater than or equal to 0
            Integer id6 = ezshop.defineCustomer("Name6");
            ezshop.attachCardToCustomer("0000000006", id6);
            Boolean modifiedPoints6 = ezshop.modifyPointsOnCard("0000000006", 1);
            Boolean modifiedPoints7 = ezshop.modifyPointsOnCard("0000000006", -1);
            Boolean modifiedPoints8 = ezshop.modifyPointsOnCard("0000000006", -1);


            // Asserts
            assertTrue(modifiedPoints1);
            assertTrue(modifiedPoints2);
            assertTrue(modifiedPoints3);
            assertTrue(modifiedPoints4);
            assertFalse(modifiedPoints5);
            assertTrue(modifiedPoints6);
            assertTrue(modifiedPoints7);
            assertFalse(modifiedPoints8);


            // Empty customer card
            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.modifyPointsOnCard("", 0);
            });

            // Customer card is null
            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.modifyPointsOnCard(null, 0);
            });

            // Customer card is not in a valid format
            assertThrows(InvalidCustomerCardException.class, () -> {
                ezshop.modifyPointsOnCard("012345678", 0);
            });

            ezshop.logout();
            // Unauthorized user or null
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.modifyPointsOnCard("0000000001", 0);
            });

        }
        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
        }
    }

    @Test
    public void testReturnCashPayment(){
        try {
            //Checking for not logged user
            assertThrows(UnauthorizedException.class,() -> ezshop.returnCashPayment(1));

            //Administrator perspective
            ezshop.login("admin","password");

            Integer idProductType = ezshop.createProductType("pasta", "5701234567899", 1.0, "none" );
            ezshop.updatePosition(idProductType, "1-a-1");
            ezshop.updateQuantity(idProductType, 10);
            Integer idTransaction = ezshop.startSaleTransaction();
            ezshop.addProductToSale(idTransaction,"5701234567899",1);
            ezshop.endSaleTransaction(idTransaction);
            ezshop.receiveCashPayment(idTransaction,1);

            Integer idReturnTransaction = ezshop.startReturnTransaction(idTransaction);
            ezshop.returnProduct(idReturnTransaction,"5701234567899",1);

            //Check for non ended transaction
            assertTrue(ezshop.returnCashPayment(idReturnTransaction)==-1);

            ezshop.endReturnTransaction(idReturnTransaction,true);

            //Checking for non existing return id
            assertTrue(ezshop.returnCashPayment(27)==-1);
            //Checking for id less or equal than 0
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCashPayment(0));
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCashPayment(-1));
            //Here's the real return transaction
            assertTrue(ezshop.returnCashPayment(idReturnTransaction)==1);

            ezshop.logout();
            setUp();

            //Shopmanager perspective
            ezshop.login("shopmanager","password");

            Integer idProductType2 = ezshop.createProductType("pasta", "5701234567899", 1.0, "none" );
            ezshop.updatePosition(idProductType2, "1-a-1");
            ezshop.updateQuantity(idProductType2, 10);
            Integer idTransaction2 = ezshop.startSaleTransaction();
            ezshop.addProductToSale(idTransaction2,"5701234567899",1);
            ezshop.endSaleTransaction(idTransaction2);
            ezshop.receiveCashPayment(idTransaction2,1);

            Integer idReturnTransaction2 = ezshop.startReturnTransaction(idTransaction2);
            ezshop.returnProduct(idReturnTransaction2,"5701234567899",1);

            //Check for non ended transaction
            assertTrue(ezshop.returnCashPayment(idReturnTransaction2)==-1);

            ezshop.endReturnTransaction(idReturnTransaction2,true);

            //Checking for non existing return id
            assertTrue(ezshop.returnCashPayment(27)==-1);
            //Checking for id less or equal than 0
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCashPayment(0));
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCashPayment(-1));
            //Here's the real return transaction
            assertTrue(ezshop.returnCashPayment(idReturnTransaction2)==1);

            ezshop.logout();
            setUp();

            ezshop.login("admin","password");
            Integer idProductType3 = ezshop.createProductType("pasta", "5701234567899", 1.0, "none" );
            ezshop.updatePosition(idProductType3, "1-a-1");
            ezshop.updateQuantity(idProductType3, 10);
            ezshop.logout();

            //Cashier perspective
            ezshop.login("shopmanager","password");

            Integer idTransaction3 = ezshop.startSaleTransaction();
            ezshop.addProductToSale(idTransaction3,"5701234567899",1);
            ezshop.endSaleTransaction(idTransaction3);
            ezshop.receiveCashPayment(idTransaction3,1);

            Integer idReturnTransaction3 = ezshop.startReturnTransaction(idTransaction3);
            ezshop.returnProduct(idReturnTransaction3,"5701234567899",1);

            //Check for non ended transaction
            assertTrue(ezshop.returnCashPayment(idReturnTransaction3)==-1);

            ezshop.endReturnTransaction(idReturnTransaction3,true);

            //Checking for non existing return id
            assertTrue(ezshop.returnCashPayment(27)==-1);
            //Checking for id less or equal than 0
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCashPayment(0));
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCashPayment(-1));
            //Here's the real return transaction
            assertTrue(ezshop.returnCashPayment(idReturnTransaction3)==1);

            setUp();
        } catch(Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testReturnCreditCardPayment(){
        try {
            //Checking for not logged user
            assertThrows(UnauthorizedException.class,() -> ezshop.returnCashPayment(1));

            //Administrator perspective
            ezshop.login("admin","password");

            Integer idProductType = ezshop.createProductType("pasta", "5701234567899", 1.0, "none" );
            ezshop.updatePosition(idProductType, "1-a-1");
            ezshop.updateQuantity(idProductType, 10);
            Integer idTransaction = ezshop.startSaleTransaction();
            ezshop.addProductToSale(idTransaction,"5701234567899",1);
            ezshop.endSaleTransaction(idTransaction);
            ezshop.receiveCashPayment(idTransaction,1);

            Integer idReturnTransaction = ezshop.startReturnTransaction(idTransaction);
            ezshop.returnProduct(idReturnTransaction,"5701234567899",1);

            //Check for non ended transaction
            assertTrue(ezshop.returnCreditCardPayment(idReturnTransaction,"4716258050958645")==-1);

            ezshop.endReturnTransaction(idReturnTransaction,true);

            //Checking for non existing return id
            assertTrue(ezshop.returnCreditCardPayment(27,"4716258050958645")==-1);
            //Checking for non registered Credit Card
            assertTrue(ezshop.returnCreditCardPayment(idReturnTransaction,"7784937915391288")==-1);
            //Checking for id less or equal than 0
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCreditCardPayment(0,"4716258050958645"));
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCreditCardPayment(-1,"4716258050958645"));
            //Checking for null or empty credit Card
            assertThrows(InvalidCreditCardException.class,()->ezshop.returnCreditCardPayment(idReturnTransaction,null));
            assertThrows(InvalidCreditCardException.class,()->ezshop.returnCreditCardPayment(idReturnTransaction,""));
            //Invalid Luhn Algorithm
            assertThrows(InvalidCreditCardException.class,()->ezshop.returnCreditCardPayment(idReturnTransaction,"4485370086510892"));
            //Here's the real return transaction
            assertTrue(ezshop.returnCreditCardPayment(idReturnTransaction,"5100293991053009")==1);

            ezshop.logout();
            setUp();

            //Shopmanager perspective
            ezshop.login("shopmanager","password");

            Integer idProductType2 = ezshop.createProductType("pasta", "5701234567899", 1.0, "none" );
            ezshop.updatePosition(idProductType2, "1-a-1");
            ezshop.updateQuantity(idProductType2, 10);
            Integer idTransaction2 = ezshop.startSaleTransaction();
            ezshop.addProductToSale(idTransaction2,"5701234567899",1);
            ezshop.endSaleTransaction(idTransaction2);
            ezshop.receiveCashPayment(idTransaction2,1);

            Integer idReturnTransaction2 = ezshop.startReturnTransaction(idTransaction2);
            ezshop.returnProduct(idReturnTransaction2,"5701234567899",1);

            //Check for non ended transaction
            assertTrue(ezshop.returnCreditCardPayment(idReturnTransaction2,"4716258050958645")==-1);

            ezshop.endReturnTransaction(idReturnTransaction2,true);

            //Checking for non existing return id
            assertTrue(ezshop.returnCreditCardPayment(27,"4716258050958645")==-1);
            //Checking for non registered Credit Card
            assertTrue(ezshop.returnCreditCardPayment(idReturnTransaction2,"7784937915391288")==-1);
            //Checking for id less or equal than 0
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCreditCardPayment(0,"4716258050958645"));
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCreditCardPayment(-1,"4716258050958645"));
            //Checking for null or empty credit Card
            assertThrows(InvalidCreditCardException.class,()->ezshop.returnCreditCardPayment(idReturnTransaction2,null));
            assertThrows(InvalidCreditCardException.class,()->ezshop.returnCreditCardPayment(idReturnTransaction2,""));
            //Invalid Luhn Algorithm
            assertThrows(InvalidCreditCardException.class,()->ezshop.returnCreditCardPayment(idReturnTransaction2,"4485370086510892"));
            //Here's the real return transaction
            assertTrue(ezshop.returnCreditCardPayment(idReturnTransaction2,"5100293991053009")==1);

            ezshop.logout();
            setUp();

            ezshop.login("admin","password");
            Integer idProductType3 = ezshop.createProductType("pasta", "5701234567899", 1.0, "none" );
            ezshop.updatePosition(idProductType3, "1-a-1");
            ezshop.updateQuantity(idProductType3, 10);
            ezshop.logout();

            //Shopmanager perspective
            ezshop.login("shopmanager","password");

            Integer idTransaction3 = ezshop.startSaleTransaction();
            ezshop.addProductToSale(idTransaction3,"5701234567899",1);
            ezshop.endSaleTransaction(idTransaction3);
            ezshop.receiveCashPayment(idTransaction3,1);

            Integer idReturnTransaction3 = ezshop.startReturnTransaction(idTransaction3);
            ezshop.returnProduct(idReturnTransaction3,"5701234567899",1);

            //Check for non ended transaction
            assertTrue(ezshop.returnCreditCardPayment(idReturnTransaction3,"4716258050958645")==-1);

            ezshop.endReturnTransaction(idReturnTransaction3,true);

            //Checking for non existing return id
            assertTrue(ezshop.returnCreditCardPayment(27,"4716258050958645")==-1);
            //Checking for non registered Credit Card
            assertTrue(ezshop.returnCreditCardPayment(idReturnTransaction3,"7784937915391288")==-1);
            //Checking for id less or equal than 0
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCreditCardPayment(0,"4716258050958645"));
            assertThrows(InvalidTransactionIdException.class,() -> ezshop.returnCreditCardPayment(-1,"4716258050958645"));
            //Checking for null or empty credit Card
            assertThrows(InvalidCreditCardException.class,()->ezshop.returnCreditCardPayment(idReturnTransaction3,null));
            assertThrows(InvalidCreditCardException.class,()->ezshop.returnCreditCardPayment(idReturnTransaction3,""));
            //Invalid Luhn Algorithm
            assertThrows(InvalidCreditCardException.class,()->ezshop.returnCreditCardPayment(idReturnTransaction3,"4485370086510892"));
            //Here's the real return transaction
            assertTrue(ezshop.returnCreditCardPayment(idReturnTransaction3,"5100293991053009")==1);

        } catch(Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testRecordBalanceUpdate() {
        try {
            //Checking if not user logged in is refused
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.recordBalanceUpdate(50);
            });

            //Checking if cashier operations are refused
            ezshop.login("cashier", "password");
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.recordBalanceUpdate(50);
            });

            ezshop.logout();

            //Check from shopmanager perspective
            ezshop.login("shopmanager", "password");
            assertTrue(ezshop.recordBalanceUpdate(50));
            assertTrue(ezshop.recordBalanceUpdate(20));
            assertTrue(ezshop.recordBalanceUpdate(-10));
            assertFalse(ezshop.recordBalanceUpdate(-1000));

            List<BalanceOperation>  l = ezshop.getCreditsAndDebits(null,null);
            assertTrue(l.size() == 3);
            assertTrue(l.get(0).getType() == "CREDIT");
            assertTrue(l.get(1).getType() == "CREDIT");
            assertTrue(l.get(2).getType() == "DEBIT");

            ezshop.logout();

            setUp();

            //Check from administrator perspective
            ezshop.login("admin", "password");

            assertTrue(ezshop.recordBalanceUpdate(50));
            assertTrue(ezshop.recordBalanceUpdate(20));
            assertTrue(ezshop.recordBalanceUpdate(-10));
            assertFalse(ezshop.recordBalanceUpdate(-1000));

            List<BalanceOperation>  l2 = ezshop.getCreditsAndDebits(null,null);
            assertTrue(l2.size() == 3);
            assertTrue(l2.get(0).getType() == "CREDIT");
            assertTrue(l2.get(1).getType() == "CREDIT");
            assertTrue(l2.get(2).getType() == "DEBIT");


            ezshop.logout();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    //There's no way to test the date interval since there's no way to set it in a balance update
    //ID test doesn't pass because balance operations still use autoincrement
    @Test
    public void getCreditsAndDebits() {
        try {
            //Checking if not user logged in is refused
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getCreditsAndDebits(null, null);
            });

            //Checking if cashier operations are refused
            ezshop.login("cashier", "password");
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getCreditsAndDebits(null, null);
            });
            ezshop.logout();

            //Checking from shopmanager perspective
            ezshop.login("shopmanager","password");

            //Return empty list
            assertTrue(ezshop.getCreditsAndDebits(null,null).size() == 0);

            //Adding some manual balanceUpdate
            ezshop.recordBalanceUpdate(1000);
            ezshop.recordBalanceUpdate(-10);
            ezshop.recordBalanceUpdate(-200);
            ezshop.recordBalanceUpdate(250);

            //Testing if content is coherent
            List<BalanceOperation> l = ezshop.getCreditsAndDebits(null,null);

            assertTrue(l.size() == 4);
            assertTrue(l.get(0).getType() == "CREDIT");
            assertTrue(l.get(1).getType() == "DEBIT");
            assertTrue(l.get(2).getType() == "DEBIT");
            assertTrue(l.get(3).getType() == "CREDIT");

            assertTrue(l.get(0).getMoney() == 1000);
            assertTrue(l.get(1).getMoney() == -10);
            assertTrue(l.get(2).getMoney() == -200);
            assertTrue(l.get(3).getMoney() == 250);

            ezshop.logout();
            setUp();

            //Checking from administrator perspective
            ezshop.login("admin","password");

            //Return empty list
            assertTrue(ezshop.getCreditsAndDebits(null,null).size() == 0);

            //Adding some manual balanceUpdate
            ezshop.recordBalanceUpdate(1000);
            ezshop.recordBalanceUpdate(-10);
            ezshop.recordBalanceUpdate(-200);
            ezshop.recordBalanceUpdate(250);

            //Testing if content is coherent
            List<BalanceOperation> l2 = ezshop.getCreditsAndDebits(null,null);

            assertTrue(l2.size() == 4);
            assertTrue(l2.get(0).getType() == "CREDIT");
            assertTrue(l2.get(1).getType() == "DEBIT");
            assertTrue(l2.get(2).getType() == "DEBIT");
            assertTrue(l2.get(3).getType() == "CREDIT");

            assertTrue(l2.get(0).getMoney() == 1000);
            assertTrue(l2.get(1).getMoney() == -10);
            assertTrue(l2.get(2).getMoney() == -200);
            assertTrue(l2.get(3).getMoney() == 250);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void computeBalance() {
        try {
            ezshop.login("cashier", "password");
            assertThrows(UnauthorizedException.class, () -> ezshop.computeBalance());
            ezshop.logout();

            ezshop.login("shopmanager", "password");

            assertTrue(ezshop.computeBalance() == 0);

            ezshop.recordBalanceUpdate(1000);
            ezshop.recordBalanceUpdate(-10);
            ezshop.recordBalanceUpdate(-200);
            ezshop.recordBalanceUpdate(250);

            assertTrue(ezshop.computeBalance() == 1040);

            ezshop.logout();

            setUp();

            ezshop.login("admin", "password");

            assertTrue(ezshop.computeBalance() == 0);

            ezshop.recordBalanceUpdate(1000);
            ezshop.recordBalanceUpdate(-10);
            ezshop.recordBalanceUpdate(-200);
            ezshop.recordBalanceUpdate(250);

            assertTrue(ezshop.computeBalance() == 1040);
            ezshop.logout();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


    @Test
    public void testStartSaleTransaction() {
        try {
            // Invoked before a user with role "Administrator", "ShopManager" or "Cashier" is logged in
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.startSaleTransaction();
            });

            // Invoked after a user with role "Administrator" is logged in
            ezshop.login("admin","password");
            assertTrue(ezshop.startSaleTransaction() >= 0);
            ezshop.logout();

            // Invoked after a user with role "ShopManager" is logged in
            ezshop.login("shopmanager","password");
            assertTrue(ezshop.startSaleTransaction() >= 0);
            ezshop.logout();

            // Invoked after a user with role "Administrator" is logged in
            ezshop.login("cashier","password");
            assertTrue(ezshop.startSaleTransaction() >= 0);
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddProductToSale() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionIdClosed = ezshop.startSaleTransaction();
            ezshop.endSaleTransaction(transactionIdClosed);
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            Integer initialProductQuantity = 10;
            ezshop.updateQuantity(productId, initialProductQuantity);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.addProductToSale(transactionId, "5701234567899", 1);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.addProductToSale(null, "5701234567899", 1);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.addProductToSale(0, "5701234567899", 1);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.addProductToSale(-1, "5701234567899", 1);
            });
            ezshop.logout();

            // Check InvalidProductCodeException
            ezshop.login("admin","password");
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.addProductToSale(transactionId, null, 1);
            });
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.addProductToSale(transactionId, "", 1);
            });
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.addProductToSale(transactionId, "0123", 1);
            });
            ezshop.logout();

            // Check InvalidQuantityException
            ezshop.login("admin","password");
            assertThrows(InvalidQuantityException.class, () -> {
                ezshop.addProductToSale(transactionId, "5701234567899", -1);
            });
            ezshop.logout();

            // Check if the product code does not exist
            ezshop.login("admin","password");
            assertFalse(ezshop.addProductToSale(transactionId, "012345678912", 1));
            ezshop.logout();

            // Check if the quantity of product cannot satisfy the request
            ezshop.login("admin","password");
            assertFalse(ezshop.addProductToSale(transactionId, "5701234567899", initialProductQuantity + 1));
            assertEquals(initialProductQuantity, ezshop.getProductTypeByBarCode("5701234567899").getQuantity());
            ezshop.logout();

            // Check if the SaleTransaction does not exist or not identify a started and open transaction
            ezshop.login("admin","password");
            assertFalse(ezshop.addProductToSale(transactionId + 1, "5701234567899", 1));
            assertFalse(ezshop.addProductToSale(transactionIdClosed, "5701234567899", 1));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            assertTrue(ezshop.addProductToSale(transactionId, "5701234567899", 1));
            assertEquals(Integer.valueOf(initialProductQuantity - 1), ezshop.getProductTypeByBarCode("5701234567899").getQuantity());
            ezshop.endSaleTransaction(transactionId);
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDeleteProductFromSale() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionIdClosed = ezshop.startSaleTransaction();
            ezshop.endSaleTransaction(transactionIdClosed);
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            Integer initialProductQuantity = 10;
            ezshop.updateQuantity(productId, initialProductQuantity);
            Integer productQuantityInSale = 2;
            ezshop.addProductToSale(transactionId, "5701234567899", productQuantityInSale);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.deleteProductFromSale(transactionId, "5701234567899", 1);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.deleteProductFromSale(null, "5701234567899", 1);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.deleteProductFromSale(0, "5701234567899", 1);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.deleteProductFromSale(-1, "5701234567899", 1);
            });
            ezshop.logout();

            // Check InvalidProductCodeException
            ezshop.login("admin","password");
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.deleteProductFromSale(transactionId, null, 1);
            });
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.deleteProductFromSale(transactionId, "", 1);
            });
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.deleteProductFromSale(transactionId, "0123", 1);
            });
            ezshop.logout();

            // Check InvalidQuantityException
            ezshop.login("admin","password");
            assertThrows(InvalidQuantityException.class, () -> {
                ezshop.deleteProductFromSale(transactionId, "5701234567899", -1);
            });
            ezshop.logout();

            // Check if the product code does not exist
            ezshop.login("admin","password");
            assertFalse(ezshop.deleteProductFromSale(transactionId, "012345678912", 1));
            ezshop.logout();

            // Check if the quantity of product cannot satisfy the request
            ezshop.login("admin","password");
            assertFalse(ezshop.deleteProductFromSale(transactionId, "5701234567899", productQuantityInSale + 1));
            assertEquals(Integer.valueOf(initialProductQuantity - productQuantityInSale), ezshop.getProductTypeByBarCode("5701234567899").getQuantity());
            ezshop.logout();

            // Check if the SaleTransaction does not exist or not identify a started and open transaction
            ezshop.login("admin","password");
            assertFalse(ezshop.deleteProductFromSale(transactionId + 1, "5701234567899", 1));
            assertFalse(ezshop.deleteProductFromSale(transactionIdClosed, "5701234567899", 1));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            assertTrue(ezshop.deleteProductFromSale(transactionId, "5701234567899", 1));
            assertEquals(Integer.valueOf(initialProductQuantity - productQuantityInSale + 1), ezshop.getProductTypeByBarCode("5701234567899").getQuantity());
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testApplyDiscountRateToProduct() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionIdClosed = ezshop.startSaleTransaction();
            ezshop.endSaleTransaction(transactionIdClosed);
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            Integer initialProductQuantity = 10;
            ezshop.updateQuantity(productId, initialProductQuantity);
            Integer productQuantityInSale = 2;
            ezshop.addProductToSale(transactionId, "5701234567899", productQuantityInSale);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.applyDiscountRateToProduct(transactionId, "5701234567899", 0.10);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.applyDiscountRateToProduct(null, "5701234567899", 0.10);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.applyDiscountRateToProduct(0, "5701234567899", 0.10);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.applyDiscountRateToProduct(-1, "5701234567899", 0.10);
            });
            ezshop.logout();

            // Check InvalidProductCodeException
            ezshop.login("admin","password");
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.applyDiscountRateToProduct(transactionId, null, 0.10);
            });
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.applyDiscountRateToProduct(transactionId, "", 0.10);
            });
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.applyDiscountRateToProduct(transactionId, "0123", 0.10);
            });
            ezshop.logout();

            // Check InvalidDiscountRateException
            ezshop.login("admin","password");
            assertThrows(InvalidDiscountRateException.class, () -> {
                ezshop.applyDiscountRateToProduct(transactionId, "5701234567899", -0.01);
            });
            assertThrows(InvalidDiscountRateException.class, () -> {
                ezshop.applyDiscountRateToProduct(transactionId, "5701234567899", -100.00);
            });
            assertThrows(InvalidDiscountRateException.class, () -> {
                ezshop.applyDiscountRateToProduct(transactionId, "5701234567899", 1.00);
            });
            assertThrows(InvalidDiscountRateException.class, () -> {
                ezshop.applyDiscountRateToProduct(transactionId, "5701234567899", 100.00);
            });
            ezshop.logout();

            // Check if the product code does not exist
            ezshop.login("admin","password");
            assertFalse(ezshop.applyDiscountRateToProduct(transactionId, "012345678912", 0.10));
            ezshop.logout();

            // Check if the SaleTransaction does not exist or not identify a started and open transaction
            ezshop.login("admin","password");
            assertFalse(ezshop.applyDiscountRateToProduct(transactionId + 1, "5701234567899", 0.10));
            assertFalse(ezshop.applyDiscountRateToProduct(transactionIdClosed, "5701234567899", 0.10));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            assertTrue(ezshop.applyDiscountRateToProduct(transactionId, "5701234567899", 0.10));
            ezshop.endSaleTransaction(transactionId);
            assertEquals(productQuantityInSale * 1.25 * (1 - 0.10), ezshop.getSaleTransaction(transactionId).getPrice(), 0.01);
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testApplyDiscountRateToSale() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionIdPayed = ezshop.startSaleTransaction();
            ezshop.endSaleTransaction(transactionIdPayed);
            ezshop.receiveCashPayment(transactionIdPayed, 10.00);
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            Integer initialProductQuantity = 10;
            ezshop.updateQuantity(productId, initialProductQuantity);
            Integer productQuantityInSale = 2;
            ezshop.addProductToSale(transactionId, "5701234567899", productQuantityInSale);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.applyDiscountRateToSale(transactionId, 0.10);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.applyDiscountRateToSale(null, 0.10);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.applyDiscountRateToSale(0, 0.10);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.applyDiscountRateToSale(-1, 0.10);
            });
            ezshop.logout();

            // Check InvalidDiscountRateException
            ezshop.login("admin","password");
            assertThrows(InvalidDiscountRateException.class, () -> {
                ezshop.applyDiscountRateToSale(transactionId, -0.01);
            });
            assertThrows(InvalidDiscountRateException.class, () -> {
                ezshop.applyDiscountRateToSale(transactionId, -100.00);
            });
            assertThrows(InvalidDiscountRateException.class, () -> {
                ezshop.applyDiscountRateToSale(transactionId, 1.00);
            });
            assertThrows(InvalidDiscountRateException.class, () -> {
                ezshop.applyDiscountRateToSale(transactionId, 100.00);
            });
            ezshop.logout();

            // Check if the SaleTransaction does not exist or identify a started or closed but not already payed transaction
            ezshop.login("admin","password");
            assertFalse(ezshop.applyDiscountRateToSale(transactionId + 1, 0.10));
            assertFalse(ezshop.applyDiscountRateToSale(transactionIdPayed, 0.10));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            assertTrue(ezshop.applyDiscountRateToSale(transactionId, 0.10));
            ezshop.endSaleTransaction(transactionId);
            assertEquals(0.10, ezshop.getSaleTransaction(transactionId).getDiscountRate(), 0.01);
            assertEquals(productQuantityInSale * 1.25 * (1 - 0.10), ezshop.getSaleTransaction(transactionId).getPrice(), 0.01);
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testComputePointsForSale() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId1 = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            Integer productId2 = ezshop.createProductType("Fusilli Barilla", "012345678912", 1.50, null);
            ezshop.updatePosition(productId1,"1-a-1");
            ezshop.updatePosition(productId2,"1-a-2");
            ezshop.updateQuantity(productId1, 10);
            ezshop.updateQuantity(productId2, 20);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.computePointsForSale(transactionId);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.computePointsForSale(null);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.computePointsForSale(0);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.computePointsForSale(-1);
            });
            ezshop.logout();

            // Check if the SaleTransaction does not exist
            ezshop.login("admin","password");
            assertEquals(-1, ezshop.computePointsForSale(transactionId + 1));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            ezshop.addProductToSale(transactionId, "5701234567899", 5);
            assertEquals((int)((5 * 1.25) / 10), ezshop.computePointsForSale(transactionId));
            ezshop.addProductToSale(transactionId, "012345678912", 10);
            assertEquals((int)((5 * 1.25 + 10 * 1.50) / 10), ezshop.computePointsForSale(transactionId));
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testEndSaleTransaction() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionIdClosed = ezshop.startSaleTransaction();
            ezshop.endSaleTransaction(transactionIdClosed);
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            ezshop.updateQuantity(productId, 10);
            ezshop.addProductToSale(transactionId, "5701234567899", 2);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.endSaleTransaction(transactionId);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.endSaleTransaction(null);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.endSaleTransaction(0);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.endSaleTransaction(-1);
            });
            ezshop.logout();

            // Check if the SaleTransaction does not exist or not identify a started transaction
            ezshop.login("admin","password");
            assertFalse(ezshop.endSaleTransaction(transactionId + 1));
            assertFalse(ezshop.endSaleTransaction(transactionIdClosed));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            assertTrue(ezshop.endSaleTransaction(transactionId));
            assertEquals(Integer.valueOf(8), ezshop.getProductTypeByBarCode("5701234567899").getQuantity());
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDeleteSaleTransaction() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionIdPayed = ezshop.startSaleTransaction();
            ezshop.endSaleTransaction(transactionIdPayed);
            ezshop.receiveCashPayment(transactionIdPayed, 10.00);
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            ezshop.updateQuantity(productId, 10);
            ezshop.addProductToSale(transactionId, "5701234567899", 2);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.deleteSaleTransaction(transactionId);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.deleteSaleTransaction(null);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.deleteSaleTransaction(0);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.deleteSaleTransaction(-1);
            });
            ezshop.logout();

            // Check if the SaleTransaction does not exist or identify a payed transaction
            ezshop.login("admin","password");
            assertFalse(ezshop.deleteSaleTransaction(transactionId + 1));
            assertFalse(ezshop.deleteSaleTransaction(transactionIdPayed));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            assertTrue(ezshop.deleteSaleTransaction(transactionId));
            assertEquals(Integer.valueOf(10), ezshop.getProductTypeByBarCode("5701234567899").getQuantity());
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetSaleTransaction() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            ezshop.updateQuantity(productId, 10);
            ezshop.addProductToSale(transactionId, "5701234567899", 2);
            ezshop.endSaleTransaction(transactionId);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.getSaleTransaction(transactionId);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.getSaleTransaction(null);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.getSaleTransaction(0);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.getSaleTransaction(-1);
            });
            ezshop.logout();

            // Check if the SaleTransaction does not exist or not identify a closed transaction
            ezshop.login("admin","password");
            assertNull(ezshop.getSaleTransaction(transactionId + 1));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            assertNotNull(ezshop.getSaleTransaction(transactionId));
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testReceiveCashPayment() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            ezshop.updateQuantity(productId, 10);
            ezshop.addProductToSale(transactionId, "5701234567899", 2);
            ezshop.endSaleTransaction(transactionId);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.receiveCashPayment(transactionId, 2.50);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.receiveCashPayment(null, 2.50);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.receiveCashPayment(0, 2.50);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.receiveCashPayment(-1, 2.50);
            });
            ezshop.logout();

            // Check InvalidPaymentException
            ezshop.login("admin","password");
            assertThrows(InvalidPaymentException.class, () -> {
                ezshop.receiveCashPayment(transactionId, 0.00);
            });
            assertThrows(InvalidPaymentException.class, () -> {
                ezshop.receiveCashPayment(transactionId, -0.01);
            });
            ezshop.logout();

            // Check if the SaleTransaction does not exist
            ezshop.login("admin","password");
            assertEquals(-1, ezshop.receiveCashPayment(transactionId + 1, 2.50), 0.00);
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            double currentBalance = ezshop.computeBalance();
            assertEquals(5.01 - ezshop.getSaleTransaction(transactionId).getPrice(), ezshop.receiveCashPayment(transactionId, 5.01), 0.01);
            assertEquals(currentBalance + 2.50, ezshop.computeBalance(), 0.01);
            assertEquals(Integer.valueOf(8), ezshop.getProductTypeByBarCode("5701234567899").getQuantity());
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testReceiveCreditCardPayment() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            ezshop.updateQuantity(productId, 10);
            ezshop.addProductToSale(transactionId, "5701234567899", 2);
            ezshop.endSaleTransaction(transactionId);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.receiveCreditCardPayment(transactionId, "4485370086510891");
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.receiveCreditCardPayment(null, "4485370086510891");
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.receiveCreditCardPayment(0, "4485370086510891");
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.receiveCreditCardPayment(-1, "4485370086510891");
            });
            ezshop.logout();

            // Check InvalidCreditCardException
            ezshop.login("admin","password");
            assertThrows(InvalidCreditCardException.class, () -> {
                ezshop.receiveCreditCardPayment(transactionId, null);
            });
            assertThrows(InvalidCreditCardException.class, () -> {
                ezshop.receiveCreditCardPayment(transactionId, "");
            });
            assertThrows(InvalidCreditCardException.class, () -> {
                ezshop.receiveCreditCardPayment(transactionId, "1234");
            });
            ezshop.logout();

            // Check if the SaleTransaction does not exist
            ezshop.login("admin","password");
            assertFalse(ezshop.receiveCreditCardPayment(transactionId + 1, "4485370086510891"));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            double currentBalance = ezshop.computeBalance();
            assertTrue(ezshop.receiveCreditCardPayment(transactionId, "4485370086510891"));
            assertEquals(currentBalance + 2.50, ezshop.computeBalance(), 0.01);
            assertEquals(Integer.valueOf(8), ezshop.getProductTypeByBarCode("5701234567899").getQuantity());
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testStartReturnTransaction() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionIdInProgress = ezshop.startSaleTransaction();
            Integer transactionIdClosed = ezshop.startSaleTransaction();
            ezshop.endSaleTransaction(transactionIdClosed);
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            ezshop.updateQuantity(productId, 10);
            ezshop.addProductToSale(transactionId, "5701234567899", 4);
            ezshop.endSaleTransaction(transactionId);
            ezshop.receiveCashPayment(transactionId, 1.25 * 4);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.startReturnTransaction(transactionId);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.startReturnTransaction(null);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.startReturnTransaction(0);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.startReturnTransaction(-1);
            });
            ezshop.logout();

            // Check if the SaleTransaction is not available
            ezshop.login("admin","password");
            assertEquals(Integer.valueOf(-1), ezshop.startReturnTransaction(transactionId + 1));
            ezshop.logout();

            // Check if the SaleTransaction has not already been sold and payed
            ezshop.login("admin","password");
            assertEquals(Integer.valueOf(-1), ezshop.startReturnTransaction(transactionIdInProgress));
            assertEquals(Integer.valueOf(-1), ezshop.startReturnTransaction(transactionIdClosed));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            assertTrue(ezshop.startReturnTransaction(transactionId) >= 0);
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testReturnProduct() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId1 = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId1,"1-a-1");
            ezshop.updateQuantity(productId1, 10);
            ezshop.addProductToSale(transactionId, "5701234567899", 4);
            ezshop.endSaleTransaction(transactionId);
            ezshop.receiveCashPayment(transactionId, 1.25 * 4);
            Integer returnId = ezshop.startReturnTransaction(transactionId);
            Integer productId2 = ezshop.createProductType("Fusilli Barilla", "012345678912", 1.50, null);
            ezshop.updatePosition(productId2,"1-a-2");
            ezshop.updateQuantity(productId2, 20);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.returnProduct(returnId, "5701234567899", 1);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.returnProduct(null, "5701234567899", 1);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.returnProduct(0, "5701234567899", 1);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.returnProduct(-1, "5701234567899", 1);
            });
            ezshop.logout();

            // Check InvalidProductCodeException
            ezshop.login("admin","password");
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.returnProduct(returnId, null, 1);
            });
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.returnProduct(returnId, "", 1);
            });
            assertThrows(InvalidProductCodeException.class, () -> {
                ezshop.returnProduct(returnId, "0123", 1);
            });
            ezshop.logout();

            // Check InvalidQuantityException
            ezshop.login("admin","password");
            assertThrows(InvalidQuantityException.class, () -> {
                ezshop.returnProduct(returnId, "5701234567899", 0);
            });
            assertThrows(InvalidQuantityException.class, () -> {
                ezshop.returnProduct(returnId, "5701234567899", -1);
            });
            ezshop.logout();

            // Check if the ReturnTransaction does not exist
            ezshop.login("admin","password");
            assertFalse(ezshop.returnProduct(returnId + 1, "5701234567899", 1));
            ezshop.logout();

            // Check if the product to be returned does not exists
            ezshop.login("admin","password");
            assertFalse(ezshop.returnProduct(returnId, "3033710074365", 1));
            ezshop.logout();

            // Check if the product was not in the transaction
            ezshop.login("admin","password");
            assertFalse(ezshop.returnProduct(returnId, "012345678912", 1));
            ezshop.logout();

            // Check if the amount is higher than the one in the sale transaction
            ezshop.login("admin","password");
            assertFalse(ezshop.returnProduct(returnId, "5701234567899", 10));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            assertTrue(ezshop.returnProduct(returnId, "5701234567899", 1));
            assertEquals(Integer.valueOf(6), ezshop.getProductTypeByBarCode("5701234567899").getQuantity());
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testEndReturnTransaction() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            ezshop.updateQuantity(productId, 10);
            ezshop.addProductToSale(transactionId, "5701234567899", 4);
            ezshop.endSaleTransaction(transactionId);
            ezshop.receiveCashPayment(transactionId, 1.25 * 4);
            Integer returnIdClosed = ezshop.startReturnTransaction(transactionId);
            ezshop.endReturnTransaction(returnIdClosed, true);
            Integer returnIdPayed = ezshop.startReturnTransaction(transactionId);
            ezshop.endReturnTransaction(returnIdPayed, true);
            ezshop.returnCashPayment(returnIdPayed);
            Integer returnId = ezshop.startReturnTransaction(transactionId);
            ezshop.returnProduct(returnId, "5701234567899", 1);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.endReturnTransaction(returnId, true);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.endReturnTransaction(null, true);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.endReturnTransaction(0, true);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.endReturnTransaction(-1, true);
            });
            ezshop.logout();

            // Check if the ReturnTransaction does not exist
            ezshop.login("admin","password");
            assertFalse(ezshop.endReturnTransaction(returnId + 1, true));
            ezshop.logout();

            // Check if the ReturnTransaction is not in progress
            ezshop.login("admin","password");
            assertFalse(ezshop.endReturnTransaction(returnIdClosed, true));
            assertFalse(ezshop.endReturnTransaction(returnIdPayed, true));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            assertTrue(ezshop.endReturnTransaction(returnId, true));
            assertEquals(Integer.valueOf(7), ezshop.getProductTypeByBarCode("5701234567899").getQuantity());
            assertEquals(1.25 * 3, ezshop.getSaleTransaction(transactionId).getPrice(), 0.01);
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDeleteReturnTransaction() {
        try {
            // Setup
            ezshop.login("admin","password");
            Integer transactionId = ezshop.startSaleTransaction();
            Integer productId = ezshop.createProductType("Spaghetti Barilla", "5701234567899", 1.25, null);
            ezshop.updatePosition(productId,"1-a-1");
            ezshop.updateQuantity(productId, 10);
            ezshop.addProductToSale(transactionId, "5701234567899", 4);
            ezshop.endSaleTransaction(transactionId);
            ezshop.receiveCashPayment(transactionId, 1.25 * 4);
            Integer returnIdInProgress = ezshop.startReturnTransaction(transactionId);
            Integer returnIdPayed = ezshop.startReturnTransaction(transactionId);
            ezshop.endReturnTransaction(returnIdPayed, true);
            ezshop.returnCashPayment(returnIdPayed);
            Integer returnId = ezshop.startReturnTransaction(transactionId);
            ezshop.returnProduct(returnId, "5701234567899", 1);
            ezshop.endReturnTransaction(returnId, true);

            // Check UnauthorizedException
            ezshop.logout();
            assertThrows(UnauthorizedException.class, () -> {
                ezshop.deleteReturnTransaction(returnId);
            });

            // Check InvalidTransactionIdException
            ezshop.login("admin","password");
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.deleteReturnTransaction(null);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.deleteReturnTransaction(0);
            });
            assertThrows(InvalidTransactionIdException.class, () -> {
                ezshop.deleteReturnTransaction(-1);
            });
            ezshop.logout();

            // Check if the ReturnTransaction does not exist
            ezshop.login("admin","password");
            assertFalse(ezshop.deleteReturnTransaction(returnId + 1));
            ezshop.logout();

            // Check if the ReturnTransaction is not in progress
            ezshop.login("admin","password");
            assertFalse(ezshop.deleteReturnTransaction(returnIdPayed));
            assertFalse(ezshop.deleteReturnTransaction(returnIdInProgress));
            ezshop.logout();

            // Check if the operation is successful
            ezshop.login("admin","password");
            assertTrue(ezshop.deleteReturnTransaction(returnId));
            assertEquals(Integer.valueOf(6), ezshop.getProductTypeByBarCode("5701234567899").getQuantity());
            ezshop.logout();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
