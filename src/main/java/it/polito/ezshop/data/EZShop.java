package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;


public class EZShop implements EZShopInterface {

    private UserImpl loggedIn;
    SaleTransactionImpl currentSale;
    Connection conn;

    public EZShop() {

        this.loggedIn = null;
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:Database.sqlite");
        } catch (SQLException e) {
            System.err.println("Error with db connection");
            throw new RuntimeException(e);
        }
        System.out.println("Connection with db ok");

    }

    public boolean closeDB() {
        if (this.conn == null) {
            return false;
        }
        try {
            this.conn.close();
        } catch (SQLException e) {
            System.err.println("Error with db connection");
            throw new RuntimeException(e);
        }

        return true;
    }


    @Override
    public void reset() {
        loggedIn = null;
        currentSale = null;
        try {
            Statement st = this.conn.createStatement();
            String deleteAllCustomers = "DELETE FROM CUSTOMERS WHERE CustomerId > 0";
            st.executeUpdate(deleteAllCustomers);
        } catch (SQLException e) {
            System.out.println("Error with db connection deleting customers");
            e.printStackTrace();

        }
        try {
            Statement st = this.conn.createStatement();
            String deleteAllusers = "DELETE FROM USERS WHERE Id > 0";
            st.executeUpdate(deleteAllusers);
        } catch (SQLException e) {
            System.out.println("Error with db connection deleting users");
            e.printStackTrace();
        }
        try {
            Statement st = this.conn.createStatement();
            String deleteAllProducts = "DELETE FROM PRODUCTTYPES WHERE productId > 0";
            st.executeUpdate(deleteAllProducts);
        } catch (SQLException e) {
            System.out.println("Error with db connection deleting products");
            e.printStackTrace();
        }
        try {
            Statement st = this.conn.createStatement();
            String deleteAllOrders = "DELETE FROM ORDERS WHERE orderId > 0";
            st.executeUpdate(deleteAllOrders);
        } catch (SQLException e) {
            System.out.println("Error with db connection deleting orders");
            e.printStackTrace();
        }
        try {
            Statement st = this.conn.createStatement();
            String deleteAllBalances = "DELETE FROM BALANCE_OPERATIONS WHERE BalanceId > 0";
            st.executeUpdate(deleteAllBalances);
        } catch (SQLException e) {
            System.out.println("Error with db connection deleting orders");
            e.printStackTrace();
        }
        try {
            Statement st = this.conn.createStatement();
            String deleteAllBalances = "DELETE FROM SALETRANSACTIONS";
            st.executeUpdate(deleteAllBalances);
        } catch (SQLException e) {
            System.out.println("Error with db connection deleting sale transactions");
            e.printStackTrace();
        }
        try {
            Statement st = this.conn.createStatement();
            String deleteAllBalances = "DELETE FROM SALESANDPRODUCTS";
            st.executeUpdate(deleteAllBalances);
        } catch (SQLException e) {
            System.out.println("Error with db connection deleting products in sale transactions");
            e.printStackTrace();
        }
        try {
            Statement st = this.conn.createStatement();
            String deleteAllBalances = "DELETE FROM RETURN_TRANSACTIONS";
            st.executeUpdate(deleteAllBalances);
        } catch (SQLException e) {
            System.out.println("Error with db connection deleting return transactions");
            e.printStackTrace();
        }
        try {
            Statement st = this.conn.createStatement();
            String deleteAllBalances = "DELETE FROM RETURN_PRODUCTS";
            st.executeUpdate(deleteAllBalances);
        } catch (SQLException e) {
            System.out.println("Error with db connection deleting products in return transactions");
            e.printStackTrace();
        }
    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {


        //Check if username is valid
        if (username==null || username.isBlank()) {
            System.out.println("Invalid username");
            throw new InvalidUsernameException();
        }
        //Check if password is valid
        if (password==null || password.isBlank()) {
            System.out.println("Invalid password");
            throw new InvalidPasswordException();
        }
        //Check if role is valid
        if(role==null || role.isBlank() || !UserImpl.isAllowedRole(role)) {
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
        if(role==null || role.isBlank() || !UserImpl.isAllowedRole(role)) {
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
        if(username==null || username.isBlank()) {
            System.out.println("Invalid login username");
            throw new InvalidUsernameException();
        }
        //Checking if password is null or empty
        if(password ==null || password.isBlank()) {
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
        if(description==null || description.isBlank()) {
            System.out.println("Invalid product description");
            throw new InvalidProductDescriptionException();
        }
        //Checking if barcode is null or empty and if it is valid
        if(productCode==null || productCode.isBlank()||!ProductTypeImpl.isValidCode(productCode)) {
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
        String sql3 = "INSERT INTO PRODUCTTYPES(productId,BarCode,Description,SellPrice,Quantity,prodDiscountRate,notes,aisleId,rackID,levelID) VALUES(?,?,?,?,0,0,?,0,'empty',0)";
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
        if(newDescription==null || newDescription.isBlank()) {
            System.out.println("Invalid product description");
            throw new InvalidProductDescriptionException();
        }
        //Checking if barcode is null or empty and if it is valid
        if(newCode==null || newCode.isBlank()||!ProductTypeImpl.isValidCode(newCode)) {
            System.out.println("Invalid product code");
            throw new InvalidProductCodeException();
        }
        //Checking if pricePerUnit is >0
        if( newPrice<=0) {
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

        //Retrieving products and adding them to a list
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
        if(barCode==null || barCode.isBlank()||!ProductTypeImpl.isValidCode(barCode)) {
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
        if(description==null ||description.equals("")){ //NOT SURE ABOUT THIS API DESCRIPTION IS NOT CLEAR
            return getAllProductTypes();
        } else {
            return getAllProductTypes().stream().filter(e -> e.getProductDescription().contains(description)).collect(Collectors.toList());
        }
    /*
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

     */
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

        Integer newQuantity = p.getQuantity()+toBeAdded;

        //Checking if location is set
        if(p.getLocation().equals("")) {
            System.out.println("Cannot set quantity if location is not set first");
            return false;
        }

        //Updating product
        String sql2 = "UPDATE PRODUCTTYPES SET Quantity=? WHERE productId=?";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql2);
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Quantity " + toBeAdded + " has now been added to the ");
        System.out.println("tot"+ p.getQuantity());
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

        Integer aisleId;
        String rackId;
        Integer levelId;

        if(newPos.equals(""))
        {
            aisleId = 0;
            rackId = "empty";
            levelId = 0;
        } else {
            aisleId = ProductTypeImpl.extractAisleId(newPos);
            rackId = ProductTypeImpl.extractRackId(newPos);
            levelId = ProductTypeImpl.extractLevelId(newPos);
        }

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
        //User authentication
        if(loggedIn == null || !loggedIn.canManageInventory()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Checking if barcode is null or empty and if it is valid
        if(productCode ==null || productCode.isBlank()||!ProductTypeImpl.isValidCode(productCode)) {
            System.out.println("Invalid product code");
            throw new InvalidProductCodeException();
        }

        //Checking if quantity is valid
        if(quantity<=0) {
            System.out.println("Quantity not set correctly");
            throw new InvalidQuantityException();
        }

        //Checking if quantity is valid
        if(pricePerUnit<=0) {
            System.out.println("Price per unit not set correctly");
            throw new InvalidPricePerUnitException();
        }

        //Checking if product barcode is already present
        String sql = "SELECT * FROM PRODUCTTYPES AS P WHERE P.BarCode=?";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setString(1, productCode);
            ResultSet rs = pstmt.executeQuery();
            if(!rs.isBeforeFirst()) {
                System.out.println("Product with barcode " + productCode + " is not present");
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        //Calculating new ID
        Integer id;
        String sql2 = "SELECT MAX(orderId) FROM ORDERS";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql2);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() == false)
                id = 1;
            else
                id = rs.getInt(1) + 1;

        } catch (SQLException e) {
            System.err.println("Error with db connection");
            e.printStackTrace();
            return -1;
        }

        //Inserting order
        String sql3 = "INSERT INTO ORDERS(orderId,productCode,pricePerUnit,quantity,status) VALUES(?,?,?,?,'ISSUED')";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql3);
            pstmt.setInt(1,id);
            pstmt.setString(2, productCode);
            pstmt.setDouble(3, pricePerUnit);
            pstmt.setInt(4, quantity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error with db connection");
            e.printStackTrace();
            return -1;
        }
        System.out.println("Order " + id + " for product " + productCode + " issued for a price of " + pricePerUnit*quantity);
        return id;
    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageInventory()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Checking if barcode is null or empty and if it is valid
        if(productCode==null || productCode.isBlank()||!ProductTypeImpl.isValidCode(productCode)) {
            System.out.println("Invalid product code");
            throw new InvalidProductCodeException();
        }

        //Checking if quantity is valid
        if(quantity<=0) {
            System.out.println("Quantity not set correctly");
            throw new InvalidQuantityException();
        }

        //Checking if quantity is valid
        if(pricePerUnit<=0) {
            System.out.println("Price per unit not set correctly");
            throw new InvalidPricePerUnitException();
        }

        //Checking if product barcode is already present
        String sql = "SELECT * FROM PRODUCTTYPES AS P WHERE P.BarCode=?";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setString(1, productCode);
            ResultSet rs = pstmt.executeQuery();
            if(!rs.isBeforeFirst()) {
                System.out.println("Product with barcode " + productCode + " is not present");
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
/*
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
*/
        Double currentBalance = getCurrentBalance();
        if (currentBalance == null) {
            System.out.println("There are some problems with the DB");
            return -1;
        }
        if(currentBalance - (quantity*pricePerUnit) < 0) {
            System.out.println("Balance is not enough to pay the order");
            return -1;
        }

        BalanceOperationImpl boi = newBalanceUpdate(-(quantity*pricePerUnit));
        if (boi == null) {
            System.out.println("There are some problems with the DB");
            return -1;
        }
        Integer balanceId = boi.getBalanceId();

        //Calculating new ID
        Integer id;
        String sql2 = "SELECT MAX(orderId) FROM ORDERS";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql2);
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst() == false)
                id = 1;
            else
                id = rs.getInt(1) + 1;

        } catch (SQLException e) {
            System.err.println("Error with db connection");
            throw new RuntimeException(e);
        }

        //Inserting order
        String sql3 = "INSERT INTO ORDERS(orderId,productCode,pricePerUnit,quantity,status,balanceId) VALUES(?,?,?,?,'PAYED',?)";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql3);
            pstmt.setInt(1,id);
            pstmt.setString(2, productCode);
            pstmt.setDouble(3, pricePerUnit);
            pstmt.setInt(4, quantity);
            pstmt.setInt(5,balanceId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error with db connection");
            e.printStackTrace();
            return -1;
        }
        System.out.println("Order " + id + "for product" + productCode + " is ordered and has been payed " + pricePerUnit*quantity);
        return id;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageInventory()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Checking if orderId is valid
        if(orderId == null || orderId<=0) {
            System.out.println("Order id not valid");
            throw new InvalidOrderIdException();
        }

        //Searching for orderId and doing preliminary controls
        String sql = "SELECT * FROM ORDERS WHERE orderId=?";
        String actualState;
        Integer quantity;
        double pricePerUnit;
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setInt(1,orderId);
            ResultSet rs = pstmt.executeQuery();
            if(!rs.isBeforeFirst()) {
                System.out.println("There's no order with orderId " + orderId);
                return false;
            }
            actualState = rs.getString("status");
            quantity = rs.getInt("quantity");
            pricePerUnit = rs.getDouble("pricePerUnit");
        } catch (SQLException e) {
            System.err.println("Error with the db connection");
            e.printStackTrace();
            return false;
        }

        //Checking if order has been payed
        if(actualState.equals("PAYED")) {
            System.out.println("Order is already PAYED");
            return false;
        }
        //Checking if order has been completed
        if(actualState.equals("COMPLETED")) {
            System.out.println("Order is already COMPLETED");
        }

        Double currentBalance = getCurrentBalance();
        if (currentBalance == null) {
            System.out.println("There are some problems with the DB");
            return false;
        }
        if(currentBalance - (quantity*pricePerUnit) < 0) {
            System.out.println("Balance is not enough to pay the order");
            return false;
        }

        BalanceOperationImpl boi = newBalanceUpdate(-(quantity*pricePerUnit));
        if (boi == null) {
            System.out.println("There are some problems with the DB");
            return false;
        }
        Integer balanceId = boi.getBalanceId();

        //Updating order
        String sql2 = "UPDATE ORDERS SET status='PAYED', balanceId=? WHERE orderId=?";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql2);
            pstmt.setInt(1, balanceId);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageInventory()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Checking if orderId is valid
        if(orderId == null || orderId<=0) {
            System.out.println("Order id not valid");
            throw new InvalidOrderIdException();
        }

        //Searching for orderId and doing preliminary controls
        String sql = "SELECT * FROM ORDERS WHERE orderId=?";
        String actualState;
        String productCode;
        Integer quantity;
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setInt(1,orderId);
            ResultSet rs = pstmt.executeQuery();
            if(!rs.isBeforeFirst()) {
                System.out.println("There's no order with orderId " + orderId);
                return false;
            }
            actualState = rs.getString("status");
            productCode = rs.getString("productCode");
            //ADDED TESTING
            quantity= rs.getInt("quantity");
        } catch (SQLException e) {
            System.err.println("Error with the db connection");
            e.printStackTrace();
            return false;
        }

        //Checking if order has been payed
        if(!actualState.equals("COMPLETED") && !actualState.equals("PAYED")){
            System.out.println("Order is not payed nor completed");
            return false;
        }

        //Checking if location for product is set;
        Integer aisleId=null;
        String rackId=null;
        Integer levelId=null;
        String sql2 = "SELECT * FROM PRODUCTTYPES AS P WHERE P.BarCode=?";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql2);
            pstmt.setString(1, productCode);
            ResultSet rs = pstmt.executeQuery();
            if(!rs.isBeforeFirst()) {
                System.err.println("ERROR: It's impossible to record a product arrival since it's not present anymore");
                return false;
            }
            aisleId = rs.getInt("aisleID");
            rackId = rs.getString("rackID");
            levelId = rs.getInt("levelID");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if(rackId == null)
            rackId="empty";

        if(aisleId == 0 && rackId.equals("empty") && levelId == 0) {
            System.out.println("Location for product is not set. Set location first");
            throw new InvalidLocationException();
        }

        //ADDED DURING TESTING!!!
        //UPDATE INVENTORY
        String sp = "UPDATE PRODUCTTYPES SET Quantity=Quantity+? WHERE BarCode=?";

        try (
                PreparedStatement pstmt = this.conn.prepareStatement(sp)) {

            // set the value of the parameter


            pstmt.setInt(1, quantity);
            pstmt.setString(2, productCode);

            //
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        //Searching for orderId and doing preliminary controls
        String sql3 = "UPDATE ORDERS SET status='COMPLETED' WHERE orderId=?";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql3);
            pstmt.setInt(1,orderId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error with the db connection");
            e.printStackTrace();
            return false;
        }
        System.out.println("Order " + orderId + " has been completed");
        return true;
    }

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
        //User authentication
        if(loggedIn == null || !loggedIn.canManageInventory()) {
            System.out.println("Unauthorized access");
            throw new UnauthorizedException();
        }

        //Retrieving orders and adding them to a list
        String sql = "SELECT * FROM ORDERS";
        List<Order> orders = new ArrayList<>();
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                Integer orderId = rs.getInt("orderId");
                String productCode = rs.getString("productCode");
                Double pricePerUnit = rs.getDouble("pricePerUnit");
                Integer quantity = rs.getInt("quantity");
                String status = rs.getString("status");
                Integer balanceId = rs.getInt("balanceId");
                orders.add(new OrderImpl(balanceId,productCode,pricePerUnit,quantity,status,orderId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return orders;
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

            Statement st = this.conn.createStatement();
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

                Statement st2 = this.conn.createStatement();
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
            Statement st3 = this.conn.createStatement();
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
            Statement st = this.conn.createStatement();
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

            Statement st1 = this.conn.createStatement();
            String updateCustomerName = "UPDATE CUSTOMERS SET CustomerName = '"+newCustomerName+"' WHERE CustomerId="+id+" ";
            st1.executeUpdate(updateCustomerName);

            // Update the card number if newCustomerCard is not null
            if(newCustomerCard != null){


                // Detach if newCustomerCard is an empty string
                if (newCustomerCard.isEmpty()){

                    Statement st3 = this.conn.createStatement();
                    String detachCustomerCard = "UPDATE CUSTOMERS SET CustomerCard = '' WHERE CustomerId="+id+" ";
                    st3.executeUpdate(detachCustomerCard);

                    Statement st4 = this.conn.createStatement();
                    String removePoints = "UPDATE CUSTOMERS SET Points = 0 WHERE CustomerId="+id+" ";
                    st4.executeUpdate(removePoints);

                    return true;
                }

                // Check if newCustomerCard is already attached
                else {

                    Statement st5 = this.conn.createStatement();
                    ResultSet res5 = st5.executeQuery("SELECT * FROM CUSTOMERS");

                    while (res5.next()){
                        if (res5.getString("CustomerCard").equals(newCustomerCard)) {
                            return false;
                        }
                    }

                }

                Statement st6 = this.conn.createStatement();
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
            Statement st = this.conn.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM CUSTOMERS");

            Boolean idFound = false;

            while (res.next()){
                if (res.getInt("CustomerId") == id){
                    idFound = true;
                }
            }

            if (!idFound) { return false;}

            Statement st1 = this.conn.createStatement();
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

            Statement st = this.conn.createStatement();
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

            Statement st = this.conn.createStatement();
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

                Statement st = this.conn.createStatement();
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

        // Check if customerCard contains only digits
        try {
            Integer.parseInt(customerCard);
        }
        catch(Exception e) {
            throw new InvalidCustomerCardException("The customer's card is not in a valid format");
        }

        if (this.loggedIn == null || !this.loggedIn.canManageCustomers()){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to attach a card to customer");
        }


        try {

            Statement st = this.conn.createStatement();
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
            Statement st2 = this.conn.createStatement();
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

        // Check if customerCard contains only digits
        try {
            Integer.parseInt(customerCard);
        }
        catch(Exception e) {
            throw new InvalidCustomerCardException("The customer's card is not in a valid format");
        }

        if (this.loggedIn == null || !this.loggedIn.canManageCustomers()){
            throw new UnauthorizedException("There is no logged user or this user has not the rights to modify points on a card");
        }


        try {

            Customer c = null;
            Statement st = this.conn.createStatement();
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

            Statement st2 = this.conn.createStatement();
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

        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName );



        if( loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
            //return -1;
        }


        Integer newtransactionId=getNewSaleTransactionId();
        currentSale = new SaleTransactionImpl(newtransactionId);
        //addSaleToSalesList(sale); NO MROE SICNE WE USE DB
        System.out.println(currentSale.getTicketNumber());
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
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(transactionId = "+ transactionId + "productCode ="+ productCode+")");

        // Check if the transactionId is null or it is less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println(methodName + ": The transactionId is null or it is less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if the productCode is null, empty or invalid
        if (productCode == null || productCode.isEmpty() || !ProductTypeImpl.isValidCode(productCode)) {
            System.err.println(methodName + ": The productCode is null, empty or invalid");
            throw new InvalidProductCodeException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Check if the quantity is less than or equal to 0
        if (amount <= 0) {
            System.err.println(methodName + ": The quantity is less than or equal to 0");
            throw new InvalidQuantityException();
        }

        // Get SaleTransaction
        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        // Check if the SaleTransaction is not available
        if (sale == null) {
            System.err.println(methodName + ": The SaleTransaction is not available");
            return false;
        }
        // Check if SaleTransaction does not identify a started and open transaction
        if (!sale.isInProgress()) {
            System.err.println(methodName + ": The SaleTransaction does not identify a started and open transaction");
            return false;
        }

        //Get prod
        ProductTypeImpl product= getProductTypeByCode(productCode);

        if (product == null) {
            System.err.println(methodName + ": The Product does not exist");
            return false;
        }

        if ((product.getQuantity() < amount)){
            return false;
        }

        if(sale.EditProductInSale(product, amount)) {
            return true;
        } else{
            return false;
        }

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
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(transactionId = "+ transactionId + "productCode ="+ productCode+")");

        // Check if the transactionId is null or it is less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println(methodName + ": The transactionId is null or it is less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if the productCode is null, empty or invalid
        if (productCode == null || productCode.isEmpty() || !ProductTypeImpl.isValidCode(productCode)) {
            System.err.println(methodName + ": The productCode is null, empty or invalid");
            throw new InvalidProductCodeException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Check if the quantity is less than or equal to 0
        if (amount <= 0) {
            System.err.println(methodName + ": The quantity is less than or equal to 0");
            throw new InvalidQuantityException();
        }

        // Get SaleTransaction
        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        // Check if the SaleTransaction is not available
        if (sale == null) {
            System.err.println(methodName + ": The SaleTransaction is not available");
            return false;
        }
        // Check if SaleTransaction does not identify a started and open transaction
        if (!sale.isInProgress()) {
            System.err.println(methodName + ": The SaleTransaction does not identify a started and open transaction");
            return false;
        }

        //Get prod
        ProductTypeImpl product= getProductTypeByCode(productCode);
        if (product == null) {
            System.err.println(methodName + ": The Product does not exist");
            return false;
        }

        if ((currentSale.getListOfProductsEntries().get(product.getBarCode()).getAmount()<amount )){
           return false;
        }

        //AMOUNT IN DB UPDATED ONLY AT THE END


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
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(transactionId = "+ transactionId + "productCode ="+ productCode+")");

        // Check if the transactionId is null or it is less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println(methodName + ": The transactionId is null or it is less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if the productCode is null, empty or invalid
        if (productCode == null || productCode.isEmpty() || !ProductTypeImpl.isValidCode(productCode)) {
            System.err.println(methodName + ": The productCode is null, empty or invalid");
            throw new InvalidProductCodeException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Check if the quantity is less than or equal to 0
        if ((discountRate <=0)||(discountRate >= 1)) {
            System.err.println(methodName + ": Invalid discoountRate");
            throw new InvalidDiscountRateException();
        }

        // Get SaleTransaction
        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        // Check if the SaleTransaction is not available
        if (sale == null) {
            System.err.println(methodName + ": The SaleTransaction is not available");
            return false;
        }
        // Check if SaleTransaction does not identify a started and open transaction
        if (!sale.isInProgress()) {
            System.err.println(methodName + ": The SaleTransaction does not identify a started and open transaction");
            return false;
        }

        //Get prod
        ProductTypeImpl product= getProductTypeByCode(productCode);

        if (product == null) {
            System.err.println(methodName + ": The Product does not exist");
            return false;
        }

        return sale.ApplyDiscountToSaleProduct(discountRate, product);

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
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(transactionId = "+ transactionId +")");

        // Check if the transactionId is null or it is less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println(methodName + ": The transactionId is null or it is less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Check if the quantity is less than or equal to 0
        if ((discountRate <=0)||(discountRate >= 1)) {
            System.err.println(methodName + ": Invalid discoountRate");
            throw new InvalidDiscountRateException();
        }

        // Get SaleTransaction
        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        // Check if the SaleTransaction is not available
        if (sale == null) {
            System.err.println(methodName + ": The SaleTransaction is not available");
            return false;
        }
        // Check if SaleTransaction is already payed
        if (sale.isPayed()) {
            System.err.println(methodName + ": The SaleTransaction is already payed");
            return false;
        }


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
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(transactionId = "+ transactionId +")");

        // Check if the transactionId is null or it is less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println(methodName + ": The transactionId is null or it is less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get SaleTransaction
        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        // Check if the SaleTransaction is not available
        if (sale == null) {
            System.err.println(methodName + ": The SaleTransaction is not available");
            return -1;
        }

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
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(transactionId = "+ transactionId +")");

        // Check if the transactionId is null or it is less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println(methodName + ": The transactionId is null or it is less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get SaleTransaction
        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        // Check if the SaleTransaction is not available
        if (sale == null) {
            System.err.println(methodName + ": The SaleTransaction is not available");
            return false;
        }

        if(sale.isClosed() ||sale.isPayed()){
            System.err.println(methodName + ": The SaleTransaction is already closed/payed");
            return false;
        }

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
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(transactionId = "+ transactionId +")");

        // Check if the transactionId is null or it is less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println(methodName + ": The transactionId is null or it is less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get SaleTransaction
        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        // Check if the SaleTransaction is not available
        if (sale == null) {
            System.err.println(methodName + ": The SaleTransaction is not available");
            return false;
        }

        if(sale.isPayed()){
            System.err.println(methodName + ": The SaleTransaction is already payed");
            return false;
        }

        sale.AbortSaleUpdateProductQuantity();
        //RemoveSaleFromSalesList(sale);
        currentSale =null;
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
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(transactionId = "+ transactionId +")");

        // Check if the transactionId is null or it is less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println(methodName + ": The transactionId is null or it is less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get SaleTransaction
        SaleTransactionImpl sale = getSaleTransactionById(transactionId);

        if (sale ==null){
            return null;
        }

        if (!(sale.isClosed()||sale.isPayed())) {
            System.err.println(methodName + ": The SaleTransaction is not closed yet");
            return null;
        }

        return sale;

    }

    @Override
    public Integer startReturnTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(transactionId = "+ transactionId +")");

        // Check if the transactionId is null or it is less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println(methodName + ": The transactionId is null or it is less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get SaleTransaction
        SaleTransactionImpl saleTransaction = getSaleTransactionById(transactionId);
        // Check if the SaleTransaction is not available
        if (saleTransaction == null) {
            System.err.println(methodName + ": The SaleTransaction is not available");
            return -1;
        }
        // Check if the SaleTransaction has not already been sold and payed
        if (!saleTransaction.isPayed()) {
            System.err.println(methodName + ": The SaleTransaction has not already been sold and payed");
            return -1;
        }

        // Create new ReturnTransaction
        ReturnTransactionImpl returnTransaction = new ReturnTransactionImpl(null, saleTransaction, null, "INPROGRESS", 0.0, null, null);

        // Write ReturnTransaction in persistence
        Integer newId = insertPersistenceReturnTransaction(returnTransaction);
        if (newId == null) {
            System.err.println(methodName + ": There are some problems with the DB");
            return -1;
        }

        return newId;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(returnId = "+ returnId +", productCode = "+ productCode +", amount = "+ amount +")");

        // Check if the returnId is null or less than or equal to 0
        if (returnId == null || returnId <= 0) {
            System.err.println(methodName + ": The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if the productCode is null, empty or invalid
        if (productCode == null || productCode.isEmpty() || !ProductTypeImpl.isValidCode(productCode)) {
            System.err.println(methodName + ": The productCode is null, empty or invalid");
            throw new InvalidProductCodeException();
        }

        // Check if the quantity is less than or equal to 0
        if (amount <= 0) {
            System.err.println(methodName + ": The quantity is less than or equal to 0");
            throw new InvalidQuantityException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get ReturnTransaction
        ReturnTransactionImpl returnTransaction = getReturnTransactionById(returnId);
        // Check if the ReturnTransaction does not exist
        if (returnTransaction == null) {
            System.err.println(methodName + ": The ReturnTransaction does not exist");
            return false;
        }

        // Get SaleTransaction
        SaleTransactionImpl saleTransaction = returnTransaction.getSaleTransaction();
        // Check if the SaleTransaction is not available
        if (saleTransaction == null) {
            System.err.println(methodName + ": The SaleTransaction is not available");
            return false;
        }

        // Get ProductType
        ProductTypeImpl productType = getProductTypeByCode(productCode);
        // Check if the product to be returned does not exists
        if (productType == null) {
            System.err.println(methodName + ": The product to be returned does not exists");
            return false;
        }
        // Check if the product was not in the transaction
        if (!saleTransaction.isProductInSale(productType)) {
            System.err.println(methodName + ": The product was not in the transaction");
            return false;
        }
        // Check if the amount is higher than the one in the sale transaction
        if (amount > saleTransaction.getProductQuantity(productType)) {
            System.err.println(methodName + ": The amount is higher than the one in the sale transaction");
            return false;
        }

        // Add product to ReturnTransaction
        returnTransaction.addProduct(productType, amount);

        // Write ReturnTransaction in persistence
        if (!updatePersistenceReturnTransaction(returnTransaction)) {
            System.err.println(methodName + ": There are some problems with the DB");
            return false;
        }

        return true;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(returnId = "+ returnId +", commit = "+ commit +")");

        // Check if the returnId is null or less than or equal to 0
        if (returnId == null || returnId <= 0) {
            System.err.println(methodName + ": The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get ReturnTransaction
        ReturnTransactionImpl returnTransaction = getReturnTransactionById(returnId);
        // Check if the ReturnTransaction does not exist
        if (returnTransaction == null) {
            System.err.println(methodName + ": The ReturnTransaction does not exist");
            return false;
        }
        // Check if the ReturnTransaction is not in progress
        if (!returnTransaction.isInProgress()) {
            System.err.println(methodName + ": The ReturnTransaction is not in progress");
            return false;
        }

        if (commit) {
            for (Map.Entry<ProductTypeImpl, Integer> entry : returnTransaction.getReturnProducts().entrySet()) {
                ProductTypeImpl productType = entry.getKey();

                // Increase the product quantity available on the shelves
                productType.updateProductQuantity(entry.getValue());
                if (!updatePersistenceProductTypeQuantity(productType)) {
                    System.err.println(methodName + ": There are some problems with the DB");
                    return false;
                }

                // Update the transaction status (decreasing the number of units sold by the number of returned one and decreasing the final price)
                SaleTransactionImpl saleTransaction = returnTransaction.getSaleTransaction();
                saleTransaction.updateProductQuantity(productType, -entry.getValue());
                if (!updatePersistenceSaleTransactionQuantity(saleTransaction)) {
                    System.err.println(methodName + ": There are some problems with the DB");
                    return false;
                }
            }

            // Set state in ReturnTransaction
            returnTransaction.setState("CLOSED");

            // Write ReturnTransaction to persistence
            if (!updatePersistenceReturnTransaction(returnTransaction)) {
                System.err.println(methodName + ": There are some problems with the DB");
                return false;
            }
        } else {
            // Delete the ReturnTransaction
            if (!deletePersistenceReturnTransaction(returnTransaction)) {
                System.err.println(methodName + ": There are some problems with the DB");
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(returnId = "+ returnId +")");

        // Check if the returnId is null or less than or equal to 0
        if (returnId == null || returnId <= 0) {
            System.err.println(methodName + ": The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get ReturnTransaction
        ReturnTransactionImpl returnTransaction = getReturnTransactionById(returnId);
        // Check if the ReturnTransaction does not exist
        if (returnTransaction == null) {
            System.err.println(methodName + ": The ReturnTransaction does not exist");
            return false;
        }
        // Check if the ReturnTransaction has been payed
        if (returnTransaction.isPayed()) {
            System.err.println(methodName + ": The ReturnTransaction has been payed");
            return false;
        }
        // Check if the ReturnTransaction is not closed
        if (!returnTransaction.isClosed()) {
            System.err.println(methodName + ": The ReturnTransaction is not closed");
            return false;
        }

        for (Map.Entry<ProductTypeImpl, Integer> entry : returnTransaction.getReturnProducts().entrySet()) {
            ProductTypeImpl productType = entry.getKey();

            // Decrease the product quantity available on the shelves
            productType.updateProductQuantity(-entry.getValue());
            if (!updatePersistenceProductTypeQuantity(productType)) {
                System.err.println(methodName + ": There are some problems with the DB");
                return false;
            }

            // Update the transaction status (increasing the number of units sold by the number of returned one and increasing the final price)
            SaleTransactionImpl saleTransaction = returnTransaction.getSaleTransaction();
            saleTransaction.updateProductQuantity(productType, entry.getValue());
            if (!updatePersistenceSaleTransactionQuantity(saleTransaction)) {
                System.err.println(methodName + ": There are some problems with the DB");
                return false;
            }
        }

        // Delete the ReturnTransaction
        if (!deletePersistenceReturnTransaction(returnTransaction)) {
            System.err.println(methodName + ": There are some problems with the DB");
            return false;
        }

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
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(returnId = "+ transactionId +")");

        // Check if the returnId is null or less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println(methodName + ": The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Check if the cash is less than or equal to 0
        if(cash <= 0){
            throw new InvalidPaymentException();
        }

        // Get SaleTransaction
        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        // Check if the SaleTransaction is not available
        if (sale == null) {
            System.err.println(methodName + ": The SaleTransaction is not available");
            return -1;
        }

        double change=sale.PaySaleAndReturnChange(cash, true);
        if(change!=-1) {
            BalanceOperationImpl balanceOperation = newBalanceUpdate(sale.getPrice());
            if (balanceOperation == null) {
                System.err.println(methodName + ": There are some problems with the DB");
                return -1;
            }
            SaleConfirmedEnsurePersistence(sale, balanceOperation);


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
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(returnId = "+ transactionId +", creditCard = "+ creditCard +")");

        // Check if the transactionId is null or less than or equal to 0
        if (transactionId == null || transactionId <= 0) {
            System.err.println(methodName + ": The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if the creditCard is null, empty or invalid
        if (!isValidCreditCard(creditCard)) {
            System.err.println(methodName + ": The creditCard is null, empty or invalid");
            throw new InvalidCreditCardException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        SaleTransactionImpl sale = getSaleTransactionById(transactionId);
        // Check if the SaleTransaction is not available
        if (sale == null) {
            System.err.println(methodName + ": The SaleTransaction is not available");
            return false;
        }

        Double amount = getCreditCardBalance(creditCard);
        if(amount==null){
            return false;
        } else{
            double newbal=sale.PaySaleAndReturnChange(amount, false);
            if(newbal>=0){
                // Update the creditCard balance
                // if (!updateCreditCardBalance(creditCard, newbal)) {
                //     return false;
                // }
                BalanceOperationImpl balanceOperation = newBalanceUpdate(sale.getPrice());
                if (balanceOperation == null) {
                    System.err.println(methodName + ": There are some problems with the DB");
                    return false;
                }
                SaleConfirmedEnsurePersistence(sale, balanceOperation);

                return true;
            } else{
                return false;
                //BE CAREFUL CHECK I ASSUME THAT THEN LATER DELETESALETRANSACTION WILL BE CALLED
            }
        }
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(returnId = "+ returnId +")");

        // Check if the returnId is null or less than or equal to 0
        if (returnId == null || returnId <= 0) {
            System.err.println(methodName + ": The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get ReturnTransaction
        ReturnTransactionImpl returnTransaction = getReturnTransactionById(returnId);
        // Check if the ReturnTransaction does not exist
        if (returnTransaction == null) {
            System.out.println(methodName + ": The ReturnTransaction does not exist");
            return -1;
        }
        // Check if the ReturnTransaction is not closed
        if (!returnTransaction.isClosed()) {
            System.err.println(methodName + ": The ReturnTransaction is not closed");
            return -1;
        }

        // Get the amount of money to be returned
        Double amount = returnTransaction.getAmount();

        // Create new BalanceOperation
        BalanceOperationImpl balanceOperation = newBalanceUpdate(-amount);
        if (balanceOperation == null) {
            System.err.println(methodName + ": There are some problems with the DB");
            return -1;
        }
        // Set BalanceOperation in ReturnTransaction
        returnTransaction.setBalanceOperation(balanceOperation);

        // Set payment type in ReturnTransaction
        returnTransaction.setPaymentType("CASH");

        // Set state in ReturnTransaction
        returnTransaction.setState("PAYED");

        // Write ReturnTransaction to persistence
        if (!updatePersistenceReturnTransaction(returnTransaction)) {
            System.err.println(methodName + ": There are some problems with the DB");
            return -1;
        }

        return amount;
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(returnId = "+ returnId +", creditCard = "+ creditCard +")");

        // Check if the returnId is null or less than or equal to 0
        if (returnId == null || returnId <= 0) {
            System.err.println(methodName + ": The returnId is null or less than or equal to 0");
            throw new InvalidTransactionIdException();
        }

        // Check if the creditCard is null, empty or invalid
        if ( !isValidCreditCard(creditCard)) {
            System.err.println(methodName + ": The creditCard is null, empty or invalid");
            throw new InvalidCreditCardException();
        }

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageSaleTransactions()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get ReturnTransaction
        ReturnTransactionImpl returnTransaction = getReturnTransactionById(returnId);
        // Check if the ReturnTransaction does not exist
        if (returnTransaction == null) {
            System.err.println(methodName + ": The ReturnTransaction does not exist");
            return -1;
        }
        // Check if the ReturnTransaction is not closed
        if (!returnTransaction.isClosed()) {
            System.err.println(methodName + ": The ReturnTransaction is not closed");
            return -1;
        }

        // Get the amount of money to be returned
        Double amount = returnTransaction.getAmount();

        // Get creditCard balance
        Double creditCardBalance = getCreditCardBalance(creditCard);
        // Check if the creditCard is not registered
        if (creditCardBalance == null) {
            return -1;
        }
        // Update the creditCard balance
        // if (!updateCreditCardBalance(creditCard, creditCardBalance + amount)) {
        //     return -1;
        // }

        // Create new BalanceOperation
        BalanceOperationImpl balanceOperation = newBalanceUpdate(-amount);
        if (balanceOperation == null) {
            System.err.println(methodName + ": There are some problems with the DB");
            return -1;
        }
        // Set BalanceOperation in ReturnTransaction
        returnTransaction.setBalanceOperation(balanceOperation);

        // Set payment type in ReturnTransaction
        returnTransaction.setPaymentType("CARD");

        // Set state in ReturnTransaction
        returnTransaction.setState("PAYED");

        // Write ReturnTransaction to persistence
        if (!updatePersistenceReturnTransaction(returnTransaction)) {
            System.err.println(methodName + ": There are some problems with the DB");
            return -1;
        }

        return amount;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(toBeAdded = "+ toBeAdded +")");

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageAccounting()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        // Get current balance
        Double currentBalance = getCurrentBalance();
        if (currentBalance == null) {
            System.err.println(methodName + ": There are some problems with the DB");
            return false;
        }
        // Check if toBeAdded + currentBalance < 0
        if (toBeAdded + currentBalance < 0) {
            System.err.println(methodName + ": toBeAdded + currentBalance < 0");
            return false;
        }

        BalanceOperationImpl balanceOperation = newBalanceUpdate(toBeAdded);
        if (balanceOperation == null) {
            System.err.println(methodName + ": There are some problems with the DB");
            return false;
        }

        return true;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(from = "+ from +", to = "+ to +")");

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageAccounting()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
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

        List<BalanceOperation> balanceOperations = getBalanceOperationByDate(from, to);
        if (balanceOperations == null) {
            System.err.println(methodName + ": There are some problems with the DB");
            return null;
        }

        return balanceOperations;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"()");

        // Check if there is no logged user or if it has not the rights to perform the operation
        if (loggedIn == null || !loggedIn.canManageAccounting()) {
            System.err.println(methodName + ": There is no logged user or if it has not the rights to perform the operation");
            throw new UnauthorizedException();
        }

        Double currentBalance = getCurrentBalance();
        if (currentBalance == null) {
            System.err.println(methodName + ": There are some problems with the DB");
            return 0;
        }

        return currentBalance;
    }




    //ADDED METHODS FOR FR 6 AND 7 PART


    //GET OBJECT FROM ID AND DB

    private CustomerImpl getCustomerById(int transactionCardId) {

        String getnewid = "SELECT * FROM CUSTOMERS WHERE CustomerId=?";
        CustomerImpl c=null;
        try (PreparedStatement pstmt  = this.conn.prepareStatement(getnewid)){

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

    private BalanceOperationImpl getBalanceOperationById(int balanceOperationId) {
        String query = "SELECT * FROM BALANCE_OPERATIONS WHERE BalanceId = ?";
        BalanceOperationImpl balanceOperation = null;
        try (PreparedStatement pstmt  = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, balanceOperationId);
            ResultSet rs = pstmt.executeQuery();

            if(rs.isBeforeFirst()) {
                LocalDate date = LocalDate.parse(rs.getString("Date"));
                double amount = rs.getDouble("Amount");
                String type = rs.getString("Type");
                balanceOperation = new BalanceOperationImpl(balanceOperationId, date, amount, type);
            }
        } catch (SQLException e) {
            System.err.println("getBalanceOperationById: " + e.getMessage());
            return null;
        }
        return balanceOperation;
    }

    private List<BalanceOperation> getBalanceOperationByDate(LocalDate from, LocalDate to) {
        String query = "SELECT BalanceId, Date, Amount, Type FROM BALANCE_OPERATIONS";
        List<BalanceOperation> balanceOperations = new ArrayList<>();
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                int balanceId = rs.getInt("BalanceId");
                LocalDate date = LocalDate.parse(rs.getString("Date"));
                double amount = rs.getDouble("Amount");
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
            System.err.println("getBalanceOperationByDate: " + e.getMessage());
            return null;
        }

        return balanceOperations;
    }

    private SaleTransactionImpl getSaleTransactionById(Integer transactionId) {
        if (currentSale != null && currentSale.getTicketNumber().equals(transactionId)){
            return currentSale;
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
                BalanceOperationImpl balanceOperation = getBalanceOperationById(rs.getInt("BalanceOperationId"));
                HashMap<String, TicketEntry> listOfProductsEntries = getProdListForSaleDB(transactionId);
                sale = new SaleTransactionImpl(transactionId, state, paymentType, amount, discountRate, customer, balanceOperation, listOfProductsEntries);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("getting sale id");
        return sale;
    }

    private ProductTypeImpl getProductTypeByCode(String barCode){

        //IF PRODUCT IS INVOLVED IN CURRENT SALE THE UP TO DATE INFORMATION ARE STORED ONLY IN RAM AT THE MOMENT
        TicketEntry prodl=null;
        ProductTypeImpl p=null;
        //Retrieving product
        String sql = "SELECT * FROM PRODUCTTYPES AS P WHERE P.BarCode=? ";

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
            if (currentSale != null) {
                prodl= currentSale.getListOfProductsEntries().get(barCode);
                if(prodl!=null){
                    p = new ProductTypeImpl(productId,barcode,description,sellPrice,quantity-prodl.getAmount(),prodDiscountRate,notes,aisleId,rackId,levelId);
                } else{
                    p = new ProductTypeImpl(productId, barcode, description, sellPrice, quantity, prodDiscountRate, notes, aisleId, rackId, levelId);
                }
            } else {
                p = new ProductTypeImpl(productId, barcode, description, sellPrice, quantity, prodDiscountRate, notes, aisleId, rackId, levelId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("Data for product with barcode " + barCode + " has been retrieved with success");

        return p;

    }

    //GET NEW ID

    private Integer getNewSaleTransactionId(){
        Integer tid=-1;

        String getnewid = "SELECT COALESCE(MAX(transactionId),0) FROM SALETRANSACTIONS";

        try (
                Statement stmt  = this.conn.createStatement();
                ResultSet rs    = stmt.executeQuery(getnewid)){

            tid=rs.getInt(1);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if(this.currentSale!=null){
         if(tid<currentSale.getTicketNumber()){
             tid=currentSale.getTicketNumber();
         }
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
      sale.getListOfProductsEntries().values().stream().forEach((el)-> {
       // map=sale.getListOfProductsSale();
       // for(HashMap.Entry<ProductTypeImpl, Integer> el : map.entrySet()){
      //  Iterator it=sale.getListOfProductsSale().entrySet().iterator();
       //         while(it.hasNext()){


            String sl = "INSERT INTO SALESANDPRODUCTS(transactionId,BarCode,description,Quantity, discountRate, pricePerUnit) VALUES(?,?,?,?,?,?)";

            try (
                    PreparedStatement pstmt = this.conn.prepareStatement(sl)) {

                // set the value of the parameter

                pstmt.setInt(1, tid);
                pstmt.setString(2, el.getBarCode());
                pstmt.setString(3,el.getProductDescription());
                pstmt.setInt(4, el.getAmount());
                pstmt.setDouble(5, el.getDiscountRate());
                pstmt.setDouble(6,el.getPricePerUnit());
                //
                pstmt.executeUpdate();


            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }


            //UPDATE INVENTORY
          String sp = "UPDATE PRODUCTTYPES SET Quantity=Quantity-? WHERE BarCode=?";

          try (
                  PreparedStatement pstmt = this.conn.prepareStatement(sp)) {

              // set the value of the parameter


              pstmt.setInt(1, el.getAmount());
              pstmt.setString(2, el.getBarCode());

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
          currentSale=null;
    }

    private BalanceOperationImpl newBalanceUpdate(Double amount) {
        LocalDate now = LocalDate.now();
        BalanceOperationImpl balanceOperation = new BalanceOperationImpl(-1, now, amount, (amount >= 0) ? "CREDIT" : "DEBIT");
        Integer newId = addBalanceToBalancesList(balanceOperation);
        if (newId != null) {
            balanceOperation.setBalanceId(newId);
        } else {
            return null;
        }

        return balanceOperation;
    }

    private Integer addBalanceToBalancesList(BalanceOperationImpl balanceOperation){
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(BalanceOperationImpl = "+ balanceOperation +")");

        String query = "INSERT INTO BALANCE_OPERATIONS(Date, Amount, Type) VALUES(?, ?, ?)";
        int rowCount;
        Integer newId = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            pstmt.setString(1, dtf.format(balanceOperation.getDate()));
            pstmt.setDouble(2, balanceOperation.getMoney());
            pstmt.setString(3, balanceOperation.getType());

            rowCount = pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                newId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return null;
        }
        System.out.println(methodName + ": inserted "+ rowCount +" rows with BalanceId = "+ newId +" in BALANCE_OPERATIONS");

        return newId;
    }


    //GET LIST OF PRODUCTS RELATED TO A SALE CAN BE USEFUL FOR RERURN TRANSACTION

    private HashMap<String, TicketEntry> getProdListForSaleDB(int tid) {
        String salesandproductssql = "SELECT BarCode, description, Quantity, discountRate, pricePerUnit FROM SALESANDPRODUCTS WHERE transactionId=?";
        HashMap< String, TicketEntry> map= new HashMap<>();
        try (PreparedStatement pstmt  = this.conn.prepareStatement(salesandproductssql)) {
            // set the value of the parameter
            pstmt.setInt(1,tid);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                TicketEntry ticket = new TicketEntryImpl(rs.getString("BarCode"),rs.getString("description"), rs.getInt("Quantity"),rs.getDouble("pricePerUnit"),rs.getDouble("discountRate"));

                map.put(ticket.getBarCode(), ticket);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return map;
    }

    public static boolean isValidCreditCard(String cardNumber) {

        if(cardNumber == null || cardNumber.isEmpty() || cardNumber==""){
            return false;
        }
        // int array for processing the cardNumber
        int[] cardIntArray=new int[cardNumber.length()];

        for (int i=0;i<cardNumber.length();i++) {
            char c= cardNumber.charAt(i);
            cardIntArray[i]=  Integer.parseInt(""+c);
        }

        for (int i=cardIntArray.length-2;i>=0;i=i-2) {
            int num = cardIntArray[i];
            num = num * 2;  // step 1
            if(num>9)
            {
                num = num%10 + num/10;  // step 2
            }
            cardIntArray[i]=num;
        }

        int sum = sumDigits(cardIntArray);  // step 3


        // step 4
        return (sum % 10) == 0;

    }

    public static int sumDigits(int[] arr) {
        return Arrays.stream(arr).sum();
    }

    private ReturnTransactionImpl getReturnTransactionById(Integer returnId) {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(returnId = "+ returnId +")");

        String query;

        query = "SELECT BarCode, Quantity FROM RETURN_PRODUCTS WHERE ReturnId = ?";
        Map<ProductTypeImpl, Integer> returnProducts = new HashMap<>();
        try (PreparedStatement pstmt  = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, returnId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String barCode = rs.getString("BarCode");
                Integer quantity = rs.getInt("Quantity");
                returnProducts.put(getProductTypeByCode(barCode), quantity);
            }
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return null;
        }

        query = "SELECT * FROM RETURN_TRANSACTIONS WHERE ReturnId = ?";
        ReturnTransactionImpl returnTransaction = null;
        try (PreparedStatement pstmt  = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, returnId);

            ResultSet rs = pstmt.executeQuery();

            if(rs.isBeforeFirst()) {
                SaleTransactionImpl saleTransaction = getSaleTransactionById(rs.getInt("TransactionId"));
                String state = rs.getString("State");
                Double amount = rs.getDouble("Amount");
                String paymentType = rs.getString("PaymentType");
                BalanceOperationImpl balanceOperation = getBalanceOperationById(rs.getInt("BalanceId"));
                returnTransaction = new ReturnTransactionImpl(returnId, saleTransaction, returnProducts, state, amount, paymentType, balanceOperation);
            }
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return null;
        }

        return returnTransaction;
    }

    private Integer insertPersistenceReturnTransaction(ReturnTransactionImpl returnTransaction) {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(ReturnTransactionImpl = "+ returnTransaction +")");

        String query;
        int rowCount;

        query = "INSERT INTO RETURN_TRANSACTIONS(TransactionId, State, Amount, PaymentType, BalanceId) VALUES(?, ?, ?, ?, ?)";
        Integer newId = null;
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, returnTransaction.getSaleTransaction().getTicketNumber());
            pstmt.setString(2, returnTransaction.getState());
            pstmt.setDouble(3, returnTransaction.getAmount());
            if (returnTransaction.getPaymentType() != null) {
                pstmt.setString(4, returnTransaction.getPaymentType());
            }
            if (returnTransaction.getBalanceOperation() != null) {
                pstmt.setInt(5, returnTransaction.getBalanceOperation().getBalanceId());
            }

            rowCount = pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                newId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return null;
        }
        if (newId == null) {
            return null;
        }
        System.out.println(methodName + ": inserted "+ rowCount +" rows with ReturnId = "+ newId +" in RETURN_TRANSACTIONS");

        if (returnTransaction.getReturnProducts() != null) {
            rowCount = 0;
            for (Map.Entry<ProductTypeImpl, Integer> entry : returnTransaction.getReturnProducts().entrySet()) {
                query = "INSERT INTO RETURN_PRODUCTS(ReturnId, BarCode, Quantity) VALUES(?, ?, ?)";
                try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
                    pstmt.setInt(1, newId);
                    pstmt.setString(2, entry.getKey().getBarCode());
                    pstmt.setInt(3, entry.getValue());

                    rowCount = pstmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println(methodName + ": " + e.getMessage());
                    return null;
                }
            }
            System.out.println(methodName + ": inserted "+ rowCount +" rows with ReturnId = "+ newId +" in RETURN_PRODUCTS");
        }

        return newId;
    }

    private boolean updatePersistenceReturnTransaction(ReturnTransactionImpl returnTransaction) {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(ReturnTransactionImpl = "+ returnTransaction +")");

        String query;
        int rowCount;

        query = "UPDATE RETURN_TRANSACTIONS SET TransactionId = ?, State = ?, Amount = ?, PaymentType = ?, BalanceId = ? WHERE ReturnId = ?";
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, returnTransaction.getSaleTransaction().getTicketNumber());
            pstmt.setString(2, returnTransaction.getState());
            pstmt.setDouble(3, returnTransaction.getAmount());
            if (returnTransaction.getPaymentType() != null) {
                pstmt.setString(4, returnTransaction.getPaymentType());
            }
            if (returnTransaction.getBalanceOperation() != null) {
                pstmt.setInt(5, returnTransaction.getBalanceOperation().getBalanceId());
            }
            pstmt.setInt(6, returnTransaction.getReturnId());

            rowCount = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return false;
        }
        System.out.println(methodName + ": updated "+ rowCount +" rows with ReturnId = "+ returnTransaction.getReturnId() +" in RETURN_TRANSACTIONS table");

        query = "DELETE FROM RETURN_PRODUCTS WHERE ReturnId = ?";
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, returnTransaction.getReturnId());

            rowCount = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return false;
        }
        System.out.println(methodName + ": deleted "+ rowCount +" rows with ReturnId = "+ returnTransaction.getReturnId() +" in RETURN_PRODUCTS table");

        if (returnTransaction.getReturnProducts() != null) {
            rowCount = 0;
            for (Map.Entry<ProductTypeImpl, Integer> entry : returnTransaction.getReturnProducts().entrySet()) {
                query = "INSERT INTO RETURN_PRODUCTS(ReturnId, BarCode, Quantity) VALUES(?, ?, ?)";
                try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
                    pstmt.setInt(1, returnTransaction.getReturnId());
                    pstmt.setString(2, entry.getKey().getBarCode());
                    pstmt.setInt(3, entry.getValue());

                    rowCount = pstmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println(methodName + ": " + e.getMessage());
                    return false;
                }
            }
            System.out.println(methodName + ": inserted "+ rowCount +" rows with ReturnId = "+ returnTransaction.getReturnId() +" in RETURN_PRODUCTS");
        }

        return true;
    }

    private boolean deletePersistenceReturnTransaction(ReturnTransactionImpl returnTransaction) {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(ReturnTransactionImpl = "+ returnTransaction +")");

        String query;
        int rowCount;

        query = "DELETE FROM RETURN_PRODUCTS WHERE ReturnId = ?";
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, returnTransaction.getReturnId());

            rowCount = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return false;
        }
        System.out.println(methodName + ": deleted "+ rowCount +" rows with ReturnId = "+ returnTransaction.getReturnId() +" in RETURN_PRODUCTS table");

        query = "DELETE FROM RETURN_TRANSACTIONS WHERE ReturnId = ?";
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, returnTransaction.getReturnId());

            rowCount = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return false;
        }
        System.out.println(methodName + ": deleted "+ rowCount +" rows with ReturnId = "+ returnTransaction.getReturnId() +" in RETURN_TRANSACTIONS table");

        return true;
    }

    private boolean updatePersistenceProductTypeQuantity(ProductTypeImpl productType) {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(ProductTypeImpl = "+ productType +")");

        String query;
        int rowCount;

        query = "UPDATE PRODUCTTYPES SET Quantity = ? WHERE BarCode = ?";
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, productType.getQuantity());
            pstmt.setString(2, productType.getBarCode());

            rowCount = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return false;
        }
        System.out.println(methodName + ": updated "+ rowCount +" rows with barCode = "+ productType.getBarCode() +" in PRODUCTTYPES table");

        return true;
    }

    private boolean updatePersistenceSaleTransactionQuantity(SaleTransactionImpl saleTransaction) {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(SaleTransactionImpl = "+ saleTransaction +")");

        String query;
        int rowCount;

        query = "UPDATE SALETRANSACTIONS SET Amount = ? WHERE transactionId = ?";
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            pstmt.setDouble(1, saleTransaction.getPrice());
            pstmt.setInt(2, saleTransaction.getTicketNumber());

            rowCount = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return false;
        }
        System.out.println(methodName + ": updated "+ rowCount +" rows with transactionId = "+ saleTransaction.getTicketNumber() +" in SALETRANSACTIONS table");

        query = "DELETE FROM SALESANDPRODUCTS WHERE transactionId = ?";
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            pstmt.setInt(1, saleTransaction.getTicketNumber());

            rowCount = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return false;
        }
        System.out.println(methodName + ": deleted "+ rowCount +" rows with transactionId = "+ saleTransaction.getTicketNumber() +" in SALESANDPRODUCTS table");

        if (saleTransaction.getListOfProductsEntries() != null) {
            rowCount = 0;
            for (TicketEntry ticketEntry : saleTransaction.getListOfProductsEntries().values()) {
                query = "INSERT INTO SALESANDPRODUCTS(transactionId, BarCode ,description, Quantity, discountRate, pricePerUnit) VALUES(?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
                    pstmt.setInt(1, saleTransaction.getTicketNumber());
                    pstmt.setString(2, ticketEntry.getBarCode());
                    pstmt.setString(3,ticketEntry.getProductDescription());
                    pstmt.setInt(4, ticketEntry.getAmount());
                    pstmt.setDouble(5, ticketEntry.getDiscountRate());
                    pstmt.setDouble(6, ticketEntry.getPricePerUnit());

                    rowCount = pstmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println(methodName + ": " + e.getMessage());
                    return false;
                }
            }
            System.out.println(methodName + ": inserted "+ rowCount +" rows with transactionId = "+ saleTransaction.getTicketNumber() +" in SALESANDPRODUCTS");
        }

        return true;
    }

    private Double getCurrentBalance() {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"()");

        String query = "SELECT SUM(Amount) AS CurrentBalance FROM BALANCE_OPERATIONS";
        Double currentBalance = null;
        try (PreparedStatement pstmt = this.conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            if(rs.isBeforeFirst()) {
                currentBalance = rs.getDouble("CurrentBalance");
                System.out.println(methodName + ": currentBalance = "+ currentBalance);
            }
        } catch (SQLException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return null;
        }

        return currentBalance;
    }

    private Double getCreditCardBalance(String creditCard) {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        System.out.println("Call "+ methodName +"(creditCard = "+ creditCard +")");

        Double balance = null;
        try (BufferedReader br = new BufferedReader(new FileReader("CreditCards.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] s = line.split(";");
                if (s[0].equals(creditCard)) {
                    balance = Double.parseDouble(s[1]);
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println(methodName + ": " + e.getMessage());
            return null;
        }

        return balance;
    }

//    private boolean updateCreditCardBalance(String creditCard, Double balance) {
//        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
//        System.out.println("Call "+ methodName +"(creditCard = "+ creditCard +", balance = "+ balance +")");
//
//        boolean find = false;
//        String newContent = "";
//        try (BufferedReader br = new BufferedReader(new FileReader("CreditCards.txt"))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                if (line.startsWith("#")) {
//                    newContent = newContent.concat(line + "\n");
//                    continue;
//                }
//                String[] s = line.split(";");
//                if (s[0].equals(creditCard)) {
//                    newContent = newContent.concat(s[0] + ";" + balance + "\n");
//                    find  = true;
//                } else {
//                    newContent = newContent.concat(line + "\n");
//                }
//            }
//        } catch (IOException e) {
//            System.err.println(methodName + ": " + e.getMessage());
//            return false;
//        }
//
//        if (find) {
//            try (FileWriter fw = new FileWriter("CreditCards.txt")) {
//                fw.write(newContent);
//            } catch (IOException e) {
//                System.err.println(methodName + ": " + e.getMessage());
//                return false;
//            }
//        }
//
//        return find;
//    }
}
