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
    -usersList: List<User>
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

|FR ID|EZShop|Customer|LoyaltyCard|SaleTransaction|BalanceOperation|Order|ProductType|User|ReturnTransaction|
|-------------| :-------------: | :-------------: | :-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|
|  FR1 |  | |   |   |   |  |   |   |    | 
|  FR2 |  |   |   |   |   |  |   |   |    | 
|  FR3 |  |   |   |   |   |  |   |   |    | 
|  FR4 |  |   |   |   |   |  |   |   |    | 
|  FR5 |  |   |   |   |   |  |   |   |    | 
|  FR6 | X |   | X  | X  | X  |  | X  |  X |    | 
|  FR7 |  |   |   | X  | X  |  |   | X   |    | 
|  FR8 |  |   |   |   |   |  |   |   |    | 







# Verification sequence diagrams 
\<select key scenarios from the requirement document. For each of them define a sequence diagram showing that the scenario can be implemented by the classes and methods in the design>

<Use Case 6>

<Scenarios 6.1, 6.2, 6.3, 7.1: Sale with product and sale discounts>

```plantuml
participant EZShop as 1
participant SaleTransaction as 2
participant ProductType as 3
participant SalePayment as 4
participant BalanceOperation as 5


1->2 : startSaleTransaction()
2->3 : addProductToSale()
3->3 : getProductTypeByBarCode()
3->3 : updateQuantity()
3-->2 : addProductToSale()
2->2 : applyDiscountRateToProduct()
2->2 : applyDiscountRateToSale()
2->4 : closeSaleTransaction()
4->4 : getSaleTicket()
4->4 : receiveCreditCardPayment()
4->5 : recordBalanceUpdate()
5-->1 : recordBalanceUpdate()

```

<Scenarios 6.4, 6.6, 7.4: Sale with loyalty card and cash payment>

```plantuml
participant EZShop as 1
participant SaleTransaction as 2
participant ProductType as 3
participant LoyaltyCard as 4
participant SalePayment as 5
participant BalanceOperation as 6

1->2 : startSaleTransaction()
2->3 : addProductToSale()
3->3 : getProductTypeByBarCode()
3->3 : updateQuantity()
3-->2 : addProductToSale()
2->2 : computePointsForSale()
2->4 : modifyPointsOnCard()
4-->2 : modifyPointsOnCard()
2->5 : closeSaleTransaction()
5->5 : getSaleTicket()
5->5 : receiveCashPayment()
5->6 : recordBalanceUpdate()
6-->1 : recordBalanceUpdate()

```


<Scenarios 6.5, 7.2, 7.3:  Sale cancelled because of invalid credit card or not enough credit>

```plantuml
participant EZShop as 1
participant SaleTransaction as 2
participant ProductType as 3
participant SalePayment as 5

1->2 : startSaleTransaction()
2->3 : addProductToSale()
3->3 : getProductTypeByBarCode()
3->3 : updateQuantity()
3-->2 : addProductToSale()
2->5 : closeSaleTransaction()
5->5 : getSaleTicket()
5->5 : receiveCreditCardPayment()
5->1 : deleteSaleTicket()
```