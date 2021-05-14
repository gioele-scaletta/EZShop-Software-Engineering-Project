package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class testUserImpl {

    String admin_role="Administrator";
    String cashier_role="Cashier";
    String shop_manager_role="ShopManager";
    String mispelled_admin_role="administrator";
    String error_empty_string="";
    String error_null_string=null;

    UserImpl userad=null;
    UserImpl usersm=null;
    UserImpl userc=null;

    @Test
    public void testisAllowedRole() {

        assertFalse(UserImpl.isAllowedRole(mispelled_admin_role));
        assertFalse(UserImpl.isAllowedRole(error_empty_string));
        assertFalse(UserImpl.isAllowedRole(error_null_string));
        assertTrue(UserImpl.isAllowedRole(shop_manager_role));
        assertTrue(UserImpl.isAllowedRole(cashier_role));
        assertTrue(UserImpl.isAllowedRole(admin_role));

    }

    @Test
    public void testConstructorGetters(){
        userad =new UserImpl(1, "gioela", "scaletti",shop_manager_role );
        usersm =new UserImpl(2, "marco", "raglyo", shop_manager_role);
        userc =new UserImpl(3, "stefano", "rossi", cashier_role);

        userad.setUsername("gioele");
        assertTrue(userad.getUsername().equals("gioele"));
        userad.setPassword("scaletta");
        assertTrue(userad.getPassword().equals("scaletta"));
        userad.setRole(admin_role);
        userad.getRole().equals(admin_role);

    }

    @Test
    public void testcanManageUsers() {

        assertTrue(userad.canManageUsers());
        assertFalse(usersm.canManageUsers());
        assertFalse(userc.canManageUsers());

    }

    @Test
    public void testcanManageProductList() {

        assertTrue(userad.canManageProductList());
        assertTrue(usersm.canManageProductList());
        assertFalse(userc.canManageProductList());

    }

    @Test
    public void testcanManageInventory() {

        assertTrue(userad.canManageInventory());
        assertTrue(usersm.canManageInventory());
        assertFalse(userc.canManageInventory());

    }

    @Test
    public void testcanManageAccounting() {

        assertTrue(userad.canManageAccounting());
        assertTrue(usersm.canManageAccounting());
        assertFalse(userc.canManageAccounting());

    }

    @Test
    public void testcanManageCustomers() {

        assertTrue(userad.canManageCustomers());
        assertTrue(usersm.canManageCustomers());
        assertTrue(userc.canManageCustomers());

    }

    @Test
    public void testcanManageSaleTransactions() {

        assertTrue(userad.canManageSaleTransactions());
        assertTrue(usersm.canManageSaleTransactions());
        assertTrue(userc.canManageSaleTransactions());

    }


    @Test
    public void testcanManagePayments() {

        assertTrue(userad.canManagePayments());
        assertTrue(usersm.canManagePayments());
        assertTrue(userc.canManagePayments());

    }

}
