# Design Document

Authors: Jose Antonio Antona Diaz, Giuseppe D'Andrea, Marco Riggio, Gioele Scaletta

Date: 09/05/2021

Version: 1.2

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
    + recordOrderArrivalRFID(Integer orderId, String RFID): boolean
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
    + addProductToSaleRFID(Integer transactionId, String RFID): boolean
    + deleteProductFromSale(Integer transactionId, String productCode, int amount): boolean
    + deleteProductFromSaleRFID(Integer transactionId, String RFID): boolean
    + applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate): boolean
    + applyDiscountRateToSale(Integer transactionId, double discountRate): boolean
    + computePointsForSale(Integer transactionId): Integer
    + endSaleTransaction(Integer transactionId): boolean
    + deleteSaleTransaction(Integer transactionId): boolean
    + getSaleTransaction(Integer transactionId): SaleTransaction
    + startReturnTransaction(Integer transactionId): Integer
    + returnProduct(Integer returnId, String productCode, int amount): boolean
    + returnProductRFID(Integer returnId, String RFID): boolean
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
    - customersList: Map<String, Customer>
    - ordersList: Map<Integer, Order>
    - salesList: Map<Integer, SaleTransaction>
    - returnsList: Map<Integer, ReturnTransaction>
    - productsList: Map<Integer,ProductType>
    - balanceOperationsList: Map<Integer, BalanceOperation>
    - usersList: Map<Integer, User>
    - loggedIn: User

    - addUserToUsersList(User u): Boolean
    - removeUserFromUserList(User u): Boolean
    - getUserByUsername() : User
    - addProductToProductsList(Product p): Boolean
    - removeProductFromProductsList(Product p): Boolean
    - getProductTypeByCode(String productCode): ProductType
    - addOrderToOrdersList(Order o): Boolean
    - removeOrderFromOrdersList(Order o): Boolean
    - getCustomerByID(Integer ID): Customer
    - addCustomerToCustomersList(Customer c): Boolean
    - removeCustomerToCustomersList(Customer c): Boolean
    - addSaleToSalesList(SaleTransaction sale): Boolean
    - removeSaleFromSalesList(SaleTransaction sale): Boolean
    - getSaleTransactionById(Integer transactionId): SaleTransaction
    - isValidCreditCard(String cardNumber): Boolean
    - sumDigits(int[] arr): Integer
    - addBalanceToBalancesList(): Boolean
    - checkEnoughBalanceOnCardAndPay(String creditCard, Double amount): Boolean
    - updateCreditCardBalance(String creditCard, Double amount): Boolean
}

note top: All methods in EZShopInterface are implemented in EZShop
}

package it.polito.ezshop.model{
class User{
    - ID: Integer
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
    - customerCard: String
}

class SaleTransaction {
    - transactionId: Integer
    - state: enum State{PAYED, CLOSED, INPROGRESS}
    - pay: enum PaymentType{CARD, CASH}
    - amount: Double
    - salediscountRate: Double

    - saleOperationRecord: BalanceOperation
    - listOfProductsSale: Map<ProductType, Integer>

    + EditProductInSale(ProductType product, Integer amount): boolean
    + isProductInSale(ProductType product): boolean
    + ApplyDiscountToSaleProduct(Double disc, ProductType product): boolean
    + ApplyDiscountToSaleAll(Double disc): boolean
    + PointsForSale(): Integer
    + EndSaleUpdateProductQuantity(): boolean
    + AbortSaleUpdateProductQuantity(): void
    + PaySaleAndReturnChange(Double amount, Boolean method): Double
    + updateCardSale(String customerCard) : boolean
}

class ReturnTransaction {
    - returnId: Integer
    - state: enum State{FAILED, COMPLETED, DELETE}!
    - paymentType: enum PaymentType{CARD, CASH}
    - amount: Double

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
    + updateProductQuantity(Integer quantityDiff): boolean
}

class Product{
    - productId: Integer
    - RFID: String

    + isValidRFID(String rfid): static boolean
    + nextRFID(String rfid): static String
}

EZShop -|> EZShopInterface : <<implements>>

User "*" <- EZShop
Customer "*" <- EZShop
EZShop --> "*" SaleTransaction
EZShop --> "*" ReturnTransaction
EZShop --> "*" Order
EZShop --> "*" ProductType
EZShop --> "*" BalanceOperation
EZShop --> "*" Product

SaleTransaction "1" <- "0..*" ReturnTransaction

SaleTransaction "*" --> "*" ProductType
ReturnTransaction "0..*" --> "1..*" ProductType
Order "*" --> ProductType

ProductType ---> "0..*" Product
SaleTransaction ---> BalanceOperation
ReturnTransaction ---> BalanceOperation
Order ---> BalanceOperation
}
```

```plantuml

note as F
   We decided not to add credit card attributes since a real software wouldn’t store credit card data. The two methods that check and modify the credit card balance should act through the credit card API in a real scenario. 
end note

