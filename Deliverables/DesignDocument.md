# Design Document 


Authors: 

Date:

Version:


# Contents

- [High level design](#package-diagram)
- [Low level design](#class-diagram)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# Instructions

The design must satisfy the Official Requirements document, notably functional and non functional requirements

# High level design 

<discuss architectural styles used, if any>
<report package diagram>






# Low level design

<for each package, report class diagram>

<"!" used to signal inconsistency in function parameter>

<"In each class, methods or connections attributes have been separated with a space">

```plantuml
left to right direction


class EZShop{

    -customersList: List<Customer>
    -ordersList: List<Order>
    -salesList: List<SaleTransaction>
    -returnsList: List<ReturnTransaction>
    -productsList: List<ProductType>
    -balanceOperationsList: List<BalanceOperation>
    -loggedIn: User

    +void reset();

    +List<User> getAllUsers()
    +User login(String username, String password)
    +boolean logout();
    +Integer createUser(String username, String password, String role)
    +boolean deleteUser(Integer id)
    +User getUser(Integer id)
    +boolean updateUserRights(Integer id, String role)

    +List<ProductType> getAllProductTypes()
    +List<ProductType> getProductTypesByDescription(String description)
    +Integer createProductType(String description, String productCode, double pricePerUnit, String note)
    +boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote)
    +boolean deleteProductType(Integer id)
    +ProductType getProductTypeByBarCode(String barCode)
    +boolean updateQuantity(Integer productId, int toBeAdded)
    +boolean updatePosition(Integer productId, String newPos)
    
    +List<Order> getAllOrders()
    +Integer issueReorder(String productCode, int quantity, double pricePerUnit)!
    +Integer payOrderFor(String productCode, int quantity, double pricePerUnit)!
    +boolean payOrder(Integer orderId)
    +boolean recordOrderArrival(Integer orderId)

    +List<Customer> getAllCustomers()
    +Integer defineCustomer(String customerName)
    +boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard)
    +boolean deleteCustomer(Integer id)
    +Customer getCustomer(Integer id)
    +String createCard();
    +boolean attachCardToCustomer(String customerCard, Integer customerId)
    +boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded)

    +Integer startSaleTransaction();
    +boolean addProductToSale(Integer transactionId, String productCode, int amount)!
    +boolean deleteProductFromSale(Integer transactionId, String productCode, int amount)!
    +boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate)!
    +boolean applyDiscountRateToSale(Integer transactionId, double discountRate)
    +int computePointsForSale(Integer transactionId)
    +boolean closeSaleTransaction(Integer transactionId)
    +boolean deleteSaleTicket(Integer ticketNumber)!
    +Ticket getSaleTicket(Integer transactionId)!

    +Integer startReturnTransaction(Integer ticketNumber)!
    +boolean returnProduct(Integer returnId, String productCode, int amount)
    +boolean endReturnTransaction(Integer returnId, boolean commit)
    +boolean deleteReturnTransaction(Integer returnId)

    +double receiveCashPayment(Integer ticketNumber, double cash)  
    +boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard)
    +double returnCashPayment(Integer returnId)
    +double returnCreditCardPayment(Integer returnId, String creditCard)

    +List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to)
    +boolean recordBalanceUpdate(double toBeAdded)
    +double computeBalance();
}


class User{
    -role: String
    -username: String
    -password: String

    +getUsername(): String
    +getPassword(): String
    +getRole(): String
    +setRole(String role): boolean
    +canManageUsers(): boolean
    +canEditProducts(): boolean
    +canListProducts(): boolean
    +canManageInventory(): boolean
    +canManageSaleTransactions(): boolean
    +canManagePayments(): boolean
    +canManageAccounting(): boolean

}


class ProductType{
    -productId: Integer
    -barcode: String
    -description: String
    -sellPrice: Double
    -quantity: Integer
    -productDiscountRate: Double
    -notes: String
    -AisleID: Integer
    -RackID: String
    -LevelID: Integer

    isValidBarcode(String barcode): static boolean
    getProductID(): Integer
    getBarcode(): String
    getDescription(): String
    setBarcode(String barcode): boolean
    setDescription(String description): boolean
    setSellPrice(Double sellPrice): boolean
    setNotes(String notes): boolean
}


class Order {
    -orderId: Integer
    -pricePerUnit: Double
    -productCode: String
    -quantity: Integer
    -status: enum Status{PAYED, ISSUED, ORDERED, COMPLETED}

    -orderOperationRecord: BalanceOperation
    -product: ProductType
}


class Customer{
    -customerName: String
    -customerId: Integer

    -customerCard: LoyaltyCard
}


class SaleTransaction {
    -transactionId: Integer
    -state: enum State{FAILED, COMPLETED, DELETE}!
    -paymentType: enum PaymentType{CARD, CASH}
    -amount_tmp: Double!
    -salediscountRate: Double

    -ListOfProductsSale: Map<ProductType, Integer>()
    -transactionCard: LoyaltyCard
    -saleOperationRecord: BalanceOperation
}


class ReturnTransaction {
    -amount_tmp: Double!
    -returnId: Integer

    -originalTransaction: SaleTransaction
    -retOperationRecord: BalanceOperation
    -ListOfProductsReturn: Map<ProductType, Integer>()
}


class LoyaltyCard{
    -cardId: String
    -cardPoints: Integer
}


class BalanceOperation {
    -balanceID: Integer
    -description: enum Description{DEBIT, CREDIT}
    -amount: Double
    -date: LocalDate 
}


EZShop ->"*" Order
EZShop ->"*" Customer
EZShop ->"*" SaleTransaction
EZShop ->"*" ReturnTransaction
EZShop ->"*" ProductType
EZShop ->"*" User
EZShop ->"*" BalanceOperation


Order "*"--> ProductType
Customer -->"0..1" LoyaltyCard


SaleTransaction "1...*" --> "0..1" LoyaltyCard
SaleTransaction "*" --> "*" ProductType
ReturnTransaction "0...*" --> "1" SaleTransaction
ReturnTransaction "0...*" --> "1...*" ProductType


SaleTransaction --> BalanceOperation
ReturnTransaction --> BalanceOperation
Order --> BalanceOperation


```




# Verification traceability matrix

\<for each functional requirement from the requirement document, list which classes concur to implement it>











# Verification sequence diagrams 
\<select key scenarios from the requirement document. For each of them define a sequence diagram showing that the scenario can be implemented by the classes and methods in the design>

