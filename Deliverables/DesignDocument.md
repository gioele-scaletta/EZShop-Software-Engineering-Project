# Design Document

Authors: Jose Antonio Antona Diaz, Giuseppe D'Andrea, Marco Riggio, Gioele Scaletta

Date:

Version:

# Contents

- [High level design](#high-level-design)
- [Low level design](#low-level-design)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# Instructions

The design must satisfy the Official Requirements document, notably functional and non functional requirements

# High level design

The application implements a 3-tiers architecture, where the data layes, the logic layer and the presentation layer are in different packages. The GUI interacts with the EZShop logic layer via the Façade class "EZShop".

```plantuml


package it.polito.ezshop {
package "it.polito.ezshop.GUI"
package "it.polito.ezshop.data"
package "it.polito.ezshop.model"
package "it.polito.ezshop.exceptions"
it.polito.ezshop.data <- it.polito.ezshop.GUI
it.polito.ezshop.exceptions <-- it.polito.ezshop.data
it.polito.ezshop.model <- it.polito.ezshop.data
it.polito.ezshop.exceptions <-- it.polito.ezshop.model
}

```

# Low level design
```plantuml
note as N
    All classes in MODEL and DATA are stored persistently.
    Here we decided to model explicitely
    the relationships with lists and maps.
end note

```
```plantuml
scale 0.88

package it.polito.ezshop.data{


interface EZShopInterface {
    .. Reset ..
    + reset(): void
    .. FR1 ..
    + createUser(String username, String password, String role): Integer
    + deleteUser(Integer id): boolean
    + getAllUsers(): List<User>
    + getUser(Integer id): User
    + updateUserRights(Integer id, String role): boolean
    + login(String username, String password): User
    + logout(): boolean
    .. FR3 ..
    + createProductType(String description, String productCode, double pricePerUnit, String note): Integer
    + updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote): boolean
    + deleteProductType(Integer id): boolean
    + getAllProductTypes(): List<ProductType>
    + getProductTypeByBarCode(String barCode): ProductType
    + getProductTypesByDescription(String description): List<ProductType>
    .. FR4 ..
    + updateQuantity(Integer productId, int toBeAdded): boolean
    + updatePosition(Integer productId, String newPos): boolean
    + issueOrder(String productCode, int quantity, double pricePerUnit): Integer
    + payOrderFor(String productCode, int quantity, double pricePerUnit): Integer
    + payOrder(Integer orderId): boolean
    + recordOrderArrival(Integer orderId): boolean
    + getAllOrders(): List<Order>
    .. FR5 ..
    + defineCustomer(String customerName): Integer
    + modifyCustomer(Integer id, String newCustomerName, String newCustomerCard): boolean
    + deleteCustomer(Integer id): boolean
    + getCustomer(Integer id): Customer
    + getAllCustomers(): List<Customer>
    + createCard(): String
    + attachCardToCustomer(String customerCard, Integer customerId): boolean
    + modifyPointsOnCard(String customerCard, int pointsToBeAdded): boolean
    .. FR6 ..
    + startSaleTransaction(): Integer
    + addProductToSale(Integer transactionId, String productCode, int amount): boolean
    + deleteProductFromSale(Integer transactionId, String productCode, int amount): boolean
    + applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate): boolean
    + applyDiscountRateToSale(Integer transactionId, double discountRate): boolean
    + computePointsForSale(Integer transactionId): Integer
    + endSaleTransaction(Integer transactionId): boolean
    + deleteSaleTransaction(Integer transactionId): boolean
    + getSaleTransaction(Integer transactionId): SaleTransaction
    + startReturnTransaction(Integer transactionId): Integer
    + returnProduct(Integer returnId, String productCode, int amount): boolean
    + endReturnTransaction(Integer returnId, boolean commit): boolean
    + deleteReturnTransaction(Integer returnId): boolean
    .. FR7 ..
    + receiveCashPayment(Integer transactionId, double cash): double
    + receiveCreditCardPayment(Integer transactionId, String creditCard): boolean
    + returnCashPayment(Integer returnId): double
    + returnCreditCardPayment(Integer returnId, String creditCard): double
    .. FR8 ..
    + recordBalanceUpdate(double toBeAdded): boolean
    + getCreditsAndDebits(LocalDate from, LocalDate to): List<BalanceOperation>
    + computeBalance(): double
}

class EZShop{
    - customersList: List<Customer>
    - ordersList: List<Order>
    - salesList: List<SaleTransaction>
    - returnsList: List<ReturnTransaction>
    - productsList: List<ProductType>
    - balanceOperationsList: List<BalanceOperation>
    - usersList: List<User>
    - loggedIn: User

    + getNewSaleTransactionId(): Integer
    + addSaleToSalesList(SaleTransaction sale): void
    + removeSaleFromSalesList(SaleTransaction sale): void
    + getSaleTransactionById(Integer transactionId): SaleTransaction
    + getProductTypeByCode(String productCode): ProductType
    + isValidCreditCard(String cardNumber): boolean
    + sumDigits(int[] arr): Integer
    + addBalanceToBalancesList(): void
    + newBalanceUpdate(Double amount): void
    + checkEnoughBalanceOnCardAndPay(String creditCard, Double amount): Boolean
    + updateCreditCardBalance(String creditCard, Double amount): Boolean
}

note top: All methods in EZShopInterface are implemented in EZShop
}

package it.polito.ezshop.model{
class User{
    - role: String
    - username: String
    - password: String

    + canManageUsers(): boolean
    + canEditProducts(): boolean
    + canListProducts(): boolean
    + canManageInventory(): boolean
    + canManageSalesAndCustomers(): boolean
    + canManagePayments(): boolean
    + canManageAccounting(): boolean
}

class Customer{
    - customerName: String
    - customerId: Integer

    - customerCard: LoyaltyCard
}

class LoyaltyCard{
    - cardId: String
    - cardPoints: Integer

    + updatePoints(Integer points)
}

class SaleTransaction {
    - transactionId: Integer
    - state: enum State{PAYED, CLOSED, INPROGRESS}
    - pay: enum PaymentType{CARD, CASH}
    - currentamount: Double
    - salediscountRate: Double

    - transactionCard: LoyaltyCard
    - saleOperationRecord: BalanceOperation
    - listOfProductsSale: Map<ProductType, Integer>

    + AddUpdateDeleteProductInSale(ProductType product, Integer amount): boolean
    + isProductInSale(ProductType product): boolean
    + ApplyDiscountToSaleProduct(Double disc, ProductType product): boolean
    + ApplyDiscountToSaleAll(Double disc): boolean
    + PointsForSale(): Integer
    + EndSaleUpdateProductQuantity(): boolean
    + AbortSaleUpdateProductQuantity(): void
    + PaySaleAndReturnChange(Double amount, Boolean method): Double
    + attachCardToSale(String customerCard)
}

class ReturnTransaction {
    - returnId: Integer
    - state: enum State{FAILED, COMPLETED, DELETE}!
    - paymentType: enum PaymentType{CARD, CASH}
    - currentamount: Double

    - saleTransaction: SaleTransaction
    - returnOperationRecord: BalanceOperation
    - listOfProductsReturn: Map<ProductType, Integer>

    + addProductToReturn(ProductType product, Integer quantity): boolean
}

class Order {
    - orderId: Integer
    - pricePerUnit: Double
    - productCode: String
    - quantity: Integer
    - status: enum Status{PAYED, ISSUED, ORDERED, COMPLETED}

    - orderOperationRecord: BalanceOperation
    - product: ProductType
}

class BalanceOperation {
    - balanceID: Integer
    - description: enum Description{DEBIT, CREDIT}
    - amount: Double
    - date: LocalDate
}

class ProductType{
    - productId: Integer
    - barcode: String
    - description: String
    - sellPrice: Double
    - quantity: Integer
    - productDiscountRate: Double
    - notes: String
    - AisleID: Integer
    - RackID: String
    - LevelID: Integer

    + isValidBarcode(String barcode): static boolean
    + updateProductQuantity(Integer quantitytorem): void
}

EZShop -|> EZShopInterface : <<implements>>

User "*" <- EZShop
Customer "*" <- EZShop
EZShop --> "*" SaleTransaction
EZShop --> "*" ReturnTransaction
EZShop --> "*" Order
EZShop --> "*" ProductType
EZShop --> "*" BalanceOperation

Customer --> "0..1" LoyaltyCard
SaleTransaction "1..*" --> "0..1" LoyaltyCard

SaleTransaction "1" <- "0..*" ReturnTransaction

SaleTransaction "*" --> "*" ProductType
ReturnTransaction "0..*" --> "1..*" ProductType
Order "*" --> ProductType

SaleTransaction ---> BalanceOperation
ReturnTransaction ---> BalanceOperation
Order ---> BalanceOperation
}
```

```plantuml

note as F
    We decided not to add credit card attributes since shops don't usually store credit card data. The two methods that will check and modify the credit card balance should act through the credit card API.
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

## Use Case 1

### Scenario 1.1 - Create Product Type X

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

### Scenario 1.3 - Modify Product Type pricePerUnit

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

## Use Case 2

### Scenario 2.1 - Create user and define rights

```plantuml
participant GUI as 1
participant EZShop as 2
participant User as 3

1->2 : createUser()
2->3 : new User()
2->2 : addUserToUsersList()
```

### Scenario 2.3 - Create user and define rights

```plantuml
participant GUI as 1
participant EZShop as 2
participant User as 3

1->2 : updateUserRights()
2->3 : canManageUsers()
2->2 : getUser()
2->3 : setRole()
```

## Use Case 3

### Scenario 3.1 - Order od Product Type Issued

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

### Scenario 3.3 - Record order of ProductType arrival

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

## Use Case 4

### Scenario 4.2: Attach card to customer record

```plantuml
EZShop -> EZShop : createCard()
EZShop ->Customer : getCustomer()
Customer -> Customer : getCustomerId()
Customer --> EZShop : customerId
EZShop -> EZShop : attachCardToCustomer()
```

### Scenario 4.2: Detach card from customer record

```plantuml
EZShop ->Customer : getCustomer()
Customer -> Customer : getCustomerId()
Customer --> EZShop : customerId
Customer -> Customer : getCustomerName()
Customer --> EZShop : customerName
EZShop -> EZShop : modifyCustomer()
```

## Use Case 6 & 7

### Scenarios 6.1, 6.2, 6.3, 6.4, 6.6, 7.1, (7.4): Full Sale with product discount, sale discounts and loyalty card update

```plantuml

actor "Administrator\nShop Manager\nCashier" as user

autonumber

user -> EZShop : startReturnTransaction()
activate  EZShop
EZShop -> User : canManageSalesAndCustomers()
EZShop <-- User : Boolean
EZShop -> EZShop : getNewSaleTransactionId()
EZShop -> SaleTransaction : new SaleTransaction()
EZShop <-- SaleTransaction : SaleTransaction
EZShop -> EZShop : addSaleToSalesList()
user <-- EZShop : Integer
deactivate EZShop

user -> EZShop : addProductToSale()
activate  EZShop
'EZShop -> User : canManageSalesAndCustomers()
'EZShop <-- User : Boolean
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
'EZShop -> User : canManageSalesAndCustomers()
'EZShop <-- User : Boolean
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
'EZShop -> User : canManageSalesAndCustomers()
'EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : ApplyDiscountToSaleAll()
EZShop <-- SaleTransaction : Boolean
user <-- EZShop : Boolean
deactivate EZShop

user -> EZShop : endSaleTransaction()
activate  EZShop
'EZShop -> User : canManageSalesAndCustomers()
'EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : EndSaleUpdateProductQuantity()
SaleTransaction -> ProductType : updateProductQuantity()
SaleTransaction <-- ProductType 
EZShop <-- SaleTransaction : Boolean
user <-- EZShop : Boolean
deactivate EZShop

user -> EZShop : computePointsForSale()
user -> EZShop : getCustomer()
activate  EZShop
'EZShop -> User : canManageSalesAndCustomers()
'EZShop <-- User : Boolean
EZShop -> Customer: getCustomerCard()
EZShop <-- Customer: LoyaltyCard
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : attachCardToSale()
EZShop <-- SaleTransaction : Boolean
user <-- EZShop : Integer
deactivate EZShop


user -> EZShop : receiveCashPayment()
activate  EZShop
'EZShop -> User : canManageSalesAndCustomers()
'EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : PaySaleAndReturnChange()
SaleTransaction -> LoyaltyCard : PointsForSale()
LoyaltyCard-> LoyaltyCard : updatePoints()
SaleTransaction <-- LoyaltyCard : Integer
EZShop <-- SaleTransaction : Double
EZShop -> EZShop : BalanceUpdate()
EZShop -> BalanceOperation : new BalanceOperation()
EZShop <-- BalanceOperation : BalanceOperation
EZShop -> EZShop : addBalanceToBalancesList()
user <-- EZShop : Double
deactivate EZShop

```

```plantuml
note as N
    In this and the next sequence diagram the method canManageSalesAndCustomers() has been represented only in the first API function call for the sake of readability.
end note

```


### Scenarios 6.5: Sale cancelled

```plantuml
actor "Administrator\nShop Manager\nCashier" as user

autonumber

user -> EZShop : startReturnTransaction()
activate  EZShop
EZShop -> User : canManageSalesAndCustomers()
EZShop <-- User : Boolean
EZShop -> EZShop : getNewSaleTransactionId()
EZShop -> SaleTransaction : new SaleTransaction()
EZShop <-- SaleTransaction : SaleTransaction
EZShop -> EZShop : addSaleToSalesList()
user <-- EZShop : Integer
deactivate EZShop

user -> EZShop : addProductToSale()
activate  EZShop
'EZShop -> User : canManageSalesAndCustomers()
'EZShop <-- User : Boolean
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
'EZShop -> User : canManageSalesAndCustomers()
'EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : EndSaleUpdateProductQuantity()
SaleTransaction -> ProductType : updateProductQuantity()
SaleTransaction <-- ProductType 
EZShop <-- SaleTransaction : Boolean
user <-- EZShop : Boolean
deactivate EZShop

user -> EZShop : deleteSaleTransaction()
activate  EZShop
'EZShop -> User : canManageSalesAndCustomers()
'EZShop <-- User : Boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : AbortSaleUpdateProductQuantity()
SaleTransaction -> ProductType : updateProductQuantity()
SaleTransaction <-- ProductType 
EZShop <-- SaleTransaction 
EZShop->EZShop : RemoveSaleFromSalesList()
user <-- EZShop : Double
deactivate EZShop

```

### Scenarios 7.2:  Invalid credit card number

```plantuml
user -> EZShop : receiveCreditCardPayment()
activate  EZShop
EZShop -> User : canManageSalesAndCustomers()
EZShop <-- User : Boolean
EZShop -> EZShop : isValidCreditCard()
EZShop -> SaleTransaction : AbortSaleUpdateProductQuantity()
SaleTransaction -> ProductType : updateProductQuantity()
SaleTransaction <-- ProductType
EZShop <-- SaleTransaction 
EZShop->EZShop : RemoveSaleFromSalesList()
user <-- EZShop : Boolean
deactivate EZShop
```

### Scenario 7.3: Not enough balance on credit card


```plantuml
user -> EZShop : receiveCreditCardPayment()
activate  EZShop
EZShop -> User : canManageSalesAndCustomers()
EZShop <-- User : Boolean
EZShop -> EZShop : isValidCreditCard()
EZShop -> EZShop : checkEnoughBalanceOnCardAndPay()
EZShop -> SaleTransaction : AbortSaleUpdateProductQuantity()
SaleTransaction -> ProductType : updateProductQuantity()
SaleTransaction <-- ProductType
EZShop <-- SaleTransaction 
EZShop->EZShop : RemoveSaleFromSalesList()
user <-- EZShop : Boolean
deactivate EZShop
```


## Use Case 8 & 10

### Scenarios 8.1/8.2 & 10.1/10.2: Return transaction of product type X completed, credit card/cash

```plantuml
actor "Administrator\nShop Manager\nCashier" as user

autonumber
user -> EZShop : startReturnTransaction()
activate  EZShop
EZShop -> User : canManageSalesAndCustomers()
EZShop <-- User : boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> ReturnTransaction : new ReturnTransaction()
EZShop <-- ReturnTransaction : ReturnTransaction
user <-- EZShop : Integer
deactivate EZShop

user -> EZShop : returnProduct()
activate EZShop
EZShop -> User : canManageSalesAndCustomers()
EZShop <-- User : boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> EZShop : getProductTypeByCode()
EZShop -> TransactionSale : isProductInSale()
EZShop <-- TransactionSale : boolean
EZShop -> ReturnTransaction : addProductToReturn()
EZShop <-- ReturnTransaction : boolean
user <-- EZShop : boolean
deactivate EZShop

alt Manage credit card return
    user -> EZShop : returnCreditCardPayment()
    activate EZShop
    EZShop -> User : canManageSalesAndCustomers()
    EZShop <-- User : boolean
    EZShop -> EZShop : isValidCreditCard()
    EZShop -> ReturnTransaction : getAmount()
    EZShop <-- ReturnTransaction : return amount
    EZShop -> EZShop : updateCreditCardBalance()
    user <-- EZShop : return amount
    deactivate EZShop
else Manage cash return
    user -> EZShop : returnCashPayment()
    activate EZShop
    EZShop -> User : canManageSalesAndCustomers()
    EZShop <-- User : return boolean
    EZShop -> ReturnTransaction : getAmount()
    EZShop <-- ReturnTransaction : Double
    user <-- EZShop : Double
    deactivate EZShop
end

user -> EZShop : endReturnTransaction()
activate EZShop
EZShop -> User : canManageSalesAndCustomers()
EZShop <-- User : boolean
EZShop -> ReturnTransaction : getSaleTransaction()
EZShop <-- ReturnTransaction : SaleTransaction
EZShop -> ReturnTransaction : getListOfProductsReturn()
EZShop <-- ReturnTransaction : Map<ProductType, Integer>
loop forEach ProductType in listOfProductsReturn
    EZShop -> SaleTransaction : addUpdateDeleteProductInSale()
    EZShop <-- SaleTransaction : boolean
    EZShop -> ProductType : updateProductQuantity()
    EZShop <-- ProductType : boolean
end
user <-- EZShop: boolean
deactivate EZShop
```

## Use Case 9

### Scenario 9.1: List credits and debits

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
