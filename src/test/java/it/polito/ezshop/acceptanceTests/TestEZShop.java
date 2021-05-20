package it.polito.ezshop.acceptanceTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestEZShop {

    private static EZShop ezshop;

/*
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        ezshop = new EZShop();
    }

    @BeforeEach
    void setUp() throws Exception {
        ezshop.reset();
        ezshop.login("giovanni", "password"); //administrator
    }


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
