package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;

//import javax.persistence.criteria.CriteriaBuilder;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;


public class EZShop implements EZShopInterface {

    /* NO MORE NEEDED SINCE WE USE DB
    private List<Customer> customers;
    private List<OrderImpl> orders;
    private List<SaleTransactionImpl> salesList;
    private List<ReturnTransactionImpl> returnsList;
    private List<BalanceOperationImpl> balanceOperationsList;
    private List<UserImpl> usersList;
    private List<ProductTypeImpl> productsList;
    private List<LoyaltyCardImpl> cardsList;*/



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

        try {

            Statement st = conn.createStatement();
            String deleteAllCustomers = "DELETE FROM CUSTOMERS WHERE CustomerId > 0";
            st.executeUpdate(deleteAllCustomers);

        }
        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
        }

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

        return getProductTypeByCode(barCode);

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

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {

        // Exceptions
        if (customerName == null || customerName.isEmpty()){
            throw new InvalidCustomerNameException("The customer's name is empty or null");
        }

        if (this.loggedIn == null || !this.loggedIn.canManageCustomers()){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to create a new customer");
        }

        try {

            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM CUSTOMERS");

            // The customer's name should be unique
            while (res.next()){
                if (res.getString("CustomerName").equals(customerName)) {
                    return -1;
                }
            }

            // Get an unique id

            // Initial value
            Integer id = 1;

            // Boolean to know if the value of the id was found in the db
            Boolean modified = true;

            // If the id value has not been modified, it means that it is unique and the while loop ends
            while (modified) {
                modified = false;

                Statement st2 = conn.createStatement();
                ResultSet res2 = st2.executeQuery("SELECT * FROM CUSTOMERS");

                while (res2.next()){
                    if (res2.getInt("CustomerId") == id) {
                        // This id value has been found, it means that it is not unique
                        // Try with the following value
                        id = res2.getInt("CustomerId") + 1;
                        modified = true;
                        break;
                    }
                }
            }

            // Create a new customer into the customer table
            Statement st3 = conn.createStatement();
            String insertCustomer = "INSERT INTO CUSTOMERS (CustomerId, CustomerName, CustomerCard, Points) VALUES ("+id+",'"+customerName+"','', 0)";
            st3.executeUpdate(insertCustomer);

            return id;

        }

        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
            return -1;
        }

    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, UnauthorizedException{

        // Exceptions
        if (newCustomerName == null || newCustomerName.isEmpty()){
            throw new InvalidCustomerNameException("The customer's name is empty or null");
        }

        if (newCustomerCard != null && !newCustomerCard.isEmpty()) {

            if (newCustomerCard.length() != 10 ){
                throw new InvalidCustomerCardException("The customer's card is not in a valid format");
            }

            // Check if newCustomerCard contains only digits
            try {
                Integer.parseInt(newCustomerCard);
            }
            catch(Exception e) {
                throw new InvalidCustomerCardException("The customer's card is not in a valid format");
            }
        }

        if (this.loggedIn == null || !this.loggedIn.canManageCustomers()){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to modify a customer");
        }


        try {

            // Update customer name if newCustomerName is unique and the id is found
            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM CUSTOMERS");

            Boolean idFound = false;

            while (res.next()){
                if (res.getString("CustomerName").equals(newCustomerName) && res.getInt("CustomerId") != id) {
                    return false;
                }

                if (res.getInt("CustomerId") == id) {
                    idFound = true;
                }
            }

            if (!idFound) { return false;}

            Statement st1 = conn.createStatement();
            String updateCustomerName = "UPDATE CUSTOMERS SET CustomerName = '"+newCustomerName+"' WHERE CustomerId="+id+" ";
            st1.executeUpdate(updateCustomerName);

            // Update the card number if newCustomerCard is not null
            if(newCustomerCard != null){


                // Detach if newCustomerCard is an empty string
                if (newCustomerCard.isEmpty()){

                    Statement st3 = conn.createStatement();
                    String detachCustomerCard = "UPDATE CUSTOMERS SET CustomerCard = '' WHERE CustomerId="+id+" ";
                    st3.executeUpdate(detachCustomerCard);

                    Statement st4 = conn.createStatement();
                    String removePoints = "UPDATE CUSTOMERS SET Points = 0 WHERE CustomerId="+id+" ";
                    st4.executeUpdate(removePoints);

                    return true;
                }

                // Check if newCustomerCard is already attached
                else {

                    Statement st5 = conn.createStatement();
                    ResultSet res5 = st5.executeQuery("SELECT * FROM CUSTOMERS");

                    while (res5.next()){
                        if (res5.getString("CustomerCard").equals(newCustomerCard)) {
                            return false;
                        }
                    }

                }

                Statement st6 = conn.createStatement();
                String modifyCustomerCard = "UPDATE CUSTOMERS SET CustomerCard = '"+newCustomerCard+"' WHERE CustomerId="+id+" ";
                st6.executeUpdate(modifyCustomerCard);

            }

            return true;

        }

        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {

        // Exceptions
        if (id == null  || id <= 0){
            throw new InvalidCustomerIdException("The customer id is null, less than or equal to 0");
        }

        if (this.loggedIn == null || !this.loggedIn.canManageCustomers()){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to delete a customer");
        }

        try {

            // Delete customer if his id is found
            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM CUSTOMERS");

            Boolean idFound = false;

            while (res.next()){
                if (res.getInt("CustomerId") == id){
                    idFound = true;
                }
            }

            if (!idFound) { return false;}

            Statement st1 = conn.createStatement();
            String deleteCustomer = "DELETE FROM CUSTOMERS WHERE CustomerId="+id+"";
            st1.executeUpdate(deleteCustomer);
            return true;

        }
        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
            return false;
        }

    }


    @Override
    public CustomerImpl getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {

        // Exceptions
        if (id == null  || id <= 0){
            throw new InvalidCustomerIdException("The customer id is null, less than or equal to 0");
        }


        if (this.loggedIn == null || !this.loggedIn.canManageCustomers()){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to get a customer");
        }


        try {

            CustomerImpl c;

            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM CUSTOMERS");

            while (res.next()){
                if (res.getInt("CustomerId") == id) {
                    c = new CustomerImpl( res.getString("CustomerName"), res.getString("CustomerCard"), res.getInt("CustomerId"), res.getInt("Points") );
                    return c;
                }
            }

            // If the customer is not found, null is returned
            return null;
        }

        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {

        // Exception
        if (this.loggedIn == null || !this.loggedIn.canManageCustomers()){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to get all customers");
        }

        List<Customer> customerList = new ArrayList<Customer>();

        try {

            Customer c;

            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM CUSTOMERS");

            while (res.next()){
                c = new CustomerImpl( res.getString("CustomerName"), res.getString("CustomerCard"), res.getInt("CustomerId"), res.getInt("Points") );
                customerList.add(c);
            }

            return customerList;
        }

        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
            return customerList;
        }


    }

    @Override
    public String createCard() throws UnauthorizedException {

        // Exception
        if (this.loggedIn == null || !this.loggedIn.canManageCustomers()){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to create a new card");
        }

        try {

            // Get an unique customerCard

            // Initial value
            Integer customerCardInt = 0;

            // Boolean to know if the value of the card was found in the db
            Boolean modified = true;

            // If the card value has not been modified, it means that it is unique and the while loop ends

            while (modified) {
                modified = false;

                Statement st = conn.createStatement();
                ResultSet res = st.executeQuery("SELECT * FROM CUSTOMERS");

                while (res.next()){
                    if ( !res.getString("CustomerCard").isEmpty() ) {
                        if ( Integer.parseInt(res.getString("CustomerCard")) == customerCardInt ) {
                            // This card value has been found, it means that it is not unique
                            // Try with the following value
                            customerCardInt = customerCardInt +1 ;
                            modified = true;
                            break;
                        }
                    }
                }
            }

            String customerCardString = Integer.toString(customerCardInt);

            while ( customerCardString.length() < 10 ) {
                customerCardString = 0 + customerCardString;
            }


            return customerCardString;
        }

        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {

        // Exceptions
        if (customerId == null  || customerId <= 0){
            throw new InvalidCustomerIdException("The customer id is null, less than or equal to 0");
        }

        if (customerCard == null || customerCard.length() != 10 || customerCard.isEmpty()) {
            throw new InvalidCustomerCardException("The customer's card is null, empty or it is not in a valid format");
        }

        if (this.loggedIn == null || !this.loggedIn.canManageCustomers()){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to attach a card to customer");
        }


        try {

            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM CUSTOMERS");

            // Return false if the card is already assigned to another user
            while (res.next()){
                if (res.getString("CustomerCard").equals(customerCard)) {
                    return false;
                }
            }

            // Return false if there is no customer with given id
            Customer c = getCustomer(customerId);
            if (c == null) {
                return false;
            }

            // Attach card to customer c
            Statement st2 = conn.createStatement();
            String updateCustomer = "UPDATE CUSTOMERS SET CustomerCard = '"+customerCard+"' WHERE CustomerId="+customerId+" ";
            st2.executeUpdate(updateCustomer);

            return true;


        }

        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
            return false;
        }



    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException{

        // Exceptions
        if (customerCard == null || customerCard.length() != 10 || customerCard.isEmpty()) {
            throw new InvalidCustomerCardException("The customer's card is null, empty or it is not in a valid format");
        }

        if (this.loggedIn == null || !this.loggedIn.canManageCustomers()){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to modify points on a card");
        }


        try {

            Customer c = null;
            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM CUSTOMERS");


            while (res.next()){
                if (res.getString("CustomerCard").equals(customerCard)) {
                    c = getCustomer(res.getInt("CustomerId"));
                }
            }

            // Return false if there is no card with given code assigned to a customer
            if (c == null){
                return false;
            }

            Integer points = c.getPoints();
            Integer totalPoints = points + pointsToBeAdded;

            // The points on a card should always be greater than or equal to 0.
            if (totalPoints < 0) {
                return false;
            }

            Statement st2 = conn.createStatement();
            String updatePoints = "UPDATE CUSTOMERS SET Points = '"+totalPoints+"' WHERE CustomerCard='"+customerCard+"'";
            st2.executeUpdate(updatePoints);

            return true;

        }

        catch (Exception e) {
            System.out.println("Error with db connection");
            e.printStackTrace();
            return false;
        }

    }


    /**
     * This method starts a new sale transaction and returns its unique identifier.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the id of the transaction (greater than or equal to 0)
     */
    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {

        if( loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
            //return -1;
        }


        Integer newtransactionId=getNewSaleTransactionId();
        currentsale = new SaleTransactionImpl(newtransactionId);
        //addSaleToSalesList(sale); NO MROE SICNE WE USE DB
        System.out.println(currentsale.getTicketNumber());
        return newtransactionId;
       // return -1;
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

        if(loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
            //return false;
        }


        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();

        }


        ProductTypeImpl product= getProductTypeByCode(productCode);
       // System.out.println("ok2");

        if (product==null){
            throw new InvalidProductCodeException();
        }
        if (product==null) return false;

        System.out.println(product.getQuantity());
        if ((product.getQuantity() < amount)||(amount <0)){
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

        //return false;
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

        if(loggedIn == null || !loggedIn.canManageSaleTransactions()) {
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

        if ((currentsale.listOfProductsSale.get(product)<amount )|| (amount <0)){
            throw new InvalidQuantityException();
        }

        //AMOUNT IN DB UPDATED ONLY AT THE END

        if(!loggedIn.canManageSaleTransactions())return false;
        if (sale==null) return false;
        if (product==null) return false;
        if ((product.getQuantity() < amount)||(amount <0)) return false;

        if(sale.EditProductInSale(product, -amount)) {

            return true;
        } else{
            return false;
        }

         //return false;
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
        if(loggedIn == null || !loggedIn.canManageSaleTransactions()) {
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

        //System.out.println(discountRate);

        if ((discountRate <=0)||(discountRate >= 1)){
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
        if(loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
            //return false;
        }

        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();
        }

        if ((discountRate <=0)||(discountRate >= 1)){
            throw new InvalidDiscountRateException();
        }

        if(!loggedIn.canManageSaleTransactions())return false;
        if (sale==null) return false;
        if ((discountRate <=0)||(discountRate >= 1)) return false;

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
        if(loggedIn == null || !loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
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
        if(loggedIn == null || !loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            throw new UnauthorizedException();
           // return false;
        }


        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        if (sale==null){
            throw new InvalidTransactionIdException();
        }

        if(loggedIn == null || !loggedIn.canManageSaleTransactions())return false;
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
        if(loggedIn == null || !loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
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
        currentsale=null;
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
        if(loggedIn == null || !loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
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
    public Integer startReturnTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        System.out.println("Call startReturnTransaction(transactionId = "+ transactionId +")");

        // Check if the transactionId is null or it is less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println("startReturnTransaction: The transactionId is null or it is less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println("startReturnTransaction: There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get SaleTransaction
        SaleTransactionImpl saleTransaction = getSaleTransactionById(transactionId);
        // Check if the SaleTransaction is not available
        if (saleTransaction == null) {
            System.err.println("startReturnTransaction: The SaleTransaction is not available");
            return -1;
        }
        // Check if the SaleTransaction has not already been sold or payed
        if (!saleTransaction.isPayed() && !saleTransaction.isClosed()) {
            System.err.println("startReturnTransaction: The SaleTransaction has not already been sold or payed");
            return -1;
        }

        // Insert a new ReturnTransaction
        String query = "INSERT INTO RETURN_TRANSACTIONS(TransactionId, State) VALUES(?, ?)";
        Integer newId = -1;
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, transactionId);
            pstmt.setString(2, "INPROGRESS");
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                newId = rs.getInt(1);
            }
            System.out.println("startReturnTransaction: Created a new ReturnTransaction with id = "+ newId);
        } catch (SQLException e) {
            System.err.println("startReturnTransaction: " + e.getMessage());
            return -1;
        }

        return newId;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        System.out.println("Call returnProduct(returnId = "+ returnId +", productCode = "+ productCode +", amount = "+ amount +")");

        // Check if the returnId is null or less than or equal to 0
        if (returnId == null || returnId <= 0) {
            System.err.println("returnProduct: The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if the productCode is null, empty or invalid
        if (productCode == null || productCode.isEmpty() || ProductTypeImpl.isValidCode(productCode)) {
            System.err.println("returnProduct: The productCode is null, empty or invalid");
            throw new InvalidProductCodeException();
        }

        // Check if the quantity is less than or equal to 0
        if (amount <= 0) {
            System.err.println("returnProduct: The quantity is less than or equal to 0");
            throw new InvalidQuantityException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println("returnProduct: There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get ReturnTransaction
        ReturnTransactionImpl returnTransaction = getReturnTransactionById(returnId);
        // Check if the ReturnTransaction does not exist
        if (returnTransaction == null) {
            System.err.println("returnProduct: The ReturnTransaction does not exist");
            return false;
        }

        // Get SaleTransaction
        SaleTransactionImpl saleTransaction = returnTransaction.getSaleTransaction();
        // Check if the SaleTransaction is not available
        if (saleTransaction == null) {
            System.err.println("returnProduct: The SaleTransaction is not available");
            return false;
        }

        // Get ProductType
        ProductTypeImpl productType = getProductTypeByCode(productCode);
        // Check if the product to be returned does not exists
        if (productType == null) {
            System.err.println("returnProduct: The product to be returned does not exists");
            return false;
        }
        // Check if the product was not in the transaction
        if (!saleTransaction.isProductInSale(productType)) {
            System.err.println("returnProduct: The product was not in the transaction");
            return false;
        }
        // Check if the amount is higher than the one in the sale transaction
        if (amount > saleTransaction.getProductQuantity(productType)) {
            System.err.println("returnProduct: The amount is higher than the one in the sale transaction");
            return false;
        }

        // Insert the product to the ReturnTransaction
        String query = "INSERT INTO RETURN_PRODUCTS(ReturnId, BarCode, Quantity) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, returnId);
            pstmt.setString(2, productType.getBarCode());
            pstmt.setInt(3, amount);
            pstmt.executeUpdate();
            System.out.println("returnProduct: Inserted the product (barcode = "+ productType.getBarCode() +", quantity = "+ amount +") in the ReturnTransaction (id = "+ returnId +")");
        } catch (SQLException e) {
            System.err.println("returnProduct: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * This method closes a return transaction. A closed return transaction can be committed (i.e. <commit> = true) thus
     * it increases the product quantity available on the shelves or not (i.e. <commit> = false) thus the whole trasaction
     * is undone.
     * This method updates the transaction status (decreasing the number of units sold by the number of returned one and
     * decreasing the final price).
     * If committed, the return transaction must be persisted in the system's memory.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param returnId the id of the transaction
     * @param commit whether we want to commit (True) or rollback(false) the transaction
     *
     * @return  true if the operation is successful
     *          false   if the returnId does not correspond to an active return transaction,
     *                  if there is some problem with the db
     *
     * @throws InvalidTransactionIdException if returnId is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        System.out.println("Call endReturnTransaction(returnId = "+ returnId +", commit = "+ commit +")");

        // Check if the returnId is null or less than or equal to 0
        if (returnId == null || returnId <= 0) {
            System.err.println("endReturnTransaction: The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println("endReturnTransaction: There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get ReturnTransaction
        ReturnTransactionImpl returnTransaction = getReturnTransactionById(returnId);
        // Check if the ReturnTransaction does not exist
        if (returnTransaction == null) {
            System.err.println("endReturnTransaction: The ReturnTransaction does not exist");
            return false;
        }

        // TODO

        return true;
    }

    /**
     * This method deletes a closed return transaction. It affects the quantity of product sold in the connected sale transaction
     * (and consequently its price) and the quantity of product available on the shelves.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param returnId the identifier of the return transaction to be deleted
     *
     * @return  true if the transaction has been successfully deleted,
     *          false   if it doesn't exist,
     *                  if it has been payed,
     *                  if there are some problems with the db
     *
     * @throws InvalidTransactionIdException if the transaction id is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        System.out.println("Call deleteReturnTransaction(returnId = "+ returnId +")");

        // Check if the returnId is null or less than or equal to 0
        if (returnId == null || returnId <= 0) {
            System.err.println("deleteReturnTransaction: The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println("deleteReturnTransaction: There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get ReturnTransaction
        ReturnTransactionImpl returnTransaction = getReturnTransactionById(returnId);
        // Check if the ReturnTransaction does not exist
        if (returnTransaction == null) {
            System.err.println("deleteReturnTransaction: The ReturnTransaction does not exist");
            return false;
        }

        // TODO

        return true;
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

            SaleConfirmedEnsurePersistence(sale,  newBalanceUpdate(sale.getPrice()));


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
        File file = new File("CARDS.txt");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String st;
        String[] s;
        Double amount=-1.0;
        try {
        while ((st = br.readLine()) != null) {

                    s = st.split(",");
                    if (s[0].equals(creditCard)) {
                        amount = Double.parseDouble(s[1]);
                        break;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(amount==-1){
                return false;
            } else{
                if(sale.PaySaleAndReturnChange(amount, false)>=0){
                    SaleConfirmedEnsurePersistence(sale,newBalanceUpdate(sale.getPrice()));

                    return true;
                } else{
                    return false;
                    //BE CAREFUL CHECK I ASSUME THAT THEN LATER DELETESALETRANSACTION WILL BE CALLED
                }
            }

        }


    /**
     * This method record the payment of a closed return transaction with given id. The return value of this method is the
     * amount of money to be returned.
     * This method affects the balance of the application.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param returnId the id of the return transaction
     *
     * @return  the money returned to the customer
     *          -1  if the return transaction is not ended,
     *              if it does not exist,
     *              if there is a problem with the db
     *
     * @throws InvalidTransactionIdException if the return id is less than or equal to 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        System.out.println("Call returnCashPayment(returnId = "+ returnId +")");

        // Check if the returnId is null or less than or equal to 0
        if (returnId == null || returnId <= 0) {
            System.err.println("returnCashPayment: The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println("returnCashPayment: There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get ReturnTransaction
        ReturnTransactionImpl returnTransaction = getReturnTransactionById(returnId);
        // Check if the ReturnTransaction does not exist
        if (returnTransaction == null) {
            System.out.println("returnCashPayment: The ReturnTransaction does not exist");
            return -1;
        }

        // TODO

        return 0;
    }

    /**
     * This method record the payment of a return transaction to a credit card.
     * The credit card number validity should be checked. It should follow the luhn algorithm.
     * The credit card should be registered and its balance will be affected.
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param returnId the id of the return transaction
     * @param creditCard the credit card number of the customer
     *
     * @return  the money returned to the customer
     *          -1  if the return transaction is not ended,
     *              if it does not exist,
     *              if the card is not registered,
     *              if there is a problem with the db
     *
     * @throws InvalidTransactionIdException if the return id is less than or equal to 0
     * @throws InvalidCreditCardException if the credit card number is empty, null or if luhn algorithm does not
     *                                      validate the credit card
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        System.out.println("Call returnCreditCardPayment(returnId = "+ returnId +", creditCard = "+ creditCard +")");

        // Check if the returnId is null or less than or equal to 0
        if (returnId == null || returnId <= 0) {
            System.err.println("returnCreditCardPayment: The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if the creditCard is null, empty or invalid
        if (creditCard == null || creditCard.isEmpty() || isValidCreditCard(creditCard)) {
            System.err.println("returnCreditCardPayment: The creditCard is null, empty or invalid");
            throw new InvalidCreditCardException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println("returnCreditCardPayment: There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get ReturnTransaction
        ReturnTransactionImpl returnTransaction = getReturnTransactionById(returnId);
        // Check if the ReturnTransaction does not exist
        if (returnTransaction == null) {
            System.err.println("returnCreditCardPayment: The ReturnTransaction does not exist");
            return -1;
        }

        // TODO

        return 0;
    }

    /**
     * This method record a balance update. <toBeAdded> can be both positive and negative. If positive the balance entry
     * should be recorded as CREDIT, if negative as DEBIT. The final balance after this operation should always be
     * positive.
     * It can be invoked only after a user with role "Administrator", "ShopManager" is logged in.
     *
     * @param toBeAdded the amount of money (positive or negative) to be added to the current balance. If this value
     *                  is >= 0 than it should be considered as a CREDIT, if it is < 0 as a DEBIT
     *
     * @return  true if the balance has been successfully updated
     *          false if toBeAdded + currentBalance < 0.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        System.out.println("Call recordBalanceUpdate(toBeAdded = "+ toBeAdded +")");

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageAccounting()) {
            System.err.println("recordBalanceUpdate: There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get current balance
        double currentBalance = currentBalance();
        // Check if toBeAdded + currentBalance < 0
         if (toBeAdded + currentBalance < 0) {
             System.err.println("recordBalanceUpdate: toBeAdded + currentBalance < 0");
             return false;
         }

        newBalanceUpdate(toBeAdded);

        return true;
    }

    /**
     * This method returns a list of all the balance operations (CREDIT,DEBIT,ORDER,SALE,RETURN) performed between two
     * given dates.
     * This method should understand if a user exchanges the order of the dates and act consequently to correct
     * them.
     * Both <from> and <to> are included in the range of dates and might be null. This means the absence of one (or
     * both) temporal constraints.
     *
     *
     * @param from the start date : if null it means that there should be no constraint on the start date
     * @param to the end date : if null it means that there should be no constraint on the end date
     *
     * @return All the operations on the balance whose date is <= to and >= from
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        System.out.println("Call getCreditsAndDebits(from = "+ from +", to = "+ to +")");

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageAccounting()) {
            System.err.println("getCreditsAndDebits: There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Check if a user exchanges the order of the dates
        if ((from != null) && (to != null)) {
            if (to.isBefore(from)) {
                LocalDate tmp = from;
                from = to;
                to = tmp;
            }
        }

        String query = "SELECT BalanceId, Date, amount, Type FROM BALANCEOPERATIONS";
        List<BalanceOperation> balanceOperations = new ArrayList<>();
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                int balanceId = rs.getInt("BalanceId");
                LocalDate date = LocalDate.parse(rs.getString("Date"));
                double amount = rs.getDouble("amount");
                String type = rs.getString("Type");

                if ((from != null) && (to != null)) {
                    if ((date.isAfter(from) || (date.isEqual(from))) && ((date.isBefore(to)) || (date.isEqual(to)))) {
                        balanceOperations.add(new BalanceOperationImpl(balanceId, date, amount, type));
                    }
                }
                if ((from != null) && (to == null)) {
                    if (date.isAfter(from) || date.isEqual(from)) {
                        balanceOperations.add(new BalanceOperationImpl(balanceId, date, amount, type));
                    }
                }
                if ((from == null) && (to != null)) {
                    if (date.isBefore(to) || date.isEqual(to)) {
                        balanceOperations.add(new BalanceOperationImpl(balanceId, date, amount, type));
                    }
                }
                if ((from == null) && (to == null)) {
                    balanceOperations.add(new BalanceOperationImpl(balanceId, date, amount, type));
                }
            }
        } catch (SQLException e) {
            System.err.println("getCreditsAndDebits: " + e.getMessage());
            return null;
        }

        return balanceOperations;
    }

    /**
     * This method returns the actual balance of the system.
     *
     * @return the value of the current balance
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public double computeBalance() throws UnauthorizedException {
        System.out.println("Call computeBalance()");

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageAccounting()) {
            System.err.println("computeBalance: There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        return currentBalance();
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
            ResultSet rs    = pstmt.executeQuery();
            if(rs.isBeforeFirst()) {
                c = new CustomerImpl( rs.getString("CustomerName"), rs.getString("CustomerCard"), rs.getInt("CustomerId"), rs.getInt("Points") );

            }
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
            ResultSet rs    = pstmt.executeQuery();

            if(rs.isBeforeFirst()) {
                bal = new BalanceOperationImpl(rs.getInt("BalanceId"), LocalDate.parse(rs.getString("Date")), rs.getDouble("amount"), rs.getString("Type"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return bal;

    }
    private BalanceOperationImpl getBal(int b) {
        BalanceOperationImpl ba=getBalanceById(b);
        if(ba!=null){
            return ba;
        } else{
            return new BalanceOperationImpl();
        }
    }

    SaleTransactionImpl getSaleTransactionById(Integer transactionId) {
        if (currentsale != null && currentsale.getTicketNumber().equals(transactionId)){
            return currentsale;
        }

        String query = "SELECT * FROM SALETRANSACTIONS WHERE transactionId = ?";
        SaleTransactionImpl sale = null;
        System.out.println("getting sale id");
        try (PreparedStatement pstmt  = this.conn.prepareStatement(query)) {
            pstmt.setInt(1,transactionId);
            ResultSet rs = pstmt.executeQuery();

            if(rs.isBeforeFirst()) {
                String state = rs.getString("State");
                String paymentType = rs.getString("PaymentType");
                Double amount = rs.getDouble("Amount");
                Double discountRate = rs.getDouble("discountRate");
                CustomerImpl customer = getCustomerById(rs.getInt("transactionCardId"));
                BalanceOperationImpl balanceOperation = getBal(rs.getInt("BalanceOperationId"));
                HashMap<ProductTypeImpl, Integer> listOfProductsSale = getProdListForSaleDB(transactionId);
                sale = new SaleTransactionImpl(transactionId, state, paymentType, amount, discountRate, customer, balanceOperation, listOfProductsSale);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("getting sale id");
        return sale;
    }



    ProductTypeImpl getProductTypeByCode(String barCode){

        //IF PRODUCT IS INVOLVED IN CURRENT SALE THE UP TO DATE INFORMATION ARE STORED ONLY IN RAM AT THE MOMENT
        List<ProductTypeImpl> prodl=null;
        if (currentsale != null) {
            prodl= currentsale.listOfProductsSale.keySet().stream().filter(e->e.getBarCode().equals(barCode)).collect(Collectors.toList());

            if (prodl.size()>0){
                // System.out.println("ok");
                return prodl.get(0);
            }
        }

        //Retrieving product
        String sql = "SELECT * FROM PRODUCTTYPES AS P WHERE P.BarCode=? ";
        ProductTypeImpl p;
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

    //GET NEW ID

    public Integer getNewSaleTransactionId(){
        Integer tid=-1;

        String getnewid = "SELECT COALESCE(MAX(transactionId),0) FROM SALETRANSACTIONS";

        try (
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(getnewid)){

            tid=rs.getInt(1);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tid+1;
    }

    private int getNewBalanceOperationId() {
        Integer tid=-1;
        String getnewid = "SELECT COALESCE(MAX(BalanceId),'0') FROM BALANCEOPERATIONS";

        try (
                Statement stmt  = conn.createStatement();
                ResultSet rs    = stmt.executeQuery(getnewid)){

            tid=rs.getInt(1);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tid+1;
    }

    //PERSISTENCE WHEN SALE FINISHES

    private void SaleConfirmedEnsurePersistence(SaleTransactionImpl sale, BalanceOperationImpl b){

        //UPDATE SALETRANSACTIONTABLE
        String sql = "INSERT INTO SALETRANSACTIONS(transactionId,State,PaymentType, Amount, discountRate, transactionCardId, BalanceOperationId) VALUES(?,?,?,?,?,?,?)";

        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setInt(1, sale.getTicketNumber());
            pstmt.setString(2, sale.getStateString());
            pstmt.setString(3, sale.getPayString());
            pstmt.setDouble(4, sale.getPrice());
            pstmt.setDouble(5, sale.getDiscountRate());
          //  MISSING I NEED THAT WHO WRITES MODIFY POINTS ON CARD ASSOCIATE THE CARD WITH THE SALE
            pstmt.setInt(6, 1/*sale.getTransactionCard().getId()*/   );
            pstmt.setInt(7, b.getBalanceId());
            pstmt.executeUpdate();
        } catch (SQLException var6) {
            System.out.println(var6.getMessage());
        }

        Integer tid=sale.getTicketNumber();

       // HashMap<ProductTypeImpl, Integer> map= new HashMap<>();
     //UPDATE SALESANDPRODUCTS TABLE
      sale.getListOfProductsSale().entrySet().stream().forEach((el)-> {
       // map=sale.getListOfProductsSale();
       // for(HashMap.Entry<ProductTypeImpl, Integer> el : map.entrySet()){
      //  Iterator it=sale.getListOfProductsSale().entrySet().iterator();
       //         while(it.hasNext()){

            String sl = "INSERT INTO SALESANDPRODUCTS(transactionId,BarCode,Quantity) VALUES(?,?,?)";

            try (
                    PreparedStatement pstmt = this.conn.prepareStatement(sl)) {

                // set the value of the parameter

                pstmt.setInt(1, tid);
                pstmt.setString(2, el.getKey().getBarCode());
                pstmt.setInt(3, el.getValue());
                //
                pstmt.executeUpdate();


            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            //UPDATE INVENTORY
          String sp = "UPDATE PRODUCTTYPES SET Quantity=Quantity-? WHERE BarCode=?";

          try (
                  PreparedStatement pstmt = conn.prepareStatement(sp)) {

              // set the value of the parameter


              pstmt.setInt(1, el.getValue());
              pstmt.setString(2, el.getKey().getBarCode());

              //
              pstmt.executeUpdate();

          } catch (SQLException e) {
              System.out.println(e.getMessage());
          }


      });

         /* !!!!PROBABLY NOT NEEDED SINCE POINTS ARE UPDATED THROUGH MODIFYPOINTS ON CARD AND NOT IN MY PART
            //UPDATE CUSTOMER POINTS

          String salecustomersql = "UPDATE CUSTOMERS SET Points=? WHERE customerId=?";

          try (
                  PreparedStatement pstmt = conn.prepareStatement(salecustomersql)) {

              // set the value of the parameter
              pstmt.setInt(1,sale.getTransactionCard().getPoints());
              pstmt.setInt(2, sale.getTransactionCard().getId());
              ResultSet rs = pstmt.executeQuery();

          } catch (SQLException e) {
              System.out.println(e.getMessage());
          }
*/
          sale=null;
    }

    public BalanceOperationImpl newBalanceUpdate(Double amount){
        int  id=getNewBalanceOperationId();

        LocalDate now = LocalDate.now();
        if(amount>=0){
            BalanceOperationImpl bal=new BalanceOperationImpl(id,now,amount,"CREDIT" );
            addBalanceToBalancesList(bal);
            return bal;
        } else{
            BalanceOperationImpl bala=new BalanceOperationImpl(id,now,amount,"DEBIT" );
            addBalanceToBalancesList(bala);
            return bala;
        }

    }




    public void addBalanceToBalancesList(BalanceOperationImpl b){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //balanceOperationsList.add(b); NO MORE SINCE NO MORE LISTS
        String sql = "INSERT INTO BALANCEOPERATIONS(BalanceId,Date, Amount, Type) VALUES(?,?,?,?)";

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
        String salesandproductssql = "SELECT BarCode, Quantity FROM SALESANDPRODUCTS WHERE transactionId=?";
        HashMap< ProductTypeImpl, Integer> map= new HashMap<>();
        try (PreparedStatement pstmt  = conn.prepareStatement(salesandproductssql)) {
            // set the value of the parameter
            pstmt.setDouble(1,tid);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                ProductTypeImpl productType = getProductTypeByCode(rs.getString("BarCode"));
                Integer quantity = rs.getInt("Quantity");
                map.put(productType, quantity);
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

    private ReturnTransactionImpl getReturnTransactionById(Integer returnId) {
        System.out.println("Call getReturnTransactionById(returnId = "+ returnId +")");

        String query = "SELECT BarCode, Quantity FROM RETURN_PRODUCTS WHERE ReturnId = ?";
        Map<ProductTypeImpl, Integer> listOfProductsReturn = new HashMap<>();
        try (PreparedStatement pstmt  = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, returnId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String barCode = rs.getString("BarCode");
                Integer quantity = rs.getInt("Quantity");
                listOfProductsReturn.put(getProductTypeByCode(barCode), quantity);
            }
        } catch (SQLException e) {
            System.err.println("getReturnTransactionById: " + e.getMessage());
            return null;
        }

        query = "SELECT * FROM RETURN_TRANSACTIONS WHERE ReturnId = ?";
        ReturnTransactionImpl returnTransaction = null;
        try (PreparedStatement pstmt  = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, returnId);
            ResultSet rs = pstmt.executeQuery();

            if(rs.isBeforeFirst()) {
                Integer transactionId = rs.getInt("TransactionId");
                String state = rs.getString("State");
                String paymentType = rs.getString("PaymentType");
                Double amount = rs.getDouble("Amount");
                Integer balanceId = rs.getInt("BalanceId");

                returnTransaction = new ReturnTransactionImpl(returnId, getSaleTransactionById(transactionId), listOfProductsReturn, state, paymentType, amount, getBalanceById(balanceId));
            }
        } catch (SQLException e) {
            System.err.println("getReturnTransactionById: " + e.getMessage());
            return null;
        }

        return returnTransaction;
    }

    private double currentBalance() {
        System.out.println("Call currentBalance()");

        String query = "SELECT amount FROM BALANCEOPERATIONS";
        double currentBalance = 0;
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                currentBalance += rs.getDouble("amount");
            }
            System.out.println("currentBalance: currentBalance = "+ currentBalance);
        } catch (SQLException e) {
            System.err.println("currentBalance: " + e.getMessage());
            return 0;
        }

        return currentBalance;
    }
}
