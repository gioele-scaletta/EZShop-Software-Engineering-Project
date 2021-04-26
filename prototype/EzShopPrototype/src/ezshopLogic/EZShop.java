package ezshopLogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.Exception;

public class EZShop {

    private List<Customer> customers;
    private List<Order> orders;
    private List<SaleTransaction> salesList;
    private List<ReturnTransaction> returnsList;
    private List<BalanceOperation> balanceOperationsList;
    private List<User> usersList;

    private User loggedIn;
    private Integer latestUserID;

    public EZShop(){
        this.customers = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.returnsList = new ArrayList<>();
        this.balanceOperationsList = new ArrayList<>();
        this.usersList = new ArrayList<>();
        this.loggedIn = null;
        this.latestUserID = 1;
    }


    public Integer createUser(String username, String password, String role) throws Exception /*throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException*/ {
        if(username.isBlank() || username == null) {
            System.out.println("Username invalido");
            //throw new InvalidUsernameException();
            return -1;
        }
        if(password.isBlank() || password == null) {
            System.out.println("Password invalida");
            //throw new InvalidPasswordException;
            return -1;
        }
        for (User u : usersList) {
            if (username.equals(u.getUsername())) {
                System.out.println("Esiste già un utente con l'username " + username);
                return -1;
            }
        }
        usersList.add(new User(latestUserID, username, password, role));
        return latestUserID++;
    }

    public boolean deleteUser(Integer id) /*throws InvalidUserIdException, UnauthorizedException*/
    {
        if(!loggedIn.canManageUsers()) {
            System.out.println("User " + loggedIn.getUsername() + " has no permission to delete users");
            //throw new UnauthorizedException();
            return false;
        }
        User u = this.getUser(id);
        System.out.println("User " + u.getUsername() + "has been deleted");
        usersList.remove(u);
        return true;
    }

    public List<User> getAllUsers() /*throws UnauthorizedException*/{
        if(!loggedIn.canManageUsers()) {
            System.out.println("User " + loggedIn.getUsername() + " has no permission to get users list");
            //throw new UnauthorizedException();
            return null;
        }
        return usersList;
    }

    public User getUser(Integer id) /*throws InvalidUserIdException, UnauthorizedException*/ {
        if(!loggedIn.canManageUsers()) {
            System.out.println("User " + loggedIn.getUsername() + " has no permission to search for users");
            //throw new UnauthorizedException();
            return null;
        }
        for (User u : usersList) {
            if (id.equals(u.getID())) {
                System.out.println("User " + u.getUsername() + " found");
                return u;
            }
        }
        System.out.println("There's no user with id " + id);
        //throw new InvalidUserIdException();
        return null;
    }

    public boolean updateUserRights(Integer id, String role) /*throws InvalidUserIdException, InvalidRoleException, UnauthorizedException*/ {
        if(!loggedIn.canManageUsers()) {
            System.out.println("User " + loggedIn.getUsername() + " has no permission to update user rights");
            //throw new UnauthorizedException();
            return false;
        }
        User u = this.getUser(id);
        u.setRole(role);
        System.out.println("User " + u.getUsername() + " has been assigned to role " + u.getRole().toString());
        return true;
    }

    public User login(String username, String password) /*throws InvalidUsernameException, InvalidPasswordException*/ {
        for (User u : usersList) {
            if (u.getUsername().equals(username)) {
                if (u.getPassword().equals(password)) {
                    loggedIn = u;
                    System.out.println("Login of user " + u.getUsername() + " successful");
                    return u;
                } else {
                    System.out.println("Incorrect Password");
                    //throw new InvalidPasswordException();
                    return null;
                }
            }
        }
        System.out.println("There's no user with username " + username);
        return null;
    }

    public boolean logout() {
        if(loggedIn == null) {
            System.out.println("No user has done login, so it's not possible to perform a logout");
            return false;
        }
        System.out.println("Logout of user " + loggedIn.getUsername() + " successful");
        loggedIn = null;
        return true;
    }

}
