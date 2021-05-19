package it.polito.ezshop.unitTest;
import org.junit.Before;
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

    @Before
    public void testUserConstructor() {

        usersm =new UserImpl(2, "marco", "raglyo", shop_manager_role);
        userc =new UserImpl(3, "stefano", "rossi", cashier_role);
        userad =new UserImpl(1, "gioela", "scaletti",shop_manager_role );


        assertTrue(userad.toString().equals("UserImpl{" +
                "id=" + 1 +
                ", username='" + "gioela" + '\'' +
                ", password='" +"scaletti" + '\'' +
                ", role=" + shop_manager_role +
                '}'));


    }

    @Test
    public void testSetUsername(){
        userad.setUsername("gioele");
        assertTrue(userad.getUsername().equals("gioele"));
        userad.setUsername(null);
        assertTrue(userad.getUsername().equals("gioele"));
    }

    @Test
    public void testSetPassword(){
        userad.setPassword("scaletta");
        assertTrue(userad.getPassword().equals("scaletta"));
        userad.setPassword(null);
        assertTrue(userad.getPassword().equals("scaletta"));
    }

    @Test
    public void testSetRole() {
        userad.setRole(admin_role);
        userad.getRole().equals(admin_role);
        userad.setRole(null);
        userad.getRole().equals(admin_role);
    }

    @Test
    public void testSetId(){
        userad.setId(1);
        assertTrue(userad.getId().equals(1));
        userad.setId(null);
        assertTrue(userad.getId().equals(1));

    }


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
    public void testCanManageUsers() {

        userad =new UserImpl(1, "gioela", "scaletti",admin_role );
        usersm =new UserImpl(2, "marco", "raglyo", shop_manager_role);
        userc =new UserImpl(3, "stefano", "rossi", cashier_role);

        assertTrue(userad.canManageUsers());
        assertFalse(usersm.canManageUsers());
        assertFalse(userc.canManageUsers());


    }

    @Test
    public void testCanManageProductList() {


        userad =new UserImpl(1, "gioela", "scaletti",admin_role );
        usersm =new UserImpl(2, "marco", "raglyo", shop_manager_role);
        userc =new UserImpl(3, "stefano", "rossi", cashier_role);

        assertTrue(userad.canManageProductList());
        assertTrue(usersm.canManageProductList());
        assertFalse(userc.canManageProductList());

    }

    @Test
    public void testCanManageInventory() {

        userad =new UserImpl(1, "gioela", "scaletti",admin_role );
        usersm =new UserImpl(2, "marco", "raglyo", shop_manager_role);
        userc =new UserImpl(3, "stefano", "rossi", cashier_role);


        assertTrue(userad.canManageInventory());
        assertTrue(usersm.canManageInventory());
        assertFalse(userc.canManageInventory());

    }

    @Test
    public void testCanManageAccounting() {

        userad =new UserImpl(1, "gioela", "scaletti",admin_role );
        usersm =new UserImpl(2, "marco", "raglyo", shop_manager_role);
        userc =new UserImpl(3, "stefano", "rossi", cashier_role);

        assertTrue(userad.canManageAccounting());
        assertTrue(usersm.canManageAccounting());
        assertFalse(userc.canManageAccounting());

    }

    @Test
    public void testCanManageCustomers() {

        userad =new UserImpl(1, "gioela", "scaletti",admin_role );
        usersm =new UserImpl(2, "marco", "raglyo", shop_manager_role);
        userc =new UserImpl(3, "stefano", "rossi", cashier_role);

        assertTrue(userad.canManageCustomers());
        assertTrue(usersm.canManageCustomers());
        assertTrue(userc.canManageCustomers());

    }

    @Test
    public void testCanManageSaleTransactions() {

        userad =new UserImpl(1, "gioela", "scaletti",admin_role );
        usersm =new UserImpl(2, "marco", "raglyo", shop_manager_role);
        userc =new UserImpl(3, "stefano", "rossi", cashier_role);

        assertTrue(userad.canManageSaleTransactions());
        assertTrue(usersm.canManageSaleTransactions());
        assertTrue(userc.canManageSaleTransactions());

    }


    @Test
    public void testCanManagePayments() {

        userad =new UserImpl(1, "gioela", "scaletti",admin_role );
        usersm =new UserImpl(2, "marco", "raglyo", shop_manager_role);
        userc =new UserImpl(3, "stefano", "rossi", cashier_role);

        assertTrue(userad.canManagePayments());
        assertTrue(usersm.canManagePayments());
        assertTrue(userc.canManagePayments());

    }

    @Test
    public void testCanListProducts() {

        userad =new UserImpl(1, "gioela", "scaletti",admin_role );
        usersm =new UserImpl(2, "marco", "raglyo", shop_manager_role);
        userc =new UserImpl(3, "stefano", "rossi", cashier_role);

        assertTrue(userad.canListProducts());
        assertTrue(usersm.canListProducts());
        assertTrue(userc.canListProducts());

    }

}
