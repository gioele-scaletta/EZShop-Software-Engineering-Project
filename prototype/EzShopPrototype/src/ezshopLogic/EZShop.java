package ezshopLogic;

import javax.print.attribute.standard.JobKOctets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.Exception;

public class EZShop /*implements EZShopInterface*/{

    private List<Customer> customers;
    private List<Order> orders;
    private List<SaleTransaction> salesList;
    private List<ReturnTransaction> returnsList;
    private List<BalanceOperation> balanceOperationsList;
    private List<User> usersList;
    private List<ProductType> productsList;

    private List<LoyaltyCard> cardsList;

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
    
    
    
    //ADDED METHODS
    public Integer getNewSaleTransactionId(){
        return salesList.stream().map(e -> e.getTransactionId()).max(Integer::compare).get()+1;
    }
    
    public void addSaleToSalesList(SaleTransaction sale){
        salesList.add(sale);
    }
    
    public void RemoveSaleFromSalesList(SaleTransaction sale) {
   	 salesList.remove(sale);
   	}
    
    SaleTransaction getSaleTransactionById(Integer transactionId) /*throw*/{

        for (SaleTransaction e : salesList ){
            if(e.getTransactionId()==transactionId){
                return e;
            }
        }
        return null;
       // throw ...
    }

    ProductType getProductTypeByCode(String productCode){
        for (ProductType p : productsList) {
            if (productCode.equals(p.getBarcode())){
                return p;
            }
        }
        return null;
       // throw ...
    }
    
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

    public void newBalanceUpdate(Double amount){
        //tobeimplemented
    	
    }

    public void addBalanceToBalancesList(){
        //tobimplemented
    }

    public boolean attachCardToSale(LoyaltyCard customerCard){	
    	return true;
    }

    LoyaltyCard getCardById(String customerCard) /*throw*/{

        for (LoyaltyCard e : cardsList ){
            if(e.getCardId()==customerCard){
                return e;
            }
        }
        return null;
       // throw ...
    }



    //INTERFACE METHODS
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

        Integer newtransactionId=getNewSaleTransactionId();
        SaleTransaction sale= new SaleTransaction(newtransactionId);
        addSaleToSalesList(sale);
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
        
        return sale.AddUpdateDeleteProductInSale(getProductTypeByCode(productCode), amount);
        
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


        //MISSING handling exceptions
        
        SaleTransaction sale = getSaleTransactionById(transactionId);
        
        return sale.AddUpdateDeleteProductInSale(getProductTypeByCode(productCode), -amount);
        

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
        //MISSING handle invalid productCode
        //MISSING handle invalid discountRate
        
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
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) /*throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException*/{

        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            //throw new UnauthorizedException();
            return false;
        }

        SaleTransaction sale = getSaleTransactionById(transactionId);
        
        //MISSING handle invalid transactionId
        //MISSING handle invalid productCode
        //MISSING handle invalid discountRate

        return sale.ApplyDiscountToSaleAll(discountRate);//if discount rate out of bound returns false
     
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

        SaleTransaction sale = getSaleTransactionById(transactionId);
        //MISSING handle invalid transactionId (ret -1)

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
    public boolean endSaleTransaction(Integer transactionId) /*throws InvalidTransactionIdException, UnauthorizedException*/{
        //MISSING Connection with balanceoperation and ticket since I havenm't understood how to handel ticket yet (also next three methods still todo)

        if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            //throw new UnauthorizedException();
            return false;
        }

        SaleTransaction sale = getSaleTransactionById(transactionId);
        //MISSING handle invalid transactionId
       
        return sale.EndSaleUpdateProductQuantity();




    }

    /**
     * This method deletes a sale transaction with given unique identifier from the system's data store.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the number of the transaction to be deleted
     *
     * @return  true if the transaction has been successfully deleted,
     *          false   if the transaction doesn't exist,
     *                  if it has been payed,
     *                  if there are some problems with the db
     *
     * @throws InvalidTransactionIdException if the transaction id number is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    public boolean deleteSaleTransaction(Integer transactionId) /*throws InvalidTransactionIdException, UnauthorizedException*/{
    	if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            //throw new UnauthorizedException();
            return false;
        }
    	

        SaleTransaction sale = getSaleTransactionById(transactionId);
    	sale.AbortSaleUpdateProductQuantity();
    	RemoveSaleFromSalesList(sale);
    	 return true; //exceptions manca
   
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
    public SaleTransaction getSaleTransaction(Integer transactionId) /*throws InvalidTransactionIdException, UnauthorizedException*/{
    
    	if(!loggedIn.canManageSaleTransactions()) {  //need to check SALE authorization part is just an idea
            System.out.println("User " + loggedIn.getUsername() + " User not authorized");
            //throw new UnauthorizedException();
            return null;
        }
    	
    	return getSaleTransactionById(transactionId);
    	//exceptions manca
    }

    //LAST METHODS RELATED TO RETURN TRANSACTION MISSING!!


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
     * @throws InvalidPaymentException if the cash is less than or equal to 0
     */
    public double receiveCashPayment(Integer transactionId, double cash) /*throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException;*/{
    	
    	SaleTransaction sale=getSaleTransactionById(transactionId);

    	Double change=sale.PaySaleAndReturnChange(cash, true);
            newBalanceUpdate(cash);
            return change;
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
    public boolean receiveCreditCardPayment(Integer transactionId, String creditCard) /* throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException;*/{
    	
    		
    		//Check enogh money on card
    		//store card
    	SaleTransaction sale=getSaleTransactionById(transactionId);
    
    	if(isValidCreditCard(creditCard)) {
    	sale.PaySaleAndReturnChange(sale.getCurrentAmount(), false);
        newBalanceUpdate(sale.getCurrentAmount());
    	return true;
    
    	}
        sale.AbortSaleUpdateProductQuantity();
    	RemoveSaleFromSalesList(sale);
    	return false;
    }

  //METHODS FOR RETURN PAYMENT MISSING
    }