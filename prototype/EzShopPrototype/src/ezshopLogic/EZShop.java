package ezshopLogic;

import javax.print.attribute.standard.JobKOctets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.Exception;

public class EZShop {

    private List<Customer> customers;
    private List<Order> orders;
    private List<SaleTransaction> salesList;
    private List<ReturnTransaction> returnsList;
    private List<BalanceOperation> balanceOperationsList;
    private List<User> usersList;
    private List<ProductType> productsList;

    private User loggedIn;
    private Integer latestUserID;
    private Integer latestProductTypeID;

    public EZShop(){
        this.customers = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.returnsList = new ArrayList<>();
        this.balanceOperationsList = new ArrayList<>();
        this.usersList = new ArrayList<>();
        this.productsList = new ArrayList<>();
        this.loggedIn = null;
        this.latestUserID = 1;
        this.latestProductTypeID = 1;
    }


    public Integer createUser(String username, String password, String role) throws Exception /*throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException*/ {
        if(username.isBlank() || username == null) {
            System.out.println("Invalid username");
            //throw new InvalidUsernameException();
            return -1;
        }
        if(password.isBlank() || password == null) {
            System.out.println("Invalid password");
            //throw new InvalidPasswordException;
            return -1;
        }
        for (User u : usersList) {
            if (username.equals(u.getUsername())) {
                System.out.println("An user with username " + username + " does already exists");
                return -1;
            }
        }
        usersList.add(new User(latestUserID, username, password, role));
        return latestUserID++;
    }

    public boolean deleteUser(Integer id) /*throws InvalidUserIdException, UnauthorizedException*/ {
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
        if(loggedIn != null)
        {
            System.out.println("There's already a user logged in");
            return null;
        }
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

    public Integer createProductType(String description, String productCode, double pricePerUnit, String note)
        /*throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException*/ {
        if(!loggedIn.canManageProductList()) {
            System.out.println("User " + loggedIn.getUsername() + " has no permission to manage product list");
            //throw new UnauthorizedException();
            return -1;
        }
        if(description.isBlank() || description == null) {
            System.out.println("Invalid product description");
            //throw new InvalidProductDescriptionException();
            return -1;
        }
        if(ProductType.isValidBarcode(productCode)) {
            System.out.println("Invalid product barcode");
            //throw new InvalidProductCodeException;
            return -1;
        }
        if(pricePerUnit <= 0.0) {
            System.out.println("Invalid price per unit");
            //throw ner InvalidPricePerUnitException
            return -1;
        }
        for (ProductType p : productsList) {
            if (productCode.equals(p.getBarcode())){
                System.out.println("There's already a product with barcode: " + productCode);
                return -1;
            }
        }
        productsList.add(new ProductType(latestProductTypeID, productCode, description, pricePerUnit, note));
        return latestProductTypeID++;
    }

    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote)
            /*throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException*/{
        if(!loggedIn.canManageProductList()) {
            System.out.println("User " + loggedIn.getUsername() + " has no permission to manage product list");
            //throw new UnauthorizedException();
            return false;
        }
        if(id<=0 || id == null) {
            System.out.println("Invalid product ID");
            //throw new InvalidProductIdException();
            return false;
        }
        if(newDescription.isBlank() || newDescription == null) {
            System.out.println("Invalid product description");
            //throw new InvalidProductDescriptionException();
            return false;
        }
        if(ProductType.isValidBarcode(newCode)) {
            System.out.println("Invalid product barcode");
            //throw new InvalidProductCodeException;
            return false;
        }
        if(newPrice <= 0.0) {
            System.out.println("Invalid price per unit");
            //throw ner InvalidPricePerUnitException
            return false;
        }

        for(ProductType p:productsList) {
            if(p.getBarcode() == newCode) {
                System.out.println("There's already a product with barcode " + newCode);
                return false;
            }
        }

        for (ProductType p : productsList) {
            if(p.getProductID() == id) {
                p.setBarcode(newCode);
                p.setDescription(newDescription);
                p.setSellPrice(newPrice);
                p.setNotes(newNote);
                return true;
            }
        }
        System.out.println("No product with ID " + id + " has been found");
        return false;
    }

    public boolean deleteProductType(Integer id) /*throws InvalidProductIdException, UnauthorizedException*/ {
        if(!loggedIn.canManageProductList()) {
            System.out.println("User " + loggedIn.getUsername() + " has no permission to delete products from the products list");
            //throw new UnauthorizedException();
            return false;
        }
        if(id<=0 || id == null) {
            System.out.println("Invalid product ID");
            //throw new InvalidProductIdException();
            return false;
        }

        for (ProductType p : productsList) {
            if(p.getProductID() == id) {
                productsList.remove(p);
                return true;
            }
        }
        System.out.println("No product with ID " + id + " has been found");
        return false;
    }

