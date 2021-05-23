package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.model.*;
import it.polito.ezshop.exceptions.*;
import static org.junit.Assert.*;

import org.junit.*;

import java.util.ArrayList;
import java.util.List;

public class TestEZShop {

    private static EZShop ezshop;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ezshop = new EZShop();
    }

    @AfterClass
    public static void cleanUpAfterClass(){
        ezshop.reset();
    }

    @Before
    public void setUp() throws Exception {
        ezshop.reset();
        //ezshop.logout();
        ezshop.createUser("admin","password","Administrator");
        ezshop.createUser("shopmanager","password","ShopManager");
        ezshop.createUser("cashier","password","Cashier");
    }

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
        if(u1.getId().equals(u2.getId()) ||
        u1.getId().equals(u2.getId()) ||
                u1.getRole().equals(u2.getRole()) ||
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
            assertTrue(compareUsers(u1,u2));

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
            ProductType p1= new ProductTypeImpl(id1, "spaghetti", "5701234567899", 5.0, "nota" );
            ProductType p2= new ProductTypeImpl(id2, "spaghettini", "9780072125757", 2.5, "note" );
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
        if(p1.getId().equals(p2.getId()) ||
                p1.getBarCode().equals(p2.getBarCode()) ||
                p1.getNote().equals(p2.getNote()) ||
                p1.getProductDescription().equals(p2.getProductDescription()))
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

    ProductType p= new ProductTypeImpl(id1, "spaghetti", "5701234567899", 5.0, "nota" );
    ProductType p1= new ProductTypeImpl(id2, "spaghettini", "9780072125757", 2.5, "note" );
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
    public void testGetproductByDescription(){
        try{
            ezshop.login("shopmanager", "password");
            // create test prod
        Integer id1= ezshop.createProductType("spaghetti", "5701234567899", 5.0, "nota" );
        Integer id2=ezshop.createProductType("spaghettini", "9780072125757", 2.5, "note" );
        Integer id3=ezshop.createProductType("kinder", "4012345678901", 2.5, "note" );

            List<ProductType> list = new ArrayList<>();

            ProductType p1= new ProductTypeImpl(id1, "spaghetti", "5701234567899", 5.0, "nota" );
            ProductType p2= new ProductTypeImpl(id2, "spaghettini", "9780072125757", 2.5, "note" );
            ProductType p3= new ProductTypeImpl(id3, "kinder", "4012345678901", 2.5, "note" );
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


/*
    @Test
    void testDefineCustomer() {

        try {

            // First customer
            Integer id1 = ezshop.defineCustomer("Name1");
            Integer expected1 = 1;

            // Second customer
            Integer id2 = ezshop.defineCustomer("Name2");
            Integer expected2 = 2;

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
    void testGetCustomer() {

        try {

            // Get customer with id = id1
            Integer id1 = ezshop.defineCustomer("Name1");
            CustomerImpl c1 = ezshop.getCustomer(id1);
            CustomerImpl expected1 = new CustomerImpl("Name1", "", id1, 0);


            // Get customer with id = id2
            Integer id2 = ezshop.defineCustomer("Name2");
            CustomerImpl c2 = ezshop.getCustomer(id2);
            CustomerImpl expected2 = new CustomerImpl("Name2", "", id2, 0);

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
    void testDeleteCustomer() {

        try {

            // Delete customer with id = id1
            Integer id1 = ezshop.defineCustomer("Name1");
            Boolean deleted1 = ezshop.deleteCustomer(id1);

            // Delete customer with id = id1
            Integer id2 = ezshop.defineCustomer("Name2");
            Boolean deleted2 = ezshop.deleteCustomer(id2);

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
    void testGetAllCustomers() {

        try {

            // The list of customers is empty
            List<Customer> list1 = ezshop.getAllCustomers();
            assertTrue (list1.size() == 0);

            // The list of customers has two customers
            Integer id1 = ezshop.defineCustomer("Name1");
            Integer id2 = ezshop.defineCustomer("Name2");
            List<Customer> list2 = ezshop.getAllCustomers();

            assertTrue (list2.size() == 2);

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
    void testModifyCustomer() {

        try {

            // Modify only customer card
            Integer id1 = ezshop.defineCustomer("Name1");
            Boolean modified1 = ezshop.modifyCustomer( id1,"Name1", "0000000010");
            String card1 = ezshop.getCustomer(id1).getCustomerCard();
            String card1Expected = "0000000010";

            // Modify customer name and detach customer card
            Integer id2 = ezshop.defineCustomer("Name2");
            ezshop.modifyCustomer( id2,"Name2", "0000000020");
            ezshop.modifyPointsOnCard("0000000020",5);
            Integer points1 = ezshop.getCustomer(id2).getPoints();
            Integer points1Expected = 5;

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
    void testAttachCardToCustomer() {

        try {

            // The customer with id=id1 has not a card assigned
            Integer id1 = ezshop.defineCustomer("Name1");
            Boolean attached1 = ezshop.attachCardToCustomer("0000000001", id1);

            // The customer with id=id2 has not a card assigned
            Integer id2 = ezshop.defineCustomer("Name2");
            Boolean attached2 = ezshop.attachCardToCustomer("0000000002", id2);

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
    void testCreateCard() {

        try {

            // First card in the database
            String card1 = ezshop.createCard();
            String expected1 = "0000000000";

            // The value "0000000000" of card1 is not attached to a customer, so createCard() returns again "0000000000"
            String card2 = ezshop.createCard();
            String expected2 = "0000000000";

            Integer id2 = ezshop.defineCustomer("Name2");
            ezshop.attachCardToCustomer(card2, id2);

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
    void testModifyPointsOnCard() {

        try {


            Integer id1 = ezshop.defineCustomer("Name1");
            ezshop.attachCardToCustomer("0000000001", id1);
            Boolean modifiedPoints1 = ezshop.modifyPointsOnCard("0000000001", 1);

            Integer id2 = ezshop.defineCustomer("Name2");
            ezshop.attachCardToCustomer("0000000002", id2);
            Boolean modifiedPoints2 = ezshop.modifyPointsOnCard("0000000002", 1);

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
    */
}
