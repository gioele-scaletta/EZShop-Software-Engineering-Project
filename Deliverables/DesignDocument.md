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

<
The application implements a 3-tiers architecture, where the data layes, the logic layer and the presentation layer are in different packages. The GUI interacts with the EZShop logic layer via the Façade class "EZShop".

```plantuml
package "GUI"
package "EZShopLogic"
package "Data"
package "EZShopException"

EZShopLogic <-> GUI
EZShopException --> EZShopLogic
Data <-> EZShopLogic

```

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
    -usersList: List<User>
    -loggedIn: User


    +Integer getNewSaleTransactionId();
    +void addSaleToSalesList(SaleTransaction sale)
    +void RemoveSaleFromSalesList(SaleTransaction sale)
    +SaleTransaction getSaleTransactionById(Integer transactionId)
    +ProductType getProductTypeByCode(String productCode)
    +boolean isValidCreditCard(String cardNumber)
    +int sumDigits(int[] arr)
    +getCardById()
    +void addBalanceToBalancesList()
    +newBalanceUpdate(Double amount)


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
    updateProductQuantity(Integer quantitytorem): void
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
    -state: enum State{PAYED, CLOSED, INPROGRESS}
    -pay: enum PaymentType{CARD, CASH}
    -currentamount: Double
    -salediscountRate: Double

    -transactionCard: LoyaltyCard
    -saleOperationRecord: BalanceOperation
    -listOfProductsSale: Map<ProductType, Integer>

    +AddUpdateDeleteProductInSale(ProductType product, Integer amount): boolean
    +isProductInSale(ProductType product): boolean
    +ApplyDiscountToSaleProduct(Double disc, ProductType product): boolean
    +ApplyDiscountToSaleAll(Double disc): boolean
    +PointsForSale(): Integer
    +EndSaleUpdateProductQuantity(): boolean
    +AbortSaleUpdateProductQuantity(): void
    +PaySaleAndReturnChange(Double amount, Boolean method): Double
    +attachCardToSale(String customerCard)

}


class ReturnTransaction {
    -returnId: Integer
    -state: enum State{FAILED, COMPLETED, DELETE}!
    -paymentType: enum PaymentType{CARD, CASH}
    -currentamount: Double

    -saleTransaction: SaleTransaction
    -returnOperationRecord: BalanceOperation
    -listOfProductsReturn: Map<ProductType, Integer>

    +addProductToReturn(ProductType product, Integer quantity): boolean
}


class LoyaltyCard{
    -cardId: String
    -cardPoints: Integer