```

# Verification traceability matrix

|FR ID|EZShop|Customer|SaleTransaction|BalanceOperation|Order|ProductType|Product|User|ReturnTransaction|
|-------------|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|:-------------:|
| FR1 | X |   |   |   |   |   | | X |   |
| FR3 | X |   |   |   |   | X | |   |   |
| FR4 | X |   |   |   | X | X |X|   |   |
| FR5 | X | X | X |   |   |   | |   |   |
| FR6 | X |   | X | X |   | X |X| X | X |
| FR7 | X |   |   | X |   |   | | X | X |
| FR8 | X |   |   | X |   |   | | X |   |

# Verification sequence diagrams

## Use Case 1

### Scenario 1.1 - Create Product Type X

```plantuml
actor "Administrator\nShop Manager" as user
autonumber

user->EZShop : createProductType()
activate EZShop
EZShop->User : canEditProducts()
User-->EZShop: Boolean
EZShop->ProductType : new ProdutctType()
EZShop<--ProductType : ProductType
EZShop->EZShop : addProductToInventory()
EZShop-->user : Integer
deactivate EZShop
```

### Scenario 1.3 - Modify Product Type pricePerUnit

```plantuml
actor "Administrator\nShop Manager" as user
autonumber

user->EZShop : updateProduct()
activate EZShop
EZShop->User : canEditProducts()
User --> EZShop : Boolean
EZShop->EZShop : getProductTypeByID()
EZShop->ProductType : setBarcode()
EZShop->ProductType : setDescription()
EZShop->ProductType : setSellPrice()
EZShop->ProductType : setNotes()
EZShop-->user: boolean
deactivate EZShop
```

## Use Case 2

### Scenario 2.1 - Create user and define rights

```plantuml
actor "Administrator" as user
autonumber

user->EZShop : createUser()
activate EZShop
EZShop->User : new User()
User -->EZShop : User
EZShop->EZShop : addUserToUsersList()
EZShop-->user: Integer
deactivate EZShop
```

### Scenario 2.3 - Modify User Rights

```plantuml
actor "Administrator" as user
autonumber

user->EZShop : updateUserRights()
activate EZShop
EZShop->User : canManageUsers()
User --> EZShop : Boolean
EZShop->EZShop : getUser()
EZShop->User : setRole()
EZShop --> user: boolean
deactivate EZShop
```

## Use Case 3

### Scenario 3.1 - Order of Product Type Issued

```plantuml
actor "Administrator\nShop Manager" as user
autonumber

user->EZShop : issueReorder()
activate EZShop
EZShop->User : canManageInventory()
User --> EZShop : Boolean
EZShop->EZShop : getProductTypeByBarCode()
EZShop->Order : new Order()
Order --> EZShop : Order
EZShop->EZShop : addOrderToOrdersList()
EZShop --> user: Integer
deactivate EZShop
```

### Scenario 3.3 - Record order of ProductType arrival

```plantuml
actor "Administrator\nShop Manager" as user
autonumber

user->EZShop : recordOrderArrivalRFID()
activate EZShop
EZShop->User : canManageInventory()
User --> EZShop : Boolean
EZShop->EZShop : getOrderByID()
EZShop->Order : setOrderState()
EZShop->EZShop : getProductTypeByID()
EZShop->ProductType : updateProductQuantity()
loop forEach product in order
    EZShop->EZShop : addProduct()
end
EZShop --> user: boolean
deactivate EZShop
```

## Use Case 4

### Scenario 4.1, 4.2: Create customer and attach card to customer record

```plantuml
actor "Administrator\nShop Manager\nCashier" as user
autonumber

user->EZShop : defineCustomer()
activate EZShop
EZShop->User : canManageSalesAndCustomers()
User -->EZShop : Boolean
EZShop->Customer : new Customer()
Customer -->EZShop : Customer
EZShop->EZShop : addCustomerToCustomersList()
EZShop-->user: Integer
deactivate EZShop

user->EZShop : createCard()
activate EZShop
EZShop->LoyaltyCard : new LoyaltyCard()
LoyaltyCard --> EZShop : LoyaltyCard
EZShop-->user : Integer
deactivate EZShop

user -> EZShop : attachCardToCustomer()
activate EZShop
EZShop->EZShop : getCustomerByID()
EZShop->EZShop : getLoyaltyCard()
EZShop->Customer : setCustomerCard()
Customer--> EZShop : boolean
EZShop ->LoyaltyCard : setIsAttached() 
LoyaltyCard --> EZShop : boolean
EZShop-->user : boolean
deactivate EZShop
```

### Scenario 4.3, 4.4: Modify customer data of and/or detach card from customer record

```plantuml
actor "Administrator\nShop Manager\nCashier" as user
autonumber

