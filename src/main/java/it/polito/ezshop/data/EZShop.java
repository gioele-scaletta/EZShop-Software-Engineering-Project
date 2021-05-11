package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;

//import javax.persistence.criteria.CriteriaBuilder;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.io.*;


public class EZShop implements EZShopInterface {

    /* NO MORE NEEDED SINCE WE USE DB
    private List<Customer> customers;
    private List<OrderImpl> orders;
    private List<SaleTransactionImpl> salesList;
    private List<ReturnTransactionImpl> returnsList;
    private List<BalanceOperationImpl> balanceOperationsList;
    private List<UserImpl> usersList;
    private List<ProductTypeImpl> productsList;
    private List<LoyaltyCardImpl> cardsList;


     */
    private UserImpl loggedIn;
    private Integer latestUserID;
    private Integer latestProductTypeID;
    private Integer flag =0;
    SaleTransactionImpl currentsale;
    Connection conn;

    public EZShop() {
       /* NO MORE NEEDED SINCE WE USE DB
        this.customers = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.returnsList = new ArrayList<>();
        this.balanceOperationsList = new ArrayList<>();
        this.usersList = new ArrayList<>();
        this.productsList = new ArrayList<>(); */
        this.loggedIn = null;
        this.latestUserID = 1;
        this.latestProductTypeID = 1;

        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:Database.sqlite");
        } catch (SQLException throwables) {
            System.err.println("Error with db connection");
            throw new RuntimeException(throwables);
        }
        System.out.println("Connection with db ok");

    }


    @Override
    public void reset() {

    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        //Check if username is valid
        if (username.isBlank()) {
            System.out.println("Invalid username");
            throw new InvalidUsernameException();
        }
        //Check if password is valid
        if (password.isBlank()) {
            System.out.println("Invalid password");
            throw new InvalidPasswordException();
        }
        //Check if role is valid
        if(role.isBlank() || !UserImpl.isAllowedRole(role)) {
            System.out.println("Invalid role");
            throw new InvalidRoleException();
        }

        //Checking if username is already present
        String sql = "SELECT * FROM USERS AS U WHERE U.Username=?";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() != false) {
                System.out.println("User " + username + " is already present");
                return -1;
            }
        } catch (SQLException e) {
           e.printStackTrace();
           return -1;
        }

        //Calculating new ID
        Integer id;
        String sql2 = "SELECT MAX(Id) FROM USERS";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql2);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() == false)
                id = 1;
            else
                id = rs.getInt(1) + 1;

        } catch (SQLException e){
            System.err.println("Error with db connection");
            throw new RuntimeException(e);
        }

        //Inserting user
        String sql3 = "INSERT INTO USERS(Id,Username,Password,Role) VALUES(?,?,?,?)";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql3);
            pstmt.setInt(1,id);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, role);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        System.out.println("User " + username + " with role " + role + " and id " + id + " has been added to the application");
        return id;
    }



    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageUsers()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Check if id is valid
        if (id == null || id<=0) {
            System.out.println("Invalid id");
            throw new InvalidUserIdException();
        }

        //Checking if user is trying to delete himself
        if(id == loggedIn.getId()) {
            System.out.println("User cannot delete himself");
            return false;
        }

        //Deleting user
        String sql = "DELETE FROM USERS WHERE Id=?";
        int numDeleted;
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            numDeleted = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if(numDeleted == 0) {
            System.out.println("There's no user with id " + id);
            return false;
        }
        System.out.println("User with id " + id + " has been deleted successfully");
        return true;
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageUsers()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        String sql = "SELECT * FROM USERS";
        List<User> users = new ArrayList<>();
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Integer id = rs.getInt("Id");
                String username = rs.getString("Username");
                String password = rs.getString("Password");
                String role = rs.getString("Role");
                users.add(new UserImpl(id,username,password,role));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return users;
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageUsers()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Check if id is valid
        if (id == null || id<=0) {
            System.out.println("Invalid id");
            throw new InvalidUserIdException();
        }
        //Retrieving user;
        String sql = "SELECT * FROM USERS AS U WHERE U.Id=? ";
        User u;
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setInt(1,id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() == false) {
                System.out.println("User with id " + id + " is not present");
                return null;
            }
            Integer id_u = rs.getInt("Id");
            String username = rs.getString("Username");
            String password = rs.getString("Password");
            String role = rs.getString("Role");
            u = new UserImpl(id_u,username,password,role);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("Data for user with id " + id + " has been retrieved with success");
        return u;
    }

    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageUsers()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Check if id is valid
        if (id == null || id<=0) {
            System.out.println("Invalid id");
            throw new InvalidUserIdException();
        }

        //Check if role is valid
        if(role.isBlank() || !UserImpl.isAllowedRole(role)) {
            System.out.println("Invalid role");
            throw new InvalidRoleException();
        }

        //Updating user
        String sql = "UPDATE USERS SET Role=? WHERE Id=?";
        int numUpdated;
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setString(1, role);
            pstmt.setInt(2,id);
            numUpdated = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if(numUpdated == 0) {
            System.out.println("There's no user with id " + id);
            return false;
        }
        System.out.println("User with id " + id + " has now role " + role.toLowerCase());
        return true;
    }

    @Override
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        //Checking if username is null or empty
        if(username.isBlank()) {
            System.out.println("Invalid login username");
            throw new InvalidUsernameException();
        }
        //Checking if password is null or empty
        if(password.isBlank()) {
            System.out.println("Invalid login password");
            throw new InvalidPasswordException();
        }
        //Checking if there's already a logged in user
        if(this.loggedIn!=null) {
            System.out.println("User " + this.loggedIn.getUsername() + " is already logged in. Perform log out first");
            return null;
        }

        //Checking if the username is present and retrieving it
        String sql = "SELECT * FROM USERS AS U where U.Username=?";
        UserImpl userObj;
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setString(1,username);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() == false) {
                System.out.println("User " + username + " is not present");
                return null;
            }
            //Building the user object
            userObj = new UserImpl(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        //Checking if the password matches
        if(!password.equals(userObj.getPassword())) {
            System.out.println("Invalid password for the user " + username);
            return null;
        }

        //Setting the logged in user
        this.loggedIn = userObj;
        System.out.println("User " + username + " has successfully logged in");
        return userObj;
    }

    @Override
    public boolean logout() {
        if(loggedIn == null) {
            System.out.println("There's no logged in user");
            return false;
        }
        loggedIn = null;
        System.out.println("Logged out successfully");
        return true;
    }

    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageProductList()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }
        //Checking if description is null or empty
        if(description.isBlank()) {
            System.out.println("Invalid product description");
            throw new InvalidProductDescriptionException();
        }
        //Checking if barcode is null or empty and if it is valid
        if(productCode.isBlank()||!ProductTypeImpl.isValidCode(productCode)) {
            System.out.println("Invalid product code");
            throw new InvalidProductCodeException();
        }
        //Checking if pricePerUnit is >0
        if(pricePerUnit<=0) {
            System.out.println("Invalid price per unit");
            throw new InvalidPricePerUnitException();
        }

        //Checking if product barcode is already present
        String sql = "SELECT * FROM PRODUCTTYPES AS P WHERE P.BarCode=?";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setString(1, productCode);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() != false) {
                System.out.println("Product with barcode " + productCode + " is already present");
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        //Calculating new product ID
        Integer id;
        String sql2 = "SELECT MAX(productId) FROM PRODUCTTYPES";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql2);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() == false)
                id = 1;
            else
                id = rs.getInt(1) + 1;

        } catch (SQLException e){
            System.err.println("Error with db connection");
            e.printStackTrace();
            return -1;
        }

        //Inserting product
        String sql3 = "INSERT INTO PRODUCTTYPES(productId,BarCode,Description,SellPrice,Quantity,prodDiscountRate,notes) VALUES(?,?,?,?,0,0,?)";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql3);
            pstmt.setInt(1,id);
            pstmt.setString(2, productCode);
            pstmt.setString(3, description);
            pstmt.setDouble(4, pricePerUnit);
            pstmt.setString(5, (note==null) ? "" : note);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error with db connection");
            e.printStackTrace();
            return -1;
        }
        System.out.println("Product " + description + " with cost " + pricePerUnit + " and productId " + id + " has been added to the application");
        return id;
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageProductList()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Check if product id is valid
        if (id == null || id<=0) {
            System.out.println("Invalid product id");
            throw new InvalidProductIdException();
        }
        //Checking if description is null or empty
        if(newDescription.isBlank()) {
            System.out.println("Invalid product description");
            throw new InvalidProductDescriptionException();
        }
        //Checking if barcode is null or empty and if it is valid
        if(newCode.isBlank()||!ProductTypeImpl.isValidCode(newCode)) {
            System.out.println("Invalid product code");
            throw new InvalidProductCodeException();
        }
        //Checking if pricePerUnit is >0
        if(newPrice<=0) {
            System.out.println("Invalid price per unit");
            throw new InvalidPricePerUnitException();
        }

        //Checking if product barcode is already present
        String sql = "SELECT * FROM PRODUCTTYPES AS P WHERE P.BarCode=?";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setString(1, newCode);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() != false) {
                System.out.println("Product with barcode " + newCode + " is already present");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        //Updating product
        String sql2 = "UPDATE PRODUCTTYPES SET Description=?, BarCode=?, SellPrice=?, notes=? WHERE productId=?";
        int numUpdated;
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql2);
            pstmt.setString(1, newDescription);
            pstmt.setString(2, newCode);
            pstmt.setDouble(3, newPrice);
            pstmt.setString(4, newNote);
            pstmt.setInt(5,id);
            numUpdated = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if(numUpdated == 0) {
            System.out.println("There's no product with id " + id);
            return false;
        }
        System.out.println("Product with " + id + " has been updated");
        return true;
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageProductList()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Check if id is valid
        if (id == null || id<=0) {
            System.out.println("Invalid id");
            throw new InvalidProductIdException();
        }

        //Deleting user
        String sql = "DELETE FROM PRODUCTTYPES WHERE productId=?";
        int numDeleted;
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            numDeleted = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if(numDeleted == 0) {
            System.out.println("There's no product with id " + id);
            return false;
        }
        System.out.println("Product with id " + id + " has been deleted successfully");
        return true;
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canListProducts()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        String sql = "SELECT * FROM PRODUCTTYPES";
        List<ProductType> products = new ArrayList<>();
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Integer productId = rs.getInt("productId");
                String barcode = rs.getString("BarCode");
                String description = rs.getString("Description");
                Double sellPrice = rs.getDouble("SellPrice");
                Integer quantity = rs.getInt("Quantity");
                Double prodDiscountRate = rs.getDouble("prodDiscountRate");
                String notes = rs.getString("notes");
                Integer aisleId = rs.getInt("aisleID");
                String rackId = rs.getString("rackID");
                Integer levelId = rs.getInt("levelID");
                products.add(new ProductTypeImpl(productId,barcode,description,sellPrice,quantity,prodDiscountRate,notes,aisleId,rackId,levelId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return products;
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageProductList()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }
        //Checking if barcode is null or empty and if it is valid
        if(barCode.isBlank()||!ProductTypeImpl.isValidCode(barCode)) {
            System.out.println("Invalid product code");
            throw new InvalidProductCodeException();
        }

        //Retrieving product
        String sql = "SELECT * FROM PRODUCTTYPES AS P WHERE P.productId=? ";
        ProductType p;
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setString(1,barCode);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() == false) {
                System.out.println("Product with id " + barCode + " is not present");
                return null;
            }
            Integer productId = rs.getInt("productId");
            String barcode = rs.getString("BarCode");
            String description = rs.getString("Description");
            Double sellPrice = rs.getDouble("SellPrice");
            Integer quantity = rs.getInt("Quantity");
            Double prodDiscountRate = rs.getDouble("prodDiscountRate");
            String notes = rs.getString("notes");
            Integer aisleId = rs.getInt("aisleID");
            String rackId = rs.getString("rackID");
            Integer levelId = rs.getInt("levelID");
            p = new ProductTypeImpl(productId,barcode,description,sellPrice,quantity,prodDiscountRate,notes,aisleId,rackId,levelId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("Data for product with barcode " + barCode + " has been retrieved with success");
        return p;
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageProductList()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        String sql = "SELECT * FROM PRODUCTTYPES AS P WHERE P.Description=?";
        List<ProductType> products = new ArrayList<>();
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setString(1,description);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Integer productId = rs.getInt("productId");
                String barcode = rs.getString("BarCode");
                Double sellPrice = rs.getDouble("SellPrice");
                Integer quantity = rs.getInt("Quantity");
                Double prodDiscountRate = rs.getDouble("prodDiscountRate");
                String notes = rs.getString("notes");
                Integer aisleId = rs.getInt("aisleID");
                String rackId = rs.getString("rackID");
                Integer levelId = rs.getInt("levelID");
                products.add(new ProductTypeImpl(productId,barcode,description,sellPrice,quantity,prodDiscountRate,notes,aisleId,rackId,levelId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return products;
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageInventory()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Check if product id is valid
        if (productId == null || productId<=0) {
            System.out.println("Invalid product id");
            throw new InvalidProductIdException();
        }

        //Check if product exists and retrieving it
        String sql = "SELECT * FROM PRODUCTTYPES WHERE productId=?";
        ProductType p = null;
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setInt(1,productId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() == false) {
                System.out.println("Product with id " + productId + " is not present");
                return false;
            }
            String barcode = rs.getString("BarCode");
            String description = rs.getString("Description");
            Double sellPrice = rs.getDouble("SellPrice");
            Integer quantity = rs.getInt("Quantity");
            Double prodDiscountRate = rs.getDouble("prodDiscountRate");
            String notes = rs.getString("notes");
            Integer aisleId = rs.getInt("aisleID");
            String rackId = rs.getString("rackID");
            Integer levelId = rs.getInt("levelID");
            p = new ProductTypeImpl(productId,barcode,description,sellPrice,quantity,prodDiscountRate,notes,aisleId,rackId,levelId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Checking if quantity update won't go negative
        if(p.getQuantity()+toBeAdded < 0) {
            System.out.println("It's impossible to update product " + productId + " to have a negative quantity");
            return false;
        }

        //Checking if location is set
        if(p.getLocation().equals("")) {
            System.out.println("Cannot set quantity if location is not set first");
            return false;
        }

        //Updating product
        String sql2 = "UPDATE PRODUCTTYPES SET Quantity=? WHERE productId=?";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql2);
            pstmt.setInt(1, toBeAdded);
            pstmt.setInt(2, productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Quantity " + toBeAdded + " has now been added to the ");
        return true;
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageInventory()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Check if product id is valid
        if (productId == null || productId<=0) {
            System.out.println("Invalid product id");
            throw new InvalidProductIdException();
        }

        //Check if position is valid
        if(newPos == null || !ProductTypeImpl.isValidLocation(newPos))
        {
            System.out.println("Invalid position");
            throw new InvalidLocationException();
        }

        Integer aisleId = ProductTypeImpl.extractAisleId(newPos);
        String rackId = ProductTypeImpl.extractRackId(newPos);
        Integer levelId = ProductTypeImpl.extractLevelId(newPos);

        //Checking if location is already occupied
        String sql = "SELECT * FROM PRODUCTTYPES AS P WHERE P.aisleID=? AND P.rackID=? AND P.levelID=?";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setInt(1, aisleId);
            pstmt.setString(2, rackId);
            pstmt.setInt(3, levelId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() != false) {
                System.out.println("Location " + newPos + " is already occupied");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        //Updating product
        String sql2 = "UPDATE PRODUCTTYPES SET aisleID=?, rackID=?, levelID=? WHERE productId=?";
        int numUpdated;
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql2);
            pstmt.setInt(1, aisleId);
            pstmt.setString(2, rackId);
            pstmt.setInt(3, levelId);
            pstmt.setInt(4, productId);
            numUpdated = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if(numUpdated == 0) {
            System.out.println("There's no product with id " + productId);
            return false;
        }
        System.out.println("Product with " + productId + " has been updated");
        return true;
    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        return null;
    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        return false;
    }

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
        return null;
    }

    /**
     * This method saves a new customer into the system. The customer's name should be unique.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerName the name of the customer to be registered
     *
     * @return the id (>0) of the new customer if successful, -1 otherwise
     *
     * @throws InvalidCustomerNameException if the customer name is empty or null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {
        /*
        // Exceptions
        if (customerName.isEmpty() || customerName == null){
            throw new InvalidCustomerNameException("The customer's name is empty or null");
        }
        if (this.loggedIn == null){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to create a new customer");
        }

        // The customer's name should be unique
        for (Customer c: customers) {
            if(c.getCustomerName() == customerName) {
                return -1;
            }
        }

        // Get an unique id
        Integer id = 1;
        for (Customer c: customers) {
          if (c.getId() >= id) {
            id = c.getId() + 1;
          }
        }

        // Create a new customer and add him to the customer list
        CustomerImpl c = new CustomerImpl (customerName, id);
        this.customers.add(c);

        return id;*/
        return -1;
    }

    /**
     * This method updates the data of a customer with given <id>. This method can be used to assign/delete a card to a
     * customer. If <newCustomerCard> has a numeric value than this value will be assigned as new card code, if it is an
     * empty string then any existing card code connected to the customer will be removed and, finally, if it assumes the
     * null value then the card code related to the customer should not be affected from the update. The card code should
     * be unique and should be a string of 10 digits.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param id the id of the customer to be updated
     * @param newCustomerName the new name to be assigned
     * @param newCustomerCard the new card code to be assigned. If it is empty it means that the card must be deleted,
     *                        if it is null then we don't want to update the cardNumber
     *
     * @return true if the update is successful
     *          false if the update fails ( cardCode assigned to another user, db unreacheable)
     *
     * @throws InvalidCustomerNameException if the customer name is empty or null
     * @throws InvalidCustomerCardException if the customer card is empty, null or if it is not in a valid format (string with 10 digits)
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {

        // Exceptions
        if (newCustomerName.isEmpty() || newCustomerName == null){
            throw new InvalidCustomerNameException("The customer's name is empty or null");
        }

        if (newCustomerCard.length() != 10){
            throw new InvalidCustomerCardException("The customer's card is not in a valid format");
        }

        if (this.loggedIn == null){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to modify a customer");
        }

        // Update customer name
        Customer c = this.getCustomer(id);
        c.setCustomerName(newCustomerName);

    
        // Detach if newCustomerCard is an empty string
        if (newCustomerCard.isEmpty()){
            c.setCustomerCard(newCustomerCard);
            c.setPoints(0);
            return true;
        }

        // Update the card number if newCustomerCard is not null
        if(newCustomerCard == null){
            return true;
        }

        c.setCustomerCard(newCustomerCard);
        return true;
    }


    /**
     * This method deletes a customer with given id from the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param id the id of the customer to be deleted
     * @return true if the customer was successfully deleted
     *          false if the user does not exists or if we have problems to reach the db
     *
     * @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        /*
        // Exceptions
        if (id <= 0  || id == null){
            throw new InvalidCustomerIdException("The customer id is null, less than or equal to 0");
        }

        if (this.loggedIn == null){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to delete a customer");
        }

        // Get the customer by id
        Customer c = this.getCustomer(id);

        // Check if customer is null
        if (c == null){
            return false;
        }

        // Remove c from customer list
        this.customers.remove(c);

        // Remove the object c referencing it to null, therefore its attributes are also eliminated: its id, 
        // its customerName and its customerCard
        c = null;
        */
        return true;
    }

    /**
     * This method returns a customer with given id.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param id the id of the customer
     *
     * @return the customer with given id
     *          null if that user does not exists
     *
     * @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
    */
    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        /*
        // Exceptions
        if (id <= 0  || id == null){
            throw new InvalidCustomerIdException("The customer id is null, less than or equal to 0");
        }

        if (this.loggedIn == null){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to get a customer");
        }

        // Search in the customer list and if the customer is found it is returned
        for (Customer c: customers){
            if (c.getId() == id){
                return c;
            }
        }

        // If the customer is not found, null is returned
        */
        return null;
    }


    /**
     * This method returns a list containing all registered users.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the list of all the customers registered
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        /*
        // Exception
        if (this.loggedIn == null){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to get all customers");
        }
        return this.customers;
        */
        return null;
    }


     /**
     * This method returns a string containing the code of a new assignable card.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the code of a new available card. An empty string if the db is unreachable
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public String createCard() throws UnauthorizedException {

        // Exception
        if (this.loggedIn == null){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to create a new card");
        }

        // An empty string is the db is unreachable ???

         return null;
    }


    /**
     * This method assigns a card with given card code to a customer with given identifier. A card with given card code
     * can be assigned to one customer only.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerCard the number of the card to be attached to a customer
     * @param customerId the id of the customer the card should be assigned to
     *
     * @return true if the operation was successful
     *          false if the card is already assigned to another user, if there is no customer with given id, if the db is unreachable
     *
     * @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
     * @throws InvalidCustomerCardException if the card is null, empty or in an invalid format
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        /*
        // Exceptions
        if (customerId <= 0  || customerId == null){
            throw new InvalidCustomerIdException("The customer id is null, less than or equal to 0");
        }

        if (customerCard.length() != 10 || customerCard.isEmpty() || customerCard == null) {
            throw new InvalidCustomerCardException("The customer's card is null, empty or it is not in a valid format");
        }

        if (this.loggedIn == null){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to attach a card to customer");
        }

        // Return false if the card is already assigned to another user
        for (Customer c: customers) {
            if (c.getCustomerCard() == customerCard) {
                return false;
            }
        }

        // Return false if there is no customer with given id
        Customer c = getCustomer(customerId);
        if (c == null) {
            return false;
        }

        // False if the db is unreachable ???

        // Attach card to customer c
        c.setCustomerCard(customerCard);
        */
        return true;
    }


    /**
     * This method updates the points on a card adding to the number of points available on the card the value assumed by
     * <pointsToBeAdded>. The points on a card should always be greater than or equal to 0.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerCard the card the points should be added to
     * @param pointsToBeAdded the points to be added or subtracted ( this could assume a negative value)
     *
     * @return true if the operation is successful
     *          false   if there is no card with given code,
     *                  if pointsToBeAdded is negative and there were not enough points on that card before this operation,
     *                  if we cannot reach the db.
     *
     * @throws InvalidCustomerCardException if the card is null, empty or in an invalid format
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
        /*
        // Exceptions
        if (customerCard.length() != 10 || customerCard.isEmpty() || customerCard == null) {
            throw new InvalidCustomerCardException("The customer's card is null, empty or it is not in a valid format");
        }

        if (this.loggedIn == null){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to modify points on a card");
        }

        Customer customer = null;

        for (Customer c: customers) {
            if (c.getCustomerCard() == customerCard) {
                customer = c;
            }
        }

        // Return false if there is no card with given code assigned to a customer
        if (customer == null){
            return false;
        }
        
        Integer points = customer.getPoints();
        Integer totalPoints = points + pointsToBeAdded;

        // The points on a card should always be greater than or equal to 0.
        if (totalPoints <= 0) {
            return false;
        }

        // False if the db is unreachable ???

        customer.setPoints(totalPoints);
        */
        return true;
    }


    /**
     * This method starts a new sale transaction and returns its unique identifier.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the id of the transaction (greater than or equal to 0)
     */
    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        /*
        if(!loggedIn.canManageSaleTransactions()) {
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
            //return -1;
        }


        Integer newtransactionId=getNewSaleTransactionId();
        currentsale = new SaleTransactionImpl(newtransactionId);
        //addSaleToSalesList(sale); NO MROE SICNE WE USE DB
        return newtransactionId;
        */
        return -1;
    }


    /**
     * This method adds a product to a sale transaction decreasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode the barcode of the product to be added
     * @param amount the quantity of product to be added
     * @return  true if the operation is successful
     *          false   if the product code does not exist,
     *                  if the quantity of product cannot satisfy the request,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException if the product code is empty, null or invalid
     * @throws InvalidQuantityException if the quantity is less than 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation*/
    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        /*
        if(!loggedIn.canManageSaleTransactions()) {
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
            //return false;
        }


        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();

        }


        ProductTypeImpl product= getProductTypeByCode(productCode);
        if (product==null){
            throw new InvalidProductCodeException();
        }
        if (product==null) return false;

        if ((product.getQuantity()< amount)||(amount <0)){
            throw new InvalidQuantityException();
        }

        //AMOUNT AND PROD LIST UPDATED ONLY AT THE END IN DB
        if(!loggedIn.canManageSaleTransactions())return false;
        if (sale==null) return false;
        if (product==null) return false;
        if ((product.getQuantity()< amount)||(amount <0)) return false;

        if(sale.EditProductInSale(product, amount)) {
            return true;
        } else{
            return false;
        }
         */
        return false;
    }

    /**
     * This method deletes a product from a sale transaction increasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode the barcode of the product to be deleted
     * @param amount the quantity of product to be deleted
     *
     * @return  true if the operation is successful
     *          false   if the product code does not exist,
     *                  if the quantity of product cannot satisfy the request,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException if the product code is empty, null or invalid
     * @throws InvalidQuantityException if the quantity is less than 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        /*
        if(!loggedIn.canManageSaleTransactions()) {
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
            //return false;
        }

        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();
        }

        ProductTypeImpl product= getProductTypeByCode(productCode);
        if (product==null){
            throw new InvalidProductCodeException();
        }

        if ((product.getQuantity()< amount)||(amount <0)){
            throw new InvalidQuantityException();
        }

        //AMOUNT IN DB UPDATED ONLY AT THE END

        if(!loggedIn.canManageSaleTransactions())return false;
        if (sale==null) return false;
        if (product==null) return false;
        if ((product.getQuantity()< amount)||(amount <0)) return false;

        if(sale.EditProductInSale(product, -amount)) {

            return true;
        } else{
            return false;
        }
        */
         return false;
    }


    /**
     * This method applies a discount rate to all units of a product type with given type in a sale transaction. The
     * discount rate should be greater than or equal to 0 and less than 1.
     * The sale transaction should be started and open.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode the barcode of the product to be discounted
     * @param discountRate the discount rate of the product
     *
     * @return  true if the operation is successful
     *          false   if the product code does not exist,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException if the product code is empty, null or invalid
     * @throws InvalidDiscountRateException if the discount rate is less than 0 or if it greater than or equal to 1.00
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        if(!loggedIn.canManageSaleTransactions()) {
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
            //return false;
        }

        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();
        }

        ProductTypeImpl product= getProductTypeByCode(productCode);
        if (product==null){
            throw new InvalidProductCodeException();
        }


        if ((discountRate <0)||(discountRate >= 1)){
            throw new InvalidDiscountRateException();
        }
        if(!loggedIn.canManageSaleTransactions())return false;
        if (sale==null) return false;
        if (product==null) return false;
        if ((discountRate <0)||(discountRate >= 1)) return false;

        return sale.ApplyDiscountToSaleProduct(discountRate, getProductTypeByCode(productCode));

    }

    /**
     * This method applies a discount rate to the whole sale transaction.
     * The discount rate should be greater than or equal to 0 and less than 1.
     * The sale transaction can be either started or closed but not already payed.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param discountRate the discount rate of the sale
     *
     * @return  true if the operation is successful
     *          false if the transaction does not exists
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidDiscountRateException if the discount rate is less than 0 or if it greater than or equal to 1.00
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        if(!loggedIn.canManageSaleTransactions()) {
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
            //return false;
        }

        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();
        }

        if ((discountRate <0)||(discountRate >= 1)){
            throw new InvalidDiscountRateException();
        }

        if(!loggedIn.canManageSaleTransactions())return false;
        if (sale==null) return false;
        if ((discountRate <0)||(discountRate >= 1)) return false;

        return sale.ApplyDiscountToSaleAll(discountRate);

    }

    /**
     * This method returns the number of points granted by a specific sale transaction.
     * Every 10€ the number of points is increased by 1 (i.e. 19.99€ returns 1 point, 20.00€ returns 2 points).
     * If the transaction with given id does not exist then the number of points returned should be -1.
     * The transaction may be in any state (open, closed, payed).
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     *
     * @return the points of the sale (1 point for each 10€) or -1 if the transaction does not exists
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();

        }

        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();
        }

        if (sale==null) return -1;


        return sale.PointsForSale();
    }

    /**
     * This method closes an opened transaction. After this operation the
     * transaction is persisted in the system's memory.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     *
     * @return  true    if the transaction was successfully closed
     *          false   if the transaction does not exist,
     *                  if it has already been closed,
     *                  if there was a problem in registering the data
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
           // return false;
        }


        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();
        }

        if(!loggedIn.canManageSaleTransactions())return false;
        if (sale==null) return false;


        return sale.EndSaleUpdateProductQuantity();
    }

    /**
     * This method deletes a sale transaction with given unique identifier from the system's data store.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     *  transactionId the number of the transaction to be deleted
     *
     *  true if the transaction has been successfully deleted,
     *          false   if the transaction doesn't exist,
     *                  if it has been payed,
     *                  if there are some problems with the db
     *
     * @throws InvalidTransactionIdException if the transaction id number is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */

    @Override
    public boolean deleteSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
            //return false;
        }

        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();
        }

        if(!loggedIn.canManageSaleTransactions())return false;
        if (sale==null) return false;

        //DB??? io metto dopo

        sale.AbortSaleUpdateProductQuantity();
        //RemoveSaleFromSalesList(sale);
        return true;
    }


    /**
     * This method returns  a closed sale transaction.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the CLOSED Sale transaction
     *
     * @return the transaction if it is available (transaction closed), null otherwise
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();

        }

        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();
        }

        if(!loggedIn.canManageSaleTransactions())return null;
        if (sale==null) return null;


        return sale;

    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }



    // -------------------- FR7 ------------------- //
    // ------------------- ADMIN ------------------ //
    // --------------- SHOP MANAGER --------------- //
    // ------------------ CASHIER ----------------- //

    /**
     * This method record the payment of a sale transaction with cash and returns the change (if present).
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the number of the transaction that the customer wants to pay
     * @param cash the cash received by the cashier
     *
     * @return the change (cash - sale price)
     *         -1   if the sale does not exists,
     *              if the cash is not enough,
     *              if there is some problemi with the db
     *
     * @throws InvalidTransactionIdException if the  number is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     * @throws InvalidPaymentException if the cash is less than or equal to 0*/
    @Override
    public double receiveCashPayment(Integer transactionId, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
        }

        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();
        }

        if(cash<=0){
            throw new InvalidPaymentException();
        }

        if (sale==null) return -1;
        if(cash<=0) return -1;

        Double change=sale.PaySaleAndReturnChange(cash, true);
        if(change!=-1) {
            SaleConfirmedEnsurePersistence(sale);
            newBalanceUpdate(cash);

            return change;
        } else{
            //BE CAREFUL CHECK I ASSUME THAT THEN LATER DELETESALETRANSACTION WILL BE CALLED
            return -1;
        }
    }


    /**
     * This method record the payment of a sale with credit card. If the card has not enough money the payment should
     * be refused.
     * The credit card number validity should be checked. It should follow the luhn algorithm.
     * The credit card should be registered in the system.
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the number of the sale that the customer wants to pay
     * @param creditCard the credit card number of the customer
     *
     * @return  true if the operation is successful
     *          false   if the sale does not exists,
     *                  if the card has not enough money,
     *                  if the card is not registered,
     *                  if there is some problem with the db connection
     *
     * @throws InvalidTransactionIdException if the sale number is less than or equal to 0 or if it is null
     * @throws InvalidCreditCardException if the credit card number is empty, null or if luhn algorithm does not
     *                                      validate the credit card
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean receiveCreditCardPayment(Integer transactionId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException{
        if(!loggedIn.canManageSaleTransactions()) {
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
        }

        if(!isValidCreditCard(creditCard)) {
            throw new InvalidCreditCardException();
        }

        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();
        }

        if (sale==null) return false;
        if(!isValidCreditCard(creditCard)) return false;

        //CHECK ENOUGH MONEY ON CARD MISSING
        File file = new File("./CARDS.txt");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String st;
        Double amount=-1.0;
        while (true) {
            try {
                if (!((st = br.readLine()) != null)){
                    String[] s= st.split(",");
                    if(s[0].equals(creditCard)){
                      amount=Double.parseDouble(s[1]);
                    }
                    break;}
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(amount==-1){
                return false;
            } else{
                if(sale.PaySaleAndReturnChange(amount, false)>=0){
                    SaleConfirmedEnsurePersistence(sale);
                    newBalanceUpdate(sale.getCurrentAmount());
                    return true;
                } else{
                    return false;
                    //BE CAREFUL CHECK I ASSUME THAT THEN LATER DELETESALETRANSACTION WILL BE CALLED
                }
            }

        }

        return false;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        return false;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        return null;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        return 0;
    }




    //ADDED METHODS FOR FR 6 AND 7 PART


    //GET OBJECT FROM ID AND DB
    private CustomerImpl getCustomerById(int transactionCardId) {
        //return (CustomerImpl) customers.stream().filter(c->c.getId()==transactionCardId);

        String getnewid = "SELECT * FROM CUSTOMERS WHERE CustomerId=?";
        CustomerImpl c=null;
        try (
                PreparedStatement pstmt  = conn.prepareStatement(getnewid)){

            pstmt.setInt(1, transactionCardId);
            ResultSet rs    = pstmt.executeQuery(getnewid);

            c =new  CustomerImpl(rs.getInt("CustomerId"),rs.getString("CustomerName"), rs.getString("CustomerCard"), rs.getInt("Points")  );

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return c;
    }

    private BalanceOperationImpl getBalanceById(int balanceOperation) {
        // return (BalanceOperationImpl) balanceOperationsList.stream().filter(c->c.getBalanceId()==balanceOperation);
        String getnewid = "SELECT * FROM BALANCEOPERATIONS WHERE BalanceId=?";
        BalanceOperationImpl bal=null;
        try (
                PreparedStatement pstmt  = conn.prepareStatement(getnewid)){

            pstmt.setInt(1,balanceOperation);
            ResultSet rs    = pstmt.executeQuery(getnewid);

            bal =new  BalanceOperationImpl(rs.getInt("BalanceId"),LocalDate.parse(rs.getString("Date")), rs.getDouble("amount"), rs.getString("Type")  );

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return bal;

    }

    SaleTransactionImpl getSaleTransactionById(Integer transactionId) {

        if (currentsale.getTicketNumber()==transactionId){
            return currentsale;
        }
/* NO MORE LISTSS USING DB NOW
        for (SaleTransactionImpl e : salesList ){
            if(e.getTicketNumber()==transactionId){
                return e;
            }
        }
        return null;
        // throw ...
        */
        String getnewid = "SELECT * FROM SALETRANSACTIONS WHERE transactionId=?";
        SaleTransactionImpl sale=null;
        try (
                PreparedStatement pstmt  = conn.prepareStatement(getnewid)){

            pstmt.setInt(1,transactionId);
            ResultSet rs    = pstmt.executeQuery(getnewid);

            sale =new SaleTransactionImpl(transactionId, rs.getString("State"), rs.getString("PaymentType"), rs.getDouble("Amount"), rs.getDouble("discountRate"), getCustomerById(rs.getInt("transactionCardId")), getBalanceById(rs.getInt("BalanceOperation")), getProdListForSaleDB(transactionId));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return sale;

    }

    ProductTypeImpl getProductTypeByCode(String productCode){
       /* for (ProductTypeImpl p : productsList) {
            if (productCode.equals(p.getBarCode())){
                return p;
            }
        }
        return null;
        // throw ...*/

        String getnewid_ = "SELECT * FROM PRODUCTTYPES WHERE BarCode=?";
        ProductTypeImpl prod=null;
        try (
                PreparedStatement pstmt  = conn.prepareStatement(getnewid_)){

            pstmt.setString(1,productCode);
            ResultSet rs    = pstmt.executeQuery(getnewid_);
            prod =new ProductTypeImpl(rs.getInt("productId"), rs.getString("BarCode"),rs.getString("Description"),rs.getDouble("SellPrice"),rs.getInt("Quantity"),rs.getDouble("prodDiscoutRate"),rs.getString("notes"),rs.getInt("aisleID"),rs.getString("rackID"),rs.getInt("levelID"));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return prod;
    }

    //GET NEW ID

    public Integer getNewSaleTransactionId(){
        Integer tid=-1;
        String getnewid = "SELECT COALESCE(MAX(transactionId),'0') FROM SALETRANSACTIONS";

        try (
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(getnewid)){

            tid=rs.getInt("transactionId");


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tid;
    }

    private int getNewBalanceOperationId() {
        Integer tid=-1;
        String getnewid = "SELECT COALESCE(MAX(BalanceId),'0') FROM SALETRANSACTIONS";

        try (
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(getnewid)){

            tid=rs.getInt("BalanceIdId");


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tid;
    }

    //PERSISTENCE WHEN SALE FINISHES

    private void SaleConfirmedEnsurePersistence(SaleTransactionImpl sale ){

        //UPDATE SALETRANSACTIONTABLE
        String sql = "INSERT INTO SALETRANSACTIONS(transactionId,State,PaymentType, Amount, discountRate, transactionCardId, BalanceOperationId) VALUES(?,?,?,?,?,?,?)";

        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setInt(1, sale.getTicketNumber());
            pstmt.setString(2, sale.getStateString());
            pstmt.setString(3, sale.getPayString());
            pstmt.setDouble(4, sale.getPrice());
            pstmt.setDouble(5, sale.getDiscountRate());
            pstmt.setInt(6, sale.getTransactionCard().getId());
            pstmt.setInt(7, sale.getSaleOperationRecord().getBalanceId());
            pstmt.executeUpdate();
        } catch (SQLException var6) {
            System.out.println(var6.getMessage());
        }

        Integer tid=sale.getTicketNumber();

     //UPDATE SALESANDPRODUCTS TABLE
      sale.getListOfProductsSale().entrySet().stream().forEach(el-> {

            String salesandproductssql = "INSERT INTO SALESANDPRODUCTS(transactionId,BarCode,Quantity) VALUES(?,?,?)";

            try (
                    PreparedStatement pstmt = conn.prepareStatement(salesandproductssql)) {

                // set the value of the parameter
                pstmt.setInt(1, sale.getTicketNumber());
                pstmt.setString(2, el.getKey().getBarCode());
                pstmt.setInt(3, el.getValue());
                //
                ResultSet rs = pstmt.executeQuery();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            //UPDATE INVENTORY
          String salesprodsql = "UPDATE PRODUCTTYPES SET Quantity=Quantity-? WHERE BarCode=?";

          try (
                  PreparedStatement pstmt = conn.prepareStatement(salesandproductssql)) {

              // set the value of the parameter
              pstmt.setInt(1, el.getValue());
              pstmt.setString(2, el.getKey().getBarCode());

              //
              ResultSet rs = pstmt.executeQuery();

          } catch (SQLException e) {
              System.out.println(e.getMessage());
          }


      });
            //UPDATE CUSTOMER POINTS

          String salecustomersql = "UPDATE CUSTOMERS SET Points=? WHERE customerId=?";

          try (
                  PreparedStatement pstmt = conn.prepareStatement(salecustomersql)) {

              // set the value of the parameter
              pstmt.setInt(1, sale.getTransactionCard().getPoints());
              pstmt.setInt(2, sale.getTransactionCard().getId());
              ResultSet rs = pstmt.executeQuery();

          } catch (SQLException e) {
              System.out.println(e.getMessage());
          }

    }

    public void newBalanceUpdate(Double amount){
        int  id=getNewBalanceOperationId();

        LocalDate now = LocalDate.now();
        if(amount>0){
            BalanceOperationImpl bal=new BalanceOperationImpl(id,now,amount,"CREDIT" );
            addBalanceToBalancesList(bal);
        } else{
            BalanceOperationImpl bala=new BalanceOperationImpl(id,now,amount,"DEBIT" );
            addBalanceToBalancesList(bala);
        }

    }




    public void addBalanceToBalancesList(BalanceOperationImpl b){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        //balanceOperationsList.add(b); NO MORE SINCE NO MORE LISTS
        String sql = "INSERT INTO BALANCEOPERATIONS(balanceID,Date, Amount, Type) VALUES(?,?,?,?)";

        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setInt(1, b.getBalanceId());
            pstmt.setString(2, dtf.format(b.getDate()));
            pstmt.setDouble(3, b.getAmount());
            pstmt.setString(4, b.getType());

            pstmt.executeUpdate();
        } catch (SQLException var6) {
            System.out.println(var6.getMessage());
        }
    }


    //GET LIST OF PRODUCTS RELATED TO A SALE CAN BE USEFUL FOR RERURN TRANSACTION

    private HashMap<ProductTypeImpl, Integer> getProdListForSaleDB(int tid) {

        String salesandproductssql = "SELECT * FROM SALESANDPRODUCTS WHERE transactionId=?";
        HashMap< ProductTypeImpl, Integer> map= new HashMap<>();
        try (
                PreparedStatement pstmt  = conn.prepareStatement(salesandproductssql)){

            // set the value of the parameter
            pstmt.setDouble(1,tid);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                String prod =rs.getString("BarCode");
                map.put(getProductTypeByCode(prod), rs.getInt("Quantity"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return map;
    }



   /* NO MORE NEEDED SINCE WE DON'T LOAD LIST AT THE START
    public void addSaleToSalesList(SaleTransactionImpl sale){
        salesList.add(sale);
    }*/

    /*NO MORE NEEDED SINCE WE DON'T LOAD LIST AT THE START
    public void RemoveSaleFromSalesList(SaleTransactionImpl sale) {
        salesList.remove(sale);
    }*/



    public boolean isValidCreditCard(String cardNumber)
    {
        // int array for processing the cardNumber
        int[] cardIntArray=new int[cardNumber.length()];

        for(int i=0;i<cardNumber.length();i++)
        {
            char c= cardNumber.charAt(i);
            cardIntArray[i]=  Integer.parseInt(""+c);
        }

        for(int i=cardIntArray.length-2;i>=0;i=i-2)
        {
            int num = cardIntArray[i];
            num = num * 2;  // step 1
            if(num>9)
            {
                num = num%10 + num/10;  // step 2
            }
            cardIntArray[i]=num;
        }

        int sum = sumDigits(cardIntArray);  // step 3



        if(sum%10==0)  // step 4
        {
            return true;
        }

        return false;

    }

    public static int sumDigits(int[] arr)
    {
        return Arrays.stream(arr).sum();
    }



}
