package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;

public class UserImpl implements User{

    private Integer id;
    private String username;
    private String password;
    private RoleType role;

    private enum RoleType {cashier, administrator, shopmanager};

    public UserImpl(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException{
        if(username.isBlank())
            throw new InvalidUsernameException();
        if(password.isBlank())
            throw new InvalidPasswordException();
        if(role.isBlank())
            throw new InvalidRoleException();
        this.username = username;
        this.password = password;
        try {
            this.role = RoleType.valueOf(role.toLowerCase());
        } catch(IllegalArgumentException e) {
            throw new InvalidRoleException();
        }
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
    public void setUsername(String username) throws InvalidUsernameException {
        if(username.isBlank())
            throw new InvalidUsernameException();
        this.username = username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) throws InvalidPasswordException {
        if(password.isBlank())
            throw new InvalidPasswordException();
        this.password = password;
    }

    @Override
    public String getRole() {
        return this.role.toString();
    }

    @Override
    public void setRole(String role) throws InvalidRoleException {
        try {
            this.role = RoleType.valueOf(role.toLowerCase());
        } catch(IllegalArgumentException e) {
            throw new InvalidRoleException();
        }
    }

    //Permission for FR1
    public boolean canManageUsers(){
        boolean permission;
        switch (role) {
            case administrator: permission = true; break;
            case cashier: permission = false; break;
            case shopmanager: permission = false; break;
            default: permission = false;
        };
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
        };
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
        };
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
        };
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
        };
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
        };
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
        };
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
        };
        return permission;
    }
}
