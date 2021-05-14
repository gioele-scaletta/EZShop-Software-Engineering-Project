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
| Valid                            |  V                      | admin_role="Administrator"  <br />isAllowedRole(admin_role) <br /><br />-> true|                 |
| Invalid                          |  I                | admin_role="administrator"  <br /> isAllowedRole(admin_role) <br /><br />-> false                 |
| Empty                            |  I               |  admin_role=""  <br />isAllowedRole(admin_role) <br /><br />-> false                 |
| NULL                             |  I                |  admin_role=null  <br /> isAllowedRole(admin_role) <br /><br />-> false                 |




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
| Aministrator                          |  V                     |  user.setRole(admin_role)<br /> user.canManageUsers()<br /><br />-> true|     
| Cashier                               |  V               |  user.setRole(cashier_role) <br /> user.canManageUsers()<br /><br />-> false|
| ShopManager                           |  V               |  user.setRole(shop_manager_role) <br /> user.canManageUsers()<br /><br />-> false                 |


## Methods canManageProductList, canManageInventory, canManageAccounting

**Criteria for methods *canManageProductList, canManageInventory, canManageAccounting*:**

- Value of field role

**Predicates for methods *canMan,ageProductList, canManageInventory, canManageAccounting*:**

| Criterion                        | Predicate |
| -------------------------------- | --------- |
|Value of field role               | Administrator |
|                                  | Cashier   |
|                                  | ShopManager  |

**Combination of predicates**:

|  Validity of the String parameter | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
| Aministrator                          |  V                     |  user.setRole(admin_role)<br /><br /> user.canManageProductList()<br /> user.canManageInventory()<br />user.canManageAccounting()<br /><br />-> true|     
| Cashier                               |  V               |  user.setRole(cashier_role) <br /><br /> user.canManageProductList()<br /> user.canManageInventory()<br />user.canManageAccounting()<br /><br />-> true|
| ShopManager                           |  V               |  user.setRole(shop_manager_role) <br /><br /> user.canManageProductList()<br /> user.canManageInventory()<br />user.canManageAccounting()<br /><br />-> false                 |



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
| Aministrator                          |  V                     |  user.setRole(admin_role)<br /><br /> user.canManageCustomers()<br /> user.canManageSaleTransactions()<br />user.canManagePayments()<br /><br />-> true|     
| Cashier                               |  V               |  user.setRole(cashier_role) <br /><br /> user.canManageCustomers()<br /> user.canManageSaleTransactions()<br />user.canManagePayments()<br /><br />-> true| 
| ShopManager                           |  V               |  user.setRole(shop_manager_role) <br /><br /> user.canManageCustomers()<br /> user.canManageSaleTransactions()<br />user.canManagePayments()<br /><br />-> true| 



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
| Validity of length               | >12 and <14 |
|                                  | <12 or >14   |
| Validity of digits type         | is parsable Long   |
|                                  |nor parsable Long |
|Validity of digits sum            | Closest higher tens to sum calculation (x1 and x3), minus sum is equal to 3 |
|Validity of String parameter      | null|
|                                  |Empty|

**Combination of predicates**:


|  Validity of the String parameter |  Validity of length| Validity of digits type | Validity of digits sum  | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|-------|-------|-------|
| Valid    |   >12 and <14     | is  parsable   | Valid   |  V                      | isValidCode("5701234567899") <br /><br />-> true         
| *  |   *      |  *        | Invalid |  I                | isValidCode("111111111111") <br /><br />-> false                 |
| *  |   *       |  is not parsable        | Invalid |  I                | isValidCode("5701234a67899") <br /><br />-> false                 |
| *  |   >12 and <14       |  *     | * |  I                | isValidCode("570167899") <br /><br />-> false 
|Empty |   *      |  *       | * |  I                |isValidCode("")  <br /><br />-> false                 |                |
|null |   *      |  *       | * |  I                | isValidCode(null)  <br /><br />-> false 


## Method isValidLocation
## Method updateProductQuantity








# White Box Unit Tests

### Test cases definition
    
    <JUnit test classes must be in src/test/java/it/polito/ezshop>
    <Report here all the created JUnit test cases, and the units/classes under test >
    <For traceability write the class and method name that contains the test case>


| Unit name | JUnit test case |
|--|--|
|||
|||
||||

### Code coverage report

    <Add here the screenshot report of the statement and branch coverage obtained using
    the Eclemma tool. >


### Loop coverage analysis

    <Identify significant loops in the units and reports the test cases
    developed to cover zero, one or multiple iterations >

|Unit name | Loop rows | Number of iterations | JUnit test case |
|---|---|---|---|
|||||
|||||
||||||



