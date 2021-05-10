package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;

import javax.management.relation.Role;

public class UserImpl implements User{

    private Integer id;
    private String username;
    private String password;
    private RoleType role;


    private enum RoleType {cashier, administrator, shopmanager}

    public UserImpl(Integer id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = RoleType.valueOf(role.toLowerCase());
    }

    public UserImpl(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = RoleType.valueOf(role.toLowerCase());
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getUsername(){
        return this.username;
    }

    @Override
    public void setUsername(String username) { this.username = username; }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) { this.password = password; }

    @Override
    public String getRole() {
        return this.role.toString();
    }

    @Override
    public void setRole(String role) { this.role = RoleType.valueOf(role.toLowerCase()); }

    @Override
    public String toString() {
        return "UserImpl{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    public static boolean isAllowedRole(String role) {
        try {
            RoleType r = RoleType.valueOf(role.toLowerCase());
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    //Permission for FR1
    public boolean canManageUsers(){
        boolean permission;
        switch (role) {
            case administrator: permission = true; break;
            case cashier: permission = false; break;
            case shopmanager: permission = false; break;
            default: permission = false;
        }
        return permission;
    }

    //Permission for FR3
    public boolean canManageProductList(){
        boolean permission;
        switch (role) {
            case administrator: permission = true; break;
            case cashier: permission = true; break;
            case shopmanager: permission = false; break;
            default: permission = false;
        }
        return permission;
    }

    //Permission for FR3.3
    public boolean canListProducts(){
        boolean permission;
        switch (role) {
            case administrator: permission = true; break;
            case cashier: permission = true; break;
            case shopmanager: permission = true; break;
            default: permission = false;
        }
        return permission;
    }

    //Permission for FR4
    public boolean canManageInventory(){
        boolean permission;
        switch (role) {
            case administrator: permission = true; break;
            case cashier: permission = true; break;
            case shopmanager: permission = false; break;
            default: permission = false;
        }
        return permission;
    }

    //Permission for FR5
    public boolean canManageCustomers(){
        boolean permission;
        switch (role) {
            case administrator: permission = true; break;
            case cashier: permission = true; break;
            case shopmanager: permission = true; break;
            default: permission = false;
        }
        return permission;
    }

    //Permission for FR6
    public boolean canManageSaleTransactions(){
        boolean permission;
        switch (role) {
            case administrator: permission = true; break;
            case cashier: permission = true; break;
            case shopmanager: permission = true; break;
            default: permission = false;
        }
        return permission;
    }


    //Permission for FR7
    public boolean canManagePayments(){
        boolean permission;
        switch (role) {
            case administrator: permission = true; break;
            case cashier: permission = true; break;
            case shopmanager: permission = true; break;
            default: permission = false;
        }
        return permission;
    }

    //Permission for FR8
    public boolean canManageAccounting(){
        boolean permission;
        switch (role) {
            case administrator: permission = true; break;
            case cashier: permission = true; break;
            case shopmanager: permission = false; break;
            default: permission = false;
        }
        return permission;
    }
}