    public List<ProductType> getAllProductTypes() /*throws UnauthorizedException*/ {
        if(!loggedIn.canListProducts()) {
            System.out.println("User " + loggedIn.getUsername() + " has no permission to retrieve the products list");
            //throw new UnauthorizedException();
            return null;
        }
        return productsList;
    }

    public ProductType getProductTypeByBarCode(String barCode) /*throws InvalidProductCodeException, UnauthorizedException*/ {
        if(!loggedIn.canManageProductList()) {
            System.out.println("User " + loggedIn.getUsername() + " has no permission to retrieve the products list");
            //throw new UnauthorizedException();
            return null;
        }
        if(ProductType.isValidBarcode(barCode)) {
            System.out.println("Invalid product barcode");
            //throw new InvalidProductCodeException;
            return null;
        }
        for (ProductType p : productsList) {
            if (barCode.equals(p.getBarcode())){
                return p;
            }
        }
        System.out.println("There's no product with barcode " + barCode);
        return null;
    }

    public List<ProductType> getProductTypesByDescription(String description) /*throws UnauthorizedException*/{
        if(!loggedIn.canManageProductList()) {
            System.out.println("User " + loggedIn.getUsername() + " has no permission to retrieve the products list");
            //throw new UnauthorizedException();
            return null;
        }
        String descriptionToSearch;
        List<ProductType> matches = new ArrayList<>();

        if(description==null)
            descriptionToSearch = "";
        else
            descriptionToSearch = description;

        for(ProductType p:productsList) {
            if(p.getDescription().equals(descriptionToSearch))
                matches.add(p);
        }
        if(matches.size() == 0) {
            System.out.println("There's no product type that matches the description");
            return null;
        }
        return matches;
    }

    // -------------------- FR6 ------------------- //
    // ------------------- ADMIN ------------------ //
    // --------------- SHOP MANAGER --------------- //
    // ------------------ CASHIER ----------------- //


    /**
     * This method starts a new sale transaction and returns its unique identifier.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the id of the transaction (greater than or equal to 0)
     */
    public Integer startSaleTransaction() /*throws UnauthorizedException*/ {
        if(!loggedIn.canManageSaleTransactions()) {
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            //throw new UnauthorizedException();
            return -1;
        }
        Double current_amount = 0.0;
        Integer newtransactionId= salesList.stream().map(e -> e.getTransactionId()).max(Integer::compare).get()+1;   // I think we could substitue lists with maps
        SaleTransaction sale= new SaleTransaction(newtransactionId, current_amount);
        salesList.add(sale);
        return newtransactionId;
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
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) /*throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException*/{

        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            //throw new UnauthorizedException();
            return false;
        }

        SaleTransaction sale = getSaleTransactionById(transactionId);
        //MISSING handle invalid transactionId
        HashMap<ProductType, Integer> listofproducts = sale.getListOfProductsSale();
        //MISSING handle invalid productCode
        //MISSING handle invalid amount

        if(listofproducts.containsKey(getProductTypeByBarCode(productCode))){
            listofproducts.put(getProductTypeByBarCode(productCode), listofproducts.get(getProductTypeByBarCode(productCode))+ amount);
            sale.setCurrentAmount(sale.getCurrentAmount()+amount*getProductTypeByBarCode(productCode).getSellPrice());
            return true;
        } else{
            listofproducts.put(getProductTypeByBarCode(productCode), amount);
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
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) /*throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException*/{

        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            //throw new UnauthorizedException();
            return false;
        }

        SaleTransaction sale = getSaleTransactionById(transactionId);
        //MISSING handle invalid transactionId
        HashMap<ProductType, Integer> listofproducts=sale.getListOfProductsSale();
        //MISSING handle invalid productCode

