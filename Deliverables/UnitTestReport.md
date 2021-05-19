# Unit Testing Documentation

Authors:

Date:

Version:

# Contents

- [Black Box Unit Tests](#black-box-unit-tests)



- [White Box Unit Tests](#white-box-unit-tests)


# Black Box Unit Tests

    <Define here criteria, predicates and the combination of predicates for each function of each class.
    Define test cases to cover all equivalence classes and boundary conditions.
    In the table, report the description of the black box test case and (traceability) the correspondence with the JUnit test case writing the 
    class and method name that contains the test case>
    <JUnit test classes must be in src/test/java/it/polito/ezshop   You find here, and you can use,  class TestEzShops.java that is executed  
    to start tests
    >

    <We used a bottom-up testing strategy>

# Class UserImpl 

## Method isAllowedRole



**Criteria for method *isAllowedRole*:**

- Validity of the String parameter


**Predicates for method *isAllowedRole*:**

| Criterion                        | Predicate |
| -------------------------------- | --------- |
| Validity of the String parameter | Valid     |
|                                  | Invalid   |
|                                  | NULL      |
||Empty|


**Combination of predicates**:


|  Validity of the String parameter | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
| Valid                            |  V                      | admin_role="Administrator"  <br />isAllowedRole(admin_role) <br /><br />-> true|     testisAllowedRole()            |
| Invalid                          |  I                | admin_role="administrator"  <br /> isAllowedRole(admin_role) <br /><br />-> false                 |  testisAllowedRole()  |
| Empty                            |  I               |  admin_role=""  <br />isAllowedRole(admin_role) <br /><br />-> false                 |  testisAllowedRole()  |
| NULL                             |  I                |  admin_role=null  <br /> isAllowedRole(admin_role) <br /><br />-> false                 |   testisAllowedRole()|




## Methods canManageUsers

**Criteria for methods *CanManageUsers*:**

- Value of field role


**Predicates for methods *canManageUsers*:**

| Criterion                        | Predicate |
| -------------------------------- | --------- |
|Value of field role               | Administrator |
|                                  | Cashier   |
|                                  | ShopManager  |

**Combination of predicates**:


|  Validity of the String parameter | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
| Aministrator                          |  V                     |  user.setRole(admin_role)<br /> user.canManageUsers()<br /><br />-> true|     testcanManageUsers()|
| Cashier                               |  I               |  user.setRole(cashier_role) <br /> user.canManageUsers()<br /><br />-> false| testcanManageUsers() |
| ShopManager                           |  I               |  user.setRole(shop_manager_role) <br /> user.canManageUsers()<br /><br />-> false | testcanManageUsers() |


## Methods canManageProductList, canManageInventory, canManageAccounting

**Criteria for methods *canManageProductList, canManageInventory, canManageAccounting*:**

- Value of field role

**Predicates for methods *canManageProductList, canManageInventory, canManageAccounting*:**

| Criterion                        | Predicate |
| -------------------------------- | --------- |
|Value of field role               | Administrator |
|                                  | Cashier   |
|                                  | ShopManager  |

**Combination of predicates**:

|  Validity of the String parameter | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
| Aministrator                          |  V                     |  user.setRole(admin_role)<br /><br /> user.canManageProductList()<br /> user.canManageInventory()<br />user.canManageAccounting()<br /><br />-> true|   testcanManageProductList()<br/>testcanManageInventory()<br/>testcanManageAccounting()  |
| Cashier                               |  V               |  user.setRole(cashier_role) <br /><br /> user.canManageProductList()<br /> user.canManageInventory()<br />user.canManageAccounting()<br /><br />-> true|   testcanManageProductList()<br/>testcanManageInventory()<br/>testcanManageAccounting() |
| ShopManager                           |  I               |  user.setRole(shop_manager_role) <br /><br /> user.canManageProductList()<br /> user.canManageInventory()<br />user.canManageAccounting()<br /><br />-> false| testcanManageProductList()<br/>testcanManageInventory()<br/>testcanManageAccounting()  |



## Methods canManageCustomers, canManageSaleTransactions, canManagePayments

**Criteria for methods *canManageCustomers, canManageSaleTransactions, canManagePayments*:**

- Value of field role

**Predicates for methods *canManageCustomers, canManageSaleTransactions, canManagePayments*:**

| Criterion                        | Predicate |
| -------------------------------- | --------- |
| Value of field role              | Administrator |
|                                  | Cashier   |
|                                  | ShopManager  |

**Combination of predicates**:

|  Validity of the String parameter | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
| Aministrator                          |  V                     |  user.setRole(admin_role)<br /><br /> user.canManageCustomers()<br /> user.canManageSaleTransactions()<br />user.canManagePayments()<br /><br />-> true|     testcanManageCustomers()<br />testcanManageSaleTransactions()<br />testcanManagePayments()|
| Cashier                               |  V               |  user.setRole(cashier_role) <br /><br /> user.canManageCustomers()<br /> user.canManageSaleTransactions()<br />user.canManagePayments()<br /><br />-> true|  testcanManageCustomers()<br />testcanManageSaleTransactions()<br />testcanManagePayments()|
| ShopManager                           |  V               |  user.setRole(shop_manager_role) <br /><br /> user.canManageCustomers()<br /> user.canManageSaleTransactions()<br />user.canManagePayments()<br /><br />-> true|  testcanManageCustomers()<br />testcanManageSaleTransactions()<br />testcanManagePayments()|



# Class ProductTypeImpl 

## Method isValidCode

**Criteria for method *isValidCode*:**

- Validity of length String parameter productCode
- Validity of digits type of String parameter productCode
- Validity of digits sum String parameter productCode
- Validity of String parameter


**Predicates for method *isValidCode*:**

| Criterion                        | Predicate |
| -------------------------------- | --------- |
| Validity of length               | >=12 and <=14 |
|                                  | <12 or >14   |
| Validity of digits type         | is parsable Long   |
|                                  |nor parsable Long |
|Validity of digits sum            | Closest higher tens to sum calculation (x1 and x3), minus sum is equal to 3 |
|Validity of String parameter      | null|
|                                  |Empty|

**Combination of predicates**:


|  Validity of the String parameter |  Validity of length| Validity of digits type | Validity of digits sum  | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|-------|-------|-------|
| Valid    |   >12 and <14     | is  parsable   | Valid   |  V                      | isValidCode("5701234567899") <br /><br />-> true   | testisValidCode ()|       
| *  |   *      |  *        | Invalid |  I                | isValidCode("111111111111") <br /><br />-> false                 |  testisValidCode () |
| *  |   *       |  is not parsable        | * |  I                | isValidCode("5701234a67899") <br /><br />-> false                 | testisValidCode ()|
| *  |   >12 and <14       |  *     | * |  I                | isValidCode("570167899") <br /><br />-> false | testisValidCode ()|
|Empty |   *      |  *       | * |  I                |isValidCode("")  <br /><br />-> false                 |   testisValidCode ()              |
|null |   *      |  *       | * |  I                | isValidCode(null)  <br /><br />-> false |   testisValidCode ()    |


## Method isValidLocation

**Criteria for method *isValidLocation*:**

- Validity of format ("number-character-number") String parameter location
- Validity of String parameter


**Predicates for method *isValidLocation*:**

| Criterion                        | Predicate |
| -------------------------------- | --------- |
| Validity of format               | format is "number-string-number" |
|                                  | wrong - usage or wrong element format (number/string)  |
|Validity of String parameter      | null|
|                                  |Empty|

**Combination of predicates**:


|  Validity of the String parameter |  Validity of format| Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|-------|
|Valid  | Valid format |  V | isValidLocation("3-a-2")  <br /><br />-> true | testisValidLocation ()|
|Valid  | Invalid format| I | isValidLocation("a-2-1") <br /> isValidLocation("1b-3") <br /><br />-> false  | testisValidLocation ()|
|Empty |   -    |  V              | isValidLocation("")  <br /><br />-> true  (location reset values)             |  testisValidLocation ()              |
|null  |  -     |  V              | isValidLocation(null)  <br /><br />-> true (location reset values)    |testisValidLocation ()|

# Class SaleTransactionImpl

## Method setTicketNumber

**Criteria for method *setTicketNumber*:**

- setTicketNumber parameter is a valid Integer object or null

**Predicates for method *setTicketNumber*:**

| Criterion                        | Predicate  |
| -------------------------------- | ---------- |
| Validity of Integer parameter    | Valid      |
|                                  | NULL       |

**Combination of predicates**:

| Validity of the Integer parameter | Valid / Invalid | Description of the test case | JUnit test case |
| --------------------------------- | --------------- | ---------------------------- | --------------- |
| Valid                             | V               | setTicketNumber(10)<br/>getTicketNumber().equals(10)<br/><br/>-> true | TestSaleTransactionImpl.testSetTicketNumber() |
| NULL                              | I               | setTicketNumber(10)<br/>setTicketNumber(null)<br/>getTicketNumber().equals(10)<br/><br/>-> true (not modified) | TestSaleTransactionImpl.testSetTicketNumber() |

## Method setPrice

**Criteria for method *setPrice*:**

- price parameter is a valid double base type

**Predicates for method *setPrice*:**

| Criterion                                     | Predicate  |
| --------------------------------------------- | ---------- |
| Validity of double parameter                  | Valid      |

**Combination of predicates**:

|  Validity of the double parameter | Valid / Invalid | Description of the test case | JUnit test case |
| --------------------------------- | --------------- | ---------------------------- | --------------- |
| Valid                             | V               | setPrice(4.99)<br/>getPrice().equals(4.99)<br/><br/>-> true | TestSaleTransactionImpl.testSetPrice() |

## Method setDiscountRate

**Criteria for method *setDiscountRate*:**

- discountRate parameter is a valid double base type

**Predicates for method *setDiscountRate*:**

| Criterion                                     | Predicate  |
| --------------------------------------------- | ---------- |
| Validity of double parameter                  | Valid      |

**Combination of predicates**:

|  Validity of the double parameter | Valid / Invalid | Description of the test case | JUnit test case |
| --------------------------------- | --------------- | ---------------------------- | --------------- |
| Valid                             | V               | setDiscountRate(0.15)<br/>getDiscountRate().equals(0.15)<br/><br/>-> true | TestSaleTransactionImpl.testSetDiscountRate() |

# Class TicketEntryImpl

## Method setBarCode

**Criteria for method *setBarCode*:**

- barCode parameter is a valid String object or null

**Predicates for method *setBarCode*:**

| Criterion                        | Predicate  |
| -------------------------------- | ---------- |
| Validity of String parameter     | Valid      |
|                                  | NULL       |

**Combination of predicates**:

| Validity of the String parameter  | Valid / Invalid | Description of the test case | JUnit test case |
| --------------------------------- | --------------- | ---------------------------- | --------------- |
| Valid                             | V               | setBarCode("5701234567899")<br/>getBarCode().equals("5701234567899")<br/><br/>-> true | TestTicketEntryImpl.testSetBarCode() |
| NULL                              | I               | setBarCode("5701234567899")<br/>setBarCode(null)<br/>getBarCode().equals("5701234567899")<br/><br/>-> true (not modified) | TestTicketEntryImpltestSetBarCode() |

## Method setProductDescription

**Criteria for method *setProductDescription*:**

- productDescription parameter is a valid String object or null

**Predicates for method *setProductDescription*:**

| Criterion                        | Predicate  |
| -------------------------------- | ---------- |
| Validity of String parameter     | Valid      |
|                                  | NULL       |

**Combination of predicates**:

| Validity of the String parameter  | Valid / Invalid | Description of the test case | JUnit test case |
| --------------------------------- | --------------- | ---------------------------- | --------------- |
| Valid                             | V               | setProductDescription("ZZZ")<br/>getProductDescription().equals("ZZZ")<br/><br/>-> true | TestTicketEntryImpl.testSetProductDescription() |
| NULL                              | I               | setProductDescription("ZZZ")<br/>setProductDescription(null)<br/>getBarCode().equals(ZZZ)<br/><br/>-> true (not modified) | TestTicketEntryImpl.testSetProductDescription() |

## Method setAmount

**Criteria for method *setAmount*:**

- amount parameter is a valid int base type

**Predicates for method *setAmount*:**

| Criterion                                     | Predicate  |
| --------------------------------------------- | ---------- |
| Validity of int parameter                     | Valid      |

**Combination of predicates**:

| Validity of the int parameter     | Valid / Invalid | Description of the test case | JUnit test case |
| --------------------------------- | --------------- | ---------------------------- | --------------- |
| Valid                             | V               | setAmount(10)<br/>getAmount().equals(10)<br/><br/>-> true | TestTicketEntryImpl.testSetAmount() |

## Method setPricePerUnit

**Criteria for method *setPricePerUnit*:**

- pricePerUnit parameter is a valid double base type

**Predicates for method *setPricePerUnit*:**

| Criterion                                     | Predicate  |
| --------------------------------------------- | ---------- |
| Validity of double parameter                  | Valid      |

**Combination of predicates**:

| Validity of the double parameter  | Valid / Invalid | Description of the test case | JUnit test case |
| --------------------------------- | --------------- | ---------------------------- | --------------- |
| Valid                             | V               | setPricePerUnit(2.49)<br/>getPricePerUnit().equals(2.49)<br/><br/>-> true | TestTicketEntryImpl.testSetPricePerUnit() |

## Method setDiscountRate

**Criteria for method *setDiscountRate*:**

- pricePerUnit parameter is a valid double base type

**Predicates for method *setDiscountRate*:**

| Criterion                                     | Predicate  |
| --------------------------------------------- | ---------- |
| Validity of double parameter                  | Valid      |

**Combination of predicates**:

| Validity of the double parameter  | Valid / Invalid | Description of the test case | JUnit test case |
| --------------------------------- | --------------- | ---------------------------- | --------------- |
| Valid                             | V               | setDiscountRate(0.50)<br/>getDiscountRate().equals(0.50)<br/><br/>-> true | TestTicketEntryImpl.testSetDiscountRate() |

# Class ReturnTransactionImpl

## Method setReturnId
**Criteria for method *setReturnId*:**

- returnId parameter is a valid Integer object or null

**Predicates for method *setReturnId*:**

| Criterion                        | Predicate  |
| -------------------------------- | -----------|
|Validity of Integer parameter     | Valid      |
|                                  | NULL       |

**Combination of predicates**:


|  Validity of the Integer parameter| Valid / Invalid | Description of the test case | JUnit test case |
|-----------------------------------|-----------------|------------------------------|-----------------|
|Valid                              |V                |setReturnId(100)<br/>getReturnId( ).equals(100)<br/><br/>-> true|testReturnTransactionImpl.testSetReturnId()|
|NULL                               |I                |setRetutnId(12)<br/>setReturnId(null)<br/>getReturnId( ).equals(12)<br/><br/>-> true(not modified)|testReturnTransactionImpl.testSetReturnId( )|


## Method setState

**Criteria for method *setState*:**

- state parameter is a valid String object or null
- value of the String object is INPROGRESS, CLOSED or PAYED

**Predicates for method *setState*:**

| Criterion                                     | Predicate  |
| ----------------------------------------------| -----------|
|Validity of String parameter                   | Valid      |
|                                               | NULL       |
|Value of String parameter                      | Empty      |
|                                               | INPROGRESS |
|                                               | CLOSED     |
|                                               | PAYED      |
|                                               | Other value|

**Combination of predicates**:


|  Validity of the String parameter |Value of String Parameter| Valid / Invalid | Description of the test case | JUnit test case |
|-----------------------------------|-------------------------|-----------------|------------------------------|-----------------|
|NULL                               |*                        |I                |setState("INPROGRESS")<br/>setState(null)<br/>getState( ).equals("INPROGRESS")<br/><br/>-> true(not modified)|testReturnTransactionImpl.testSetState( )|
|Valid                              |Empty                    |I                |setState("")<br/><br/>-> exception thrown|testReturnTransactionImpl.testSetState( )|
|"                                 |INPROGRESS               |V                |setState("INPROGRESS")<br/>getState( ).equals("INPROGRESS)<br/><br/>-> true|testReturnTransactionImpl.testSetState( )|
|"                                  |CLOSED                   |V                |setState("CLOSED")<br/>getState( ).equals("CLOSED")<br/><br/>-> true|testReturnTransactionImpl.testSetState( )|
|"                                  |PAYED                    |V                |setState("PAYED")<br/>getState( ).equals("PAYED")<br/><br/>-> true|testReturnTransactionImpl.testSetState( )|
|"                                  |Other Value              |I                |setState("rAnDoM VaLuE")<br/><br/>-> exception thrown|testReturnTransactionImpl.testSetState( )|


## Method setAmount

**Criteria for method *setAmount*:**

- amount parameter is a valid Double object or null

**Predicates for method *setAmount*:**

| Criterion                                     | Predicate  |
| ----------------------------------------------| -----------|
|Validity of Double parameter                   | Valid      |
|                                               | NULL       |

**Combination of predicates**:


|  Validity of the Double parameter | Valid / Invalid | Description of the test case | JUnit test case |
|-----------------------------------|-----------------|------------------------------|-----------------|
|Valid                              |V                |setAmount(17.50)<br/>getAmount( ).equals(17.50)<br/><br/>->true|testReturnTransactionImpl.testSetAmount( )|
|NULL                               |I                |setAmount(22.30)<br/>setAmount(null)<br/>getAmount( ).equals(22.30)<br/><br/>-> true(not modified)|testReturnTransactionImpl.testSetAmount( )|

## Method setPaymentType

**Criteria for method *setPaymentType*:**

- paymentType parameter is a valid String object or null
- value of the String object is CARD or CASH

**Predicates for method *setPaymentType*:**

| Criterion                                     | Predicate  |
| ----------------------------------------------| -----------|
|Validity of String parameter                   | Valid      |
|                                               | NULL       |
|Value of String parameter                      | Empty      |
|                                               | CARD       |
|                                               | CASH       |
|                                               | Other value|

**Combination of predicates**:


|  Validity of the String parameter |Value of String Parameter| Valid / Invalid | Description of the test case | JUnit test case |
|-----------------------------------|-------------------------|-----------------|------------------------------|-----------------|
|NULL                               |*                        |I                |setPaymentType("CARD")<br/>setPaymentType(null)<br/>getPaymentType( ).equals("CARD")<br/><br/>-> true(not modified)|testReturnTransactionImpl.testSetPaymentType( )|
|Valid                              |Empty                    |I                |setPaymentType("")<br/><br/>-> exception thrown|testReturnTransactionImpl.testSetPaymentType( )|
|"                                  |CARD                     |V                |setPaymentType("CARD")<br/>getPaymentType( ).equals(null)<br/><br/>-> true|testReturnTransactionImpl.testSetPaymentType( )|
|"                                  |CASH                     |V                |setPaymentType("CASH")<br/>getPaymentType( ).equals(null)<br/><br/>-> true|testReturnTransactionImpl.testSetPaymentType( )|
|"                                  |Other Value              |I                |setPaymentType("sOmE sTuFF")<br/><br/>-> exception thrown|testReturnTransactionImpl.testSetPaymentType( )|


# Class BalanceOperationImpl

## Method setBalanceId
**Criteria for method *setBalanceId*:**

- balanceId parameter is a valid int base type

**Predicates for method *setBalanceId*:**

| Criterion                                     | Predicate  |
| ----------------------------------------------| -----------|
|Validity of int parameter                      | Valid      |

**Combination of predicates**:


|  Validity of the int parameter    | Valid / Invalid | Description of the test case | JUnit test case |
|-----------------------------------|-----------------|------------------------------|-----------------|
|Valid                              |V                |setBalanceId(27)<br/>getBalanceId( ).equals(27)<br/><br/>-> true|testBalanceOperationImpl.testSetBalanceId( )|

## Method setDate

**Criteria for method *setDate*:**

- date parameter is a valid LocalDate object or null

**Predicates for method *setDate*:**

| Criterion                                     | Predicate  |
| ----------------------------------------------| -----------|
|Validity of LocalDate parameter                | Valid      |
|                                               | NULL       |

**Combination of predicates**:


|Validity of the LocalDate parameter| Valid / Invalid | Description of the test case | JUnit test case |
|-----------------------------------|-----------------|------------------------------|-----------------|
|Valid                              |V                |LocalDate l = LocalDate.now( )<br/>setLocalDate(l)<br/>getLocalDate( ).equals(l)<br/><br/>-> true|testBalanceOperationImpl.testSetDate( )|
|NULL                               |I                |LocalDate l = LocalDate.now( )<br/>setLocalDate(l)<br/>setLocalDate(null)<br/>getLocalDate( ).equals(l)<br/><br/>-> true(not modified)|testBalanceOperationImpl.testSetDate( )|

## Method setMoney

**Criteria for method *setMoney*:**

- money parameter is a valid double base type

**Predicates for method *setMoney*:**

| Criterion                                     | Predicate  |
| ----------------------------------------------| -----------|
|Validity of double parameter                   | Valid      |

**Combination of predicates**:


|  Validity of the double parameter | Valid / Invalid | Description of the test case | JUnit test case |
|-----------------------------------|-----------------|------------------------------|-----------------|
|Valid                              |V                | setMoney(23.50)<br/>getMoney( ).equals(23.50)<br/><br/>-> true|testBalanceOperationImpl.testSetMoney( )|

## Method setType

**Criteria for method *setType*:**

- type parameter is a valid String object or null

**Predicates for method *setType*:**

| Criterion                                     | Predicate  |
| ----------------------------------------------| -----------|
|Validity of String parameter                   | Valid      |
|                                               | NULL       |

**Combination of predicates**:


|  Validity of the String parameter | Valid / Invalid | Description of the test case | JUnit test case |
|-----------------------------------|-----------------|------------------------------|-----------------|
|Valid                              |V                |setType("CREDIT")<br/>getType( ).equals("CREDIT")<br/><br/>-> true|testBalanceOperationImpl.testSetType( )|
|NULL                               |I                |setType("CREDIT")<br/>setType(null)<br/>getType( ).equals("CREDIT")<br/><br/>-> true(not modified)|testBalanceOperationImpl.testSetType( )|

# White Box Unit Tests

### Test cases definition
    
    <JUnit test classes must be in src/test/java/it/polito/ezshop>
    <Report here all the created JUnit test cases, and the units/classes under test >
    <For traceability write the class and method name that contains the test case>


| Unit name | JUnit test case |
|---------|-------|
| UserImpl | testUserImpl/testUserConstructorGetters() |
| ProductTypeImpl | testProductTypeImpl/testProductConstructorGetters() |


### Code coverage report

![UserImpl](./coverage_images/UserImpl_coverage.jpg)
![ProductTypeImpl](./coverage_images/ProductTypeimpl_coverage.jpg)


### Loop coverage analysis

    <Identify significant loops in the units and reports the test cases
    developed to cover zero, one or multiple iterations >

|Unit name | Loop rows | Number of iterations | JUnit test case |
|---|---|---|---|
| ProductTypeImpl | 74-77 | 0 | testProductImpl/testisValidCode() |
| |  | 1+ | testProductImpl/testisValidCode() |




