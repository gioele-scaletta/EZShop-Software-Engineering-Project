# Integration and API Test Documentation

Authors:

Date:

Version:

# Contents

- [Dependency graph](#dependency graph)

- [Integration approach](#integration)

- [Tests](#tests)

- [Scenarios](#scenarios)

- [Coverage of scenarios and FR](#scenario-coverage)
- [Coverage of non-functional requirements](#nfr-coverage)



# Dependency graph 

     <report the here the dependency graph of the classes in EzShop, using plantuml>
     
# Integration approach

    <Write here the integration sequence you adopted, in general terms (top down, bottom up, mixed) and as sequence
    (ex: step1: class A, step 2: class A+B, step 3: class A+B+C, etc)> 
    <Some steps may  correspond to unit testing (ex step1 in ex above), presented in other document UnitTestReport.md>
    <One step will  correspond to API testing>
    


#  Tests

   <define below a table for each integration step. For each integration step report the group of classes under test, and the names of
     JUnit test cases applied to them> JUnit test classes should be here src/test/java/it/polito/ezshop

## Step 1 - Unit testing
| Classes  | JUnit test cases |
|--|--|
|model.OrderImpl|unitTest.testOrderImpl|
|model.UserImpl|unitTest.testUserImpl|
|model.ProductTypeImpl|unitTest.testProductTypeImpl|

## Step 2 - EzShop private low level methods
| Classes  | JUnit test cases |
|--|--|
|||


## Step 3 -API test

| Classes  | JUnit test cases |
|--|--|
|EZShop| TestEzShop|



# Scenarios


<If needed, define here additional scenarios for the application. Scenarios should be named
 referring the UC in the OfficialRequirements that they detail>

##### Scenario UC1.4 Delete product

| Scenario |  Delete product type |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Product type X exists |
|  Post condition     | X deleted |
| Step#        | Description  |
|  1    |  C searches X via bar code |
|  2    |  C selects X's record |
|  3    |  C deletes X |
|  4   |  C confirms the update |

##### Scenario UC1.5 Get List of products

| Scenario |  List product types |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Product list is not empty |
|  Post condition     | list returned |
| Step#        | Description  |
|  1    |  C requests list of products|
|  2    |  C receives list of products |

##### Scenario UC1.6 Search a product

| Scenario |  List product types |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Product list is not empty |
|  Post condition     | list returned |
| Step#        | Description  |
|  1    |  C inserts barcode of product to be searched |
|1| C inserts the description (or part of it) of the products he is looking for |
|  2    |  C receives the product/products |

##### Scenario UC2.4 Get list of Users

| Scenario |  List Users |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Users list is not empty |
|  Post condition     | list returned |
| Step#        | Description  |
|  1    |  C requests list of users|
|  2    |  C receives list of users |

##### Scenario UC2.5 Search a user

| Scenario |  List product types |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Users list  is not empty |
|  Post condition     | list returned |
| Step#        | Description  |
|  1    |  C inserts id of user to be searched |
|  2    |  C receives the user |

##### Scenario UC3.4 Update product Quantity

| Scenario |  Delete product type |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Product type X exists |
|  Post condition     | X quantity updated |
| Step#        | Description  |
|  1    |  C searches X via bar code |
|  2    |  C selects X's record |
|  3    |  C modifies X's quantity |
|  4   |  C confirms the update |

##### Scenario UC3.5 Get lists of Orders

| Scenario |  List Orders |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Orders list is not empty |
|  Post condition     | list returned |
| Step#        | Description  |
|  1    |  C requests list of orders|
|  2    |  C receives list of orders |

##### Scenario UC4.5

| Scenario |  Delete customer record |
| ------------- |:-------------:| 
|  Precondition     | Account U for customer Cu existing |
|  Post condition     | Account U for customer Cu not existing |
| Step#        | Description  |
|  1    | User selects customer record U |
|  2    | U is deleted |

##### Scenario UC4.6

| Scenario |  List all customer records |
| ------------- |:-------------:| 
|  Precondition     | Customers list is not empty |
|  Post condition     | Customer list returned |
| Step#        | Description  |
|  1    | User requests list of customers |
|  2    | User receives list of customers |

##### Scenario UC4.7

| Scenario |  Modify points on a Loyalty card |
| ------------- |:-------------:| 
|  Precondition     | Account U for customer Cu existing |
|  | Loyalty card L attached to U |
|  Post condition     | Points on a Loyalty card L modified |
| Step#        | Description  |
|  1    |  User selects customer record U |
|  2    |  User modify points on L |

##### Scenario UC9.2

| Scenario |  Credit operation |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Sale operation O has occurred |
|  Post condition     | A Balance operation has been added to the application |
| Step#        | Description  |
|  1    |  The amount T (where T>0) of the operation O is calculated |
|  2    |  A Balance operation B with amount T is created |
|  3    |  B is added to the list of balance operations of the application |

##### Scenario UC9.3

| Scenario |  Debit operation |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Order/Return operation O has occurred |
|  Post condition     | A Balance operation has been added to the application |
| Step#        | Description  |
|  1    |  The amount T (where T<0)of the operation O is calculated |
|  2    |  A Balance operation B with amount T is created |
|  3    |  B is added to the list of balance operations of the application |

##### Scenario UC9.4

| Scenario |  Compute Balance |
| ------------- |:-------------:| 
|  Precondition     | Employee C exists and is logged in |
|  | Sale/Order/Return operation O has occurred |
|  Post condition     | Total Balance is Computed |
| Step#        | Description  |
|  1    |  Retrieve balance operation |
|  2    |  Retrieve amount of each balance operation and sum |
|  3    |  Return sum |


# Coverage of Scenarios and FR


<Report in the following table the coverage of  scenarios (from official requirements and from above) vs FR. 
Report also for each of the scenarios the (one or more) API JUnit tests that cover it. >




| Scenario ID | Functional Requirements covered | JUnit  Test(s) | 
| ----------- | ------------------------------- | ----------- | 
|  1.1      | FR3.1                             |  TestEzShop.testCreateProductType()           |        
|  1.2       | FR4.2                        |   TestEzShop.testUpdatePosition()          |
|  1.3        | "                          |   TestEzShop.testUpdateProduct()          |             
| 1.4        |   FR3.2                            |    TestEzShop.testDeeleteProductType()       |             
| 1.5        |        FR3.3                         |  TestEzShop.testGetAllProductTypes()           |             
| 1.6        |   FR3.4                              |   TestEzShop.testGetProductTypeByBarCode()          |             
|   "       |    "                         |   TestEzShop.testGetProductByDescription()          |             
|2.1|FR1.1|TestEzShop.testCreateUser()|
|2.2|FR1.2|TestEzShop.testDeleteUser()|
|2.3|FR1.5|TestEzShop.testUpdateUserRights()|
|2.4|FR1.3|TestEzShop.testGetAllUsers()|
|2.5|FR1.4|TestEzShop.testGetUser()|
|3.1|FR4.3|TestEzShop.testIssueOrder()|
|3.2|FR4.4|TestEzShop.testPayOrderFor()|
|"|FR4.5|TestEzShop.testPayOrder()|
|3.3|FR4.6|TestEzShop.testrecordOrderArrival()|
|3.4|FR4.1|TestEZShop.testUpdateQuanity()|
|3.5|FR4.7|TestEZShop.testGetAllOrders()|
|4.1|FR5.1|TestEZShop.testDefineCustomer()|
|4.2|FR5.5|TestEZShop.testCreateCard()|
|"|FR5.6|TestEZShop.testAttachCardToCustomer()|
|4.3|FR5.3|TestEZShop.testGetCustomer()|
|"|FR5.1|TestEZShop.testModifyCustomer()|
|4.4|FR5.3|TestEZShop.testGetCustomer()|
|"|FR5.1|TestEZShop.testModifyCustomer()|
|4.5|FR5.2|TestEZShop.testDeleteCustomer()|
|4.6|FR5.4|TestEZShop.testGetAllCustomers()|
|4.7|FR5.7|TestEZShop.testModifyPointsOnCard()|
|5.1|FR1.5|TestEZShop.testLogin()|
|5.2|"|TestEZShop.testLogout()|
|7.1|FR7.2|TestEZShop.testReceiveCreditCardPayment()|
|7.2|"|TestEZShop.testReceiveCreditCardPayment()|
|7.3|"|TestEZShop.testReceiveCreditCardPayment()|
|7.4|FR7.1|TestEZShop.testReceiveCashPayment()|
|9.1|FR8.3|TestEZShop.testGetDebitsAndCredits()|
|9.2|FR8.2|TestEZShop.testRecordBalanceUpdate()|
|9.3|FR8.1|TestEZShop.testRecordBalanceUpdate()|
|9.4|FR8.4|TestEZShop.testRecordBalanceUpdate()|
|10.1|FR7.4|TestEZShop.testReturnCreditCardPayment()|
|10.2|FR7.3|TestEZShop.testReturnCashPayment()|



# Coverage of Non Functional Requirements


<Report in the following table the coverage of the Non Functional Requirements of the application - only those that can be tested with automated testing frameworks.>


### 

| Non Functional Requirement | Test name |
| -------------------------- | --------- |
|  NFR2                      |  TestTiming   |