user->EZShop : modifyCustomer()
activate EZShop
EZShop->User : canManageSalesAndCustomers()
User -->EZShop : Boolean
EZShop->EZShop : getCustomerByID()
EZShop->Customer : setCustomerName()
alt if newCustomerCard is not null
    EZShop->Customer : setCustomerCard(newCustomerCard)
    Customer->LoyaltyCard : oldCustomerCard.setIsAttached(false)
    Customer->LoyaltyCard : newCustomerCard.setIsAttached(true)
    
end
EZShop->user : boolean
deactivate EZShop
```
## Use Case 5

### Scenario 5.1: Login
```plantuml
actor "Administrator\nShop Manager\nCashier" as user
autonumber

activate EZShop
user->EZShop : login()
EZShop->EZShop : getUserByUsername()
alt if user is found
    EZShop->User : getPassword()
    alt if password is correct
        EZShop-->user: return User
    end
end
deactivate EZShop
```


## Use Case 6 & 7

### Scenarios 6.1, 6.2, 6.3, 6.4, 6.6, 7.1, (7.4): Full Sale with product discount, sale discounts and loyalty card update

```plantuml
scale 0.9

actor "Administrator\nShop Manager\nCashier" as user

autonumber

user -> EZShop : startSaleTransaction()()
activate  EZShop
EZShop -> User : canManageSalesAndCustomers()
EZShop <-- User : Boolean
EZShop -> SaleTransaction : new SaleTransaction()
EZShop <-- SaleTransaction : SaleTransaction
EZShop -> EZShop : addSaleToSalesList()
user <-- EZShop : Integer
deactivate EZShop

loop forEach product
    user -> EZShop : addProductToSaleRFID()
    activate  EZShop
    'EZShop -> User : canManageSalesAndCustomers()
    'EZShop <-- User : Boolean
    EZShop -> EZShop : getSaleTransactionById()
    EZShop-> EZShop : getProductTypeByRFID()
    EZShop -> SaleTransaction : EditProductInSale()
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
end

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

user -> EZShop : receiveCashPayment()
activate  EZShop
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : PaySaleAndReturnChange()
EZShop <-- SaleTransaction : Double
user <-- EZShop : Double
deactivate EZShop

user -> EZShop : computePointsForSale() 
activate  EZShop
EZShop -> EZShop : getSaleTransactionById()
EZShop -> SaleTransaction : PointsForSale()
EZShop <-- SaleTransaction : Integer
user <-- EZShop : Integer
deactivate EZShop

user -> EZShop : modifyPointsOnCard()
activate EZShop
EZShop -> EZShop : getLoyaltyCard()
EZShop -> SaleTransaction : updateCardSale()
SaleTransaction -> LoyaltyCard : updatePoints()
SaleTransaction <-- LoyaltyCard : Boolean
EZShop <-- SaleTransaction : Boolean
user <-- EZShop :Boolean
deactivate EZShop

user -> EZShop : recordBalanceUpdate()
activate EZShop
EZShop -> BalanceOperation : new BalanceOperation()
EZShop <-- BalanceOperation : BalanceOperation
EZShop -> EZShop : addBalanceToBalancesList()
user <-- EZShop : boolean
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

user -> EZShop : startSaleTransaction()()
activate  EZShop
EZShop -> User : canManageSalesAndCustomers()
EZShop <-- User : Boolean
EZShop -> SaleTransaction : new SaleTransaction()
EZShop <-- SaleTransaction : SaleTransaction
EZShop -> EZShop : addSaleToSalesList()
user <-- EZShop : Integer
deactivate EZShop

loop forEach product
    user -> EZShop : addProductToSaleRFID()
    activate  EZShop
    'EZShop -> User : canManageSalesAndCustomers()
    'EZShop <-- User : Boolean
    EZShop -> EZShop : getSaleTransactionById()
    EZShop-> EZShop : getProductTypeByRFID()
    EZShop -> SaleTransaction : EditProductInSale()
    SaleTransaction -> SaleTransaction : isProductInSale()
    SaleTransaction -> ProductType : getSellPrice()
    SaleTransaction <-- ProductType : Double
    EZShop <-- SaleTransaction : Boolean
    user <-- EZShop : Boolean
    deactivate EZShop
end

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
actor "Administrator\nShop Manager\nCashier" as user
autonumber

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
actor "Administrator\nShop Manager\nCashier" as user
autonumber

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

user -> EZShop : returnProductRFID()
activate EZShop
EZShop -> User : canManageSalesAndCustomers()
EZShop <-- User : boolean
EZShop -> EZShop : getSaleTransactionById()
EZShop -> EZShop : getProductTypeByRFID()
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
    EZShop -> SaleTransaction : EditProductInSale()
    EZShop <-- SaleTransaction : boolean
    EZShop -> ProductType : updateProductQuantity()
    EZShop <-- ProductType : boolean
end
user <-- EZShop: boolean
deactivate EZShop

user -> EZShop : recordBalanceUpdate()
activate EZShop
EZShop -> BalanceOperation : new BalanceOperation()
EZShop <-- BalanceOperation : BalanceOperation
EZShop -> EZShop : addBalanceToBalancesList()
user <-- EZShop : boolean
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