    +updatePoints(Integer points)
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

note as N
The classes is stored persistently in the data layer.\n
Here we decided to model explicitely the relationships with lists and maps.\n
end note
```



# Verification traceability matrix



|FR ID|EZShop|Customer|LoyaltyCard|SaleTransaction|BalanceOperation|Order|ProductType|User|ReturnTransaction|
|-------------| :-------------: | :-------------: | :-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|
| FR1 | X |   |   |   |   |   |   | X |   |
| FR3 | X |   |   |   |   |   | X |   |   |
| FR4 | X |   |   |   |   | X | X |   |   |
| FR5 | X | X | X |   |   |   |   |   |   |
| FR6 | X |   | X | X | X |   | X | X | X |
| FR7 | X |   |   | X | X |   |   | X | X |
| FR8 | X |   |   |   | X |   |   | X |   |

# Verification sequence diagrams 
\<select key scenarios from the requirement document. For each of them define a sequence diagram showing that the scenario can be implemented by the classes and methods in the design>

### Use Case 1

##### Scenario 1.1 - Create Product Type X

```plantuml
participant GUI as 1
participant EZShop as 2
participant User as 3
participant ProductType as 4

1->2 : createProductType()
2->3 : canEditProducts()
2->4 : new ProdutctType()
2->2 : addProductToInventory()
```

##### Scenario 1.3 - Modify Product Type pricePerUnit

```plantuml
participant GUI as 1
participant EZShop as 2
participant User as 3
participant ProductType as 4

1->2 : updateProduct()
2->3 : canEditProducts()
2->2 : getProductTypeByID()
2->4 : setBarcode()
2->4 : setDescription()
2->4 : setSellPrice()
2->4 : setNotes()
```
Use Case 2>

<Scenario 2.1 - Create user and define rights>

```plantuml
participant GUI as 1
participant EZShop as 2
participant User as 3

1->2 : createUser()
2->3 : new User()
2->2 : addUserToUsersList()
```

<Scenario 2.3 - Create user and define rights>

```plantuml
participant GUI as 1
participant EZShop as 2
participant User as 3

1->2 : updateUserRights()
2->3 : canManageUsers()
2->2 : getUser()
2->3 : setRole()
```

<Use Case 3>

<Scenario 3.1 - Order od Product Type Issued>

```plantuml
participant GUI as 1
participant EZShop as 2
participant User as 3
participant Order as 4

1->2 : issueReorder()
2->3 : canManageInventory()
2->2 : getProductTypeByBarCode
2->4 : new Order()
2->2 : addOrderToOrdersList()
```

<Scenario 3.3 - Record order of ProductType arrival>

```plantuml
participant GUI as 1
participant EZShop as 2
participant User as 3
participant Order as 4
participant ProductType as 5

1->2 : recordOrderArrival()
2->3 : canManageInventory()
2->2 : getOrderByID()
2->4 : setOrderState()
2->2 : getProductTypeByID()
2->5 : setBarcode()
2->5 : setDescription()
2->5 : setSellPrice()
2->5 : setNotes()
```

<Use Case 4>

<Scenario 4.2: Attach card to customer record>

```plantuml
EZShop -> EZShop : createCard()
EZShop ->Customer : getCustomer()
Customer -> Customer : getCustomerId()
Customer --> EZShop : customerId
EZShop -> EZShop : attachCardToCustomer()
```

<Scenario 4.2: Detach card from customer record>

```plantuml
EZShop ->Customer : getCustomer()
Customer -> Customer : getCustomerId()
Customer --> EZShop : customerId
Customer -> Customer : getCustomerName()
Customer --> EZShop : customerName
EZShop -> EZShop : modifyCustomer()
```

<Use Case 6 & 7>

<Scenarios 6.1, 6.2, 6.3, 6.4, 6.6, 7.1, (7.4): Full Sale with product discount, sale discounts and loyalty card update>

```plantuml
actor "Administrator\nShop Manager\nCashier" as user

autonumber

user -> EZShop : startReturnTransaction()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : getNewSaleTransactionId()
EZShop -> SaleTransaction : new SaleTransaction()
EZShop <-- SaleTransaction : SaleTransaction
EZShop -> EZShop : addSaleToSalesList()
user <-- EZShop : Integer
deactivate EZShop

user -> EZShop : addProductToSale()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop-> EZShop : getProductTypeByCode()
EZShop -> SaleTransaction : AddUpdateDeleteProductInSale()
SaleTransaction -> SaleTransaction : isProductInSale()
SaleTransaction -> ProductType : getSellPrice()
SaleTransaction <-- ProductType : Double
EZShop <-- SaleTransaction : Boolean
user <-- EZShop : Boolean
deactivate EZShop

user -> EZShop : applyDiscountRateToProduct()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop-> EZShop : getProductTypeByCode()
EZShop -> SaleTransaction : ApplyDiscountToSaleProduct()
SaleTransaction -> ProductType : getSellPrice()
SaleTransaction <-- ProductType : Double
EZShop <-- SaleTransaction : Boolean
user <-- EZShop : Boolean
deactivate EZShop

user -> EZShop : applyDiscountRateToSale()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : ApplyDiscountToSaleAll()
EZShop <-- SaleTransaction : Boolean
user <-- EZShop : Boolean
deactivate EZShop

user -> EZShop : endSaleTransaction()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : EndSaleUpdateProductQuantity()
SaleTransaction -> ProductType : updateProductQuantity()
SaleTransaction <-- ProductType : void
EZShop <-- SaleTransaction : Boolean
user <-- EZShop : Boolean
deactivate EZShop

user -> EZShop : computePointsForSale?API?
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : attachCardToSale()
EZShop <-- SaleTransaction : Boolean
user <-- EZShop : Boolean
deactivate EZShop


user -> EZShop : receiveCashPayment()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : PaySaleAndReturnChange()
SaleTransaction -> LoyaltyCard : PointsForSale()
LoyaltyCard-> LoyaltyCard : updatePoints()
SaleTransaction <-- LoyaltyCard : Integer?API?
EZShop <-- SaleTransaction : Double
EZShop -> EZShop : BalanceUpdate()
EZShop -> BalanceOperation : new BalanceOperation()
EZShop <-- BalanceOperation : BalanceOperation
EZShop -> EZShop : addBalanceToBalancesList()
user <-- EZShop : Double
deactivate EZShop

```

<Scenarios 6.5: Sale cancelled>

```plantuml
actor "Administrator\nShop Manager\nCashier" as user

autonumber

user -> EZShop : startReturnTransaction()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : getNewSaleTransactionId()
EZShop -> SaleTransaction : new SaleTransaction()
EZShop <-- SaleTransaction : SaleTransaction
EZShop -> EZShop : addSaleToSalesList()
user <-- EZShop : Integer
deactivate EZShop

user -> EZShop : addProductToSale()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop-> EZShop : getProductTypeByCode()
EZShop -> SaleTransaction : AddUpdateDeleteProductInSale()
SaleTransaction -> SaleTransaction : isProductInSale()
SaleTransaction -> ProductType : getSellPrice()
SaleTransaction <-- ProductType : Double
EZShop <-- SaleTransaction : Boolean
user <-- EZShop : Boolean
deactivate EZShop

user -> EZShop : endSaleTransaction()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : EndSaleUpdateProductQuantity()
SaleTransaction -> ProductType : updateProductQuantity()
SaleTransaction <-- ProductType : void
EZShop <-- SaleTransaction : Boolean
user <-- EZShop : Boolean
deactivate EZShop

user -> EZShop : deleteSaleTransaction()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : AbortSaleUpdateProductQuantity()
SaleTransaction -> ProductType : updateProductQuantity()
SaleTransaction <-- ProductType : void
EZShop <-- SaleTransaction : void
EZShop->EZShop : RemoveSaleFromSalesList()
user <-- EZShop : Double
deactivate EZShop

```

<Scenarios 7.2:  Invalid credit card number>


```plantuml
user -> EZShop : receiveCreditCardPayment()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : Boolean
EZShop -> EZShop : isValidCreditCard()
EZShop -> SaleTransaction : AbortSaleUpdateProductQuantity()
SaleTransaction -> ProductType : updateProductQuantity()
SaleTransaction <-- ProductType : void
EZShop <-- SaleTransaction : void
EZShop->EZShop : RemoveSaleFromSalesList()
user <-- EZShop : Boolean
deactivate EZShop
```

<Scenario 7.3 API????????>



<Use Case 8 & 10>

<Scenarios 8.1 & 10.1: Return transaction of product type X completed, credit card>

```plantuml
actor "Administrator\nShop Manager\nCashier" as user

autonumber
user -> EZShop : startReturnTransaction()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : return boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> ReturnTransaction : new ReturnTransaction()
EZShop <-- ReturnTransaction : return ReturnTransaction
user <-- EZShop : return returnTransactionId
deactivate EZShop

user -> EZShop : returnProduct()
activate EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : return boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> EZShop : getProductTypeByCode()
EZShop -> TransactionSale : isProductInSale()
EZShop <-- TransactionSale : return boolean
EZShop -> ReturnTransaction : addProductToReturn()
EZShop <-- ReturnTransaction : return boolean
user <-- EZShop : return boolean
deactivate EZShop

user -> EZShop : returnCreditCardPayment()
activate EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : return boolean
EZShop -> EZShop : isValidCreditCard()
EZShop -> ReturnTransaction : getAmount()
EZShop <-- ReturnTransaction : return amount
user <-- EZShop : return amount
deactivate EZShop

user -> EZShop : endReturnTransaction()
activate EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : return boolean
EZShop -> EZShop
user <-- EZShop : return boolean
deactivate EZShop
```

<Scenarios 8.2 & 10.2: Return transaction of product type X completed, cash>

```plantuml
actor "Administrator\nShop Manager\nCashier" as user

autonumber
user -> EZShop : startReturnTransaction()
activate  EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : return boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> ReturnTransaction : new ReturnTransaction()
EZShop <-- ReturnTransaction : return ReturnTransaction
user <-- EZShop : return returnTransactionId
deactivate EZShop

user -> EZShop : returnProduct()
activate EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : return boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> EZShop : getProductTypeByCode()
EZShop -> TransactionSale : isProductInSale()
EZShop <-- TransactionSale : return boolean
EZShop -> ReturnTransaction : addProductToReturn()
EZShop <-- ReturnTransaction : return boolean
user <-- EZShop : return boolean
deactivate EZShop

user -> EZShop : returnCashPayment()
activate EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : return boolean
EZShop -> ReturnTransaction : getAmount()
EZShop <-- ReturnTransaction : return amount
user <-- EZShop : return amount
deactivate EZShop

user -> EZShop : endReturnTransaction()
activate EZShop
EZShop -> User : canManageSaleTransactions()
EZShop <-- User : return boolean
EZShop -> EZShop
user <-- EZShop: return boolean
deactivate EZShop
```

<Use Case 9>

<Scenario 9.1: List credits and debits>

```plantuml
actor "Administrator\nShop Manager" as user

autonumber
user -> EZShop : getCreditsAndDebits()
activate EZShop
EZShop -> User : canManageAccounting()
EZShop <-- User : return boolean
loop forEach BalanceOperation in balanceOperationsList
    EZShop -> BalanceOperation : getDate()
    EZShop <-- BalanceOperation : return LocalDate
end
user <- EZShop : return List<BalanceOperation>
deactivate EZShop
```
