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
    getQuantity() : Integer
    setBarcode(String barcode): boolean
    setDescription(String description): boolean
    setSellPrice(Double sellPrice): boolean
    setQuantity(Integer quantity) : boolean
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

    -transactionCard: LoyaltyCard
    -saleOperationRecord: BalanceOperation
    -ListOfProductsSale: Map<ProductType, Integer>()

    +isProductInSale(String productCode): boolean
}


class ReturnTransaction {
    -returnId: Integer
    -state: enum State{FAILED, COMPLETED, DELETE}!
    -paymentType: enum PaymentType{CARD, CASH}
    -amount_tmp: Double!

    -originalTransaction: SaleTransaction
    -retOperationRecord: BalanceOperation
    -ListOfProductsReturn: Map<ProductType, Integer>()

    +addProduct(ProductType product, Integer quantity): boolean
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

    +getBalanceID(): Integer
    +getDescription(): enum Description{DEBIT, CREDIT}
    +getAmount(): Double
    +getDate(): LocalDate
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

## Scenario 8.1 & 10.1

```plantuml
actor "Administrator\nShop Manager\nCashier" as user

autonumber
user -> EZShop : startReturnTransaction()
activate  EZShop
EZShop -> EZShop : getLoggedIn()
note right: Is it necessary?
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : return canManageSaleTransactions()
EZShop -> EZShop : searchTransactionById()
note right: To check if the transaction exists
EZShop -> ReturnTransaction : ReturnTransaction()
note right: ReturnTransaction constructor
EZShop <-- ReturnTransaction : return ReturnTransaction()
user <-- EZShop : return startReturnTransaction()
deactivate EZShop

user -> EZShop : returnProduct()
activate EZShop
EZShop -> EZShop : searchProductByCode()
EZShop -> TransactionSale : isProductInSale()
EZShop <-- TransactionSale : return isProductInSale()
EZShop -> ReturnTransaction : addProduct()
EZShop <-- ReturnTransaction : return addProduct()
EZShop -> ProductType : updateQuantity()
EZShop <-- ProductType : return updateQuantity()
user <-- EZShop : return returnProduct()
deactivate EZShop

user -> EZShop : returnCreditCardPayment()
activate EZShop
EZShop -> EZShop : validateCreditCard()
note right: Luhn algorithm
EZShop -> ReturnTransaction : getAmount()
EZShop <-- ReturnTransaction : return getAmount()
user <-- EZShop
deactivate EZShop

user -> EZShop : endReturnTransaction()
activate EZShop
EZShop -> EZShop : recordBalanceUpdate()
user <-- EZShop
deactivate EZShop
```

## Scenario 8.2 & 10.2

```plantuml
actor "Administrator\nShop Manager\nCashier" as user

autonumber
user -> EZShop : startReturnTransaction()
activate  EZShop
EZShop -> EZShop : getLoggedIn()
note right: Is it necessary?
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : return canManageSaleTransactions()
EZShop -> EZShop : searchTransactionById()
note right: To check if the transaction exists
EZShop -> ReturnTransaction : ReturnTransaction()
note right: ReturnTransaction constructor
EZShop <-- ReturnTransaction : return ReturnTransaction()
user <-- EZShop : return startReturnTransaction()
deactivate EZShop

user -> EZShop : returnProduct()
activate EZShop
EZShop -> EZShop : searchProductByCode()
EZShop -> TransactionSale : isProductInSale()
EZShop <-- TransactionSale : return isProductInSale()
EZShop -> ReturnTransaction : addProduct()
EZShop <-- ReturnTransaction : return addProduct()
EZShop -> ProductType : updateQuantity()
EZShop <-- ProductType : return updateQuantity()
user <-- EZShop : return returnProduct()
deactivate EZShop

user -> EZShop : returnCashPayment()
activate EZShop
EZShop -> ReturnTransaction : getAmount()
EZShop <-- ReturnTransaction : return getAmount()
user <-- EZShop
deactivate EZShop

user -> EZShop : endReturnTransaction()
activate EZShop
EZShop -> EZShop : recordBalanceUpdate()
user <-- EZShop
deactivate EZShop
```

## Scenario 9.1

```plantuml
actor "Administrator\nShop Manager" as user

autonumber
user -> EZShop : getCreditsAndDebits()
activate EZShop
EZShop -> EZShop : getLoggedIn()
note right: Is it necessary?
EZShop -> User : canManageAccounting()
EZShop <-- User : return canManageAccounting()
EZShop -> EZShop : getBalanceOperationsList()
loop for-each BalanceOperation
    EZShop -> BalanceOperation : getDate()
    EZShop <-- BalanceOperation : return LocalDate
end
user <- EZShop : return getCreditsAndDebits()
deactivate EZShop
```