        if(listofproducts.containsKey(getProductTypeByBarCode(productCode))){
            if(listofproducts.get(getProductTypeByBarCode(productCode))<amount){
                listofproducts.put(getProductTypeByBarCode(productCode), listofproducts.get(getProductTypeByBarCode(productCode))- amount);
                sale.setCurrentAmount(sale.getCurrentAmount()-amount*getProductTypeByBarCode(productCode).getSellPrice());
                return true;
            } else if(listofproducts.get(getProductTypeByBarCode(productCode))==amount) {
                sale.setCurrentAmount(sale.getCurrentAmount()-amount*getProductTypeByBarCode(productCode).getSellPrice());
                listofproducts.remove(getProductTypeByBarCode(productCode));
                return true;
            } else{
                //throw InvalidQuantityException;
                return false;
            }
        }

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
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) /*throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException*/{

        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            //throw new UnauthorizedException();
            return false;
        }

        SaleTransaction sale = getSaleTransactionById(transactionId);
        //MISSING handle invalid transactionId
        HashMap<ProductType, Integer> listofproducts=sale.getListOfProductsSale();
        //MISSING handle invalid productCode
        //MISSING handle invalid discountRate

        if(listofproducts.containsKey(getProductTypeByBarCode(productCode))){
            sale.setCurrentAmount(sale.getCurrentAmount()-discountRate*listofproducts.get(getProductTypeByBarCode(productCode))*getProductTypeByBarCode(productCode).getSellPrice());
            return true;
        }

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
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) /*throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException*/{

        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            //throw new UnauthorizedException();
            return false;
        }

        SaleTransaction sale = getSaleTransactionById(transactionId);
        //MISSING handle invalid transactionId
        HashMap<ProductType, Integer> listofproducts=sale.getListOfProductsSale();
        //MISSING handle invalid productCode
        //MISSING handle invalid discountRate

 
            sale.setCurrentAmount(sale.getCurrentAmount()*(1-discountRate));
            return true;
     
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
    public int computePointsForSale(Integer transactionId) /*throws InvalidTransactionIdException, UnauthorizedException*/{

        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            //throw new UnauthorizedException();
         
        }

        Integer points=-1;

        SaleTransaction sale = getSaleTransactionById(transactionId);
        //MISSING handle invalid transactionId

        points=(int) ((sale.getCurrentAmount()-5)/10);

    }


    /**
     * This method makes the transaction's ticket available by closing an opened transaction. After this operation the
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
    public boolean closeSaleTransaction(Integer transactionId) /*throws InvalidTransactionIdException, UnauthorizedException*/{
        //MISSING Connection with balanceoperation and ticket since I havenm't understood how to handel ticket yet (also next three methods still todo)

        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            //throw new UnauthorizedException();
            return false;
        }

        SaleTransaction sale = getSaleTransactionById(transactionId);
        //MISSING handle invalid transactionId
        HashMap<ProductType, Integer> listofproducts=sale.getListOfProductsSale();

        listofproducts.entrySet().stream().forEach(e->updateQuantity(e.getKey().getProductID(),e.getValue()));
        return true;


    }

    /**
     * This method deletes a sale ticket with given unique identifier from the system's data store.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param ticketNumber the number of the ticket to be deleted
     *
     * @return  true if the ticket has been successfully deleted,
     *          false   if the ticket doesn't exist,
     *                  if it has been payed,
     *                  if there are some problems with the db
     *
     * @throws InvalidTicketNumberException if the ticket number is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    //!!!!!!public boolean deleteSaleTicket(Integer ticketNumber) throws InvalidTicketNumberException, UnauthorizedException;

    /**
     * This method returns the sale ticket related to a closed sale transaction.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the CLOSED Sale transaction
     *
     * @return the ticket of the transaction if it is available (transaction closed), null otherwise
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    //!!!!!!public Ticket getSaleTicket(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException;

    /**
     * This method returns a sale ticket having given ticket number.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param ticketNumber the ticket number
     *
     * @return the ticket it is available, null otherwise
     *
     * @throws InvalidTicketNumberException if the ticket number is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    //!!!!!!!public Ticket getTicketByNumber(Integer ticketNumber) throws InvalidTicketNumberException, UnauthorizedException;
    

    //LAST METHODS RELATED TO RETURN TRANSACTION MISSING!!

    // -------------------- FR7 ------------------- //
    // ------------------- ADMIN ------------------ //
    // --------------- SHOP MANAGER --------------- //
    // ------------------ CASHIER ----------------- //

    /**
     * This method record the payment of a ticket with cash and returns the change (if present).
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param ticketNumber the number of the ticket that the customer wants to pay
     * @param cash the cash received by the cashier
     *
     * @return the change (cash - ticket price)
     *         -1   if the ticket does not exists,
     *              if the cash is not enough,
     *              if there is some problemi with the db
     *
     * @throws InvalidTicketNumberException if the ticket number is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     * @throws InvalidPaymentException if the cash is less than or equal to 0
     */
    //!!!!!!public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTicketNumberException, InvalidPaymentException, UnauthorizedException;

    /**
     * This method record the payment of a ticket with credit card. If the card has not enough money the payment should
     * be refused.
     * The credit card number validity should be checked. It should follow the luhn algorithm.
     * The credit card should be registered in the system.
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param ticketNumber the number of the ticket that the customer wants to pay
     * @param creditCard the credit card number of the customer
     *
     * @return  true if the operation is successful
     *          false   if the ticket does not exists,
     *                  if the card has not enough money,
     *                  if the card is not registered,
     *                  if there is some problem with the db connection
     *
     * @throws InvalidTicketNumberException if the ticket number is less than or equal to 0 or if it is null
     * @throws InvalidCreditCardException if the credit card number is empty, null or if luhn algorithm does not
     *                                      validate the credit card
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    //!!!!public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTicketNumberException, InvalidCreditCardException, UnauthorizedException;

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
    //!!!!!public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException;

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
    //!!!!!public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException;
    

}
