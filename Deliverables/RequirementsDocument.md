# Requirements Document 

Authors: Jose Antonio Antona Diaz, Giuseppe D'Andrea, Marco Riggio, Gioele Scaletta

Date: 16/04/21

Version: 0.1.1

# Contents

- [Essential description](#essential-description)
- [Stakeholders](#stakeholders)
- [Context Diagram and interfaces](#context-diagram-and-interfaces)
	+ [Context Diagram](#context-diagram)
	+ [Interfaces](#interfaces) 
	
- [Stories and personas](#stories-and-personas)
- [Functional and non functional requirements](#functional-and-non-functional-requirements)
	+ [Functional Requirements](#functional-requirements)
	+ [Non functional requirements](#non-functional-requirements)
- [Use case diagram and use cases](#use-case-diagram-and-use-cases)
	+ [Use case diagram](#use-case-diagram)
	+ [Use cases](#use-cases)
    	+ [Relevant scenarios](#relevant-scenarios)
- [Glossary](#glossary)
- [Deployment diagram](#deployment-diagram)

# Essential description

Small shops require a simple application to support the owner or manager. A small shop (ex a food shop) occupies 50-200 square meters, sells 500-2000 different item types, has one or a few cash registers 
EZShop is a software application to:
* manage sales
* manage inventory
* manage customers
* support accounting


# Stakeholders


| Stakeholder name  | Description | 
| ----------------- |:-----------|
|   Customer     	|    He's indirectly involved in the software usage. He's interested in a smooth shop experience, and for this purpose, he has to rely on a staff that is equipped with a software that can support all its requests.         |
|   Cashier     	|   He manages the shop sells at the cash desk. He's interested in having a reliable and intuitive software that supports his daily job.          |
| Warehouse Worker	| He's in charge of managing and working in the warehouse. He's interested in keeping all the data of the warehouse in a detailed and accurate way, and the possibility to order products easily from the suppliers.            |
|   Shop Owner    	| He's the buyer of the software. He needs to have the most control on every aspect of his shop, and for this reason he needs a complete application for him and his employees, and to keep track of the revenue and expenditure of its shop (accounting functionalities)            |
|   IT Support    	| He's responsible of supporting the shop needs by means of IT tools. He's the maintainer of the software.            |
|   Section Head    |  He manages a section of the shop. He needs reliable data about his section, to support the daily job and the statistical analysis of the purchases. He's also interested in having the possibility of giving/removing rights to his subordinates           |
|   Shelf Stocker   |   He's the person in charge of setting up the shelves and assist to the customer's requests in aisles. He needs a software that helps him in his daily tasks and increment his productivity.          |
| Business Manager  |  It manages the financial situation of the shop. He has to be informed of the shop expenditures and revenues. He has to take care of tax regulator declaration. It may coincide with the shop owner.           |
|   Supplier    	|   It supplies the shop with the products. It has to send to the shop the DBs with the orderable products and it has to notify the shop of the effective availability of products.          |
|   POS    			|   An electronic device that allows to interface with payment cards to make electronic funds transactions.|
|   Cash Register   |   The machine at the cash desk that registers sales, prints receipts and stores money. It is at the cash desk. |
|   Barcode Reader  |   The device that allows to reading barcodes on products or shipping order in entrance. It can be a scanner device or a smartphone.        |
|   Fidelity Card   |   A card that allows customers to participate to fidelity programs of the shop by accumulating points, access to special discounts and exchanging points for rewards.|

# Context Diagram and interfaces

## Context Diagram
```plantuml
left to right direction
 
actor "User" as u
actor "Shop Owner\n    (Admin)" as so
actor "Cashier" as c
actor "Warehouse Worker" as ww
actor "Shelf Stocker" as ss
actor "Barcode Reader" as br
actor "Cash Register" as cr
actor "POS" as p
actor "Supplier" as s
 
rectangle System {
    usecase EZShop
}
 
so --|> u
c --|> u
ww --|> u
ss --|> u
 
u -- EZShop
EZShop -- br
EZShop -- cr
EZShop -- p
s - EZShop
```

## Interfaces
| Actor 			| Logical Interface | Physical Interface  	|
| ------------------|:------------------|:----------------------|
|   Cashier     	|GUI				|Screen and mouse/keyboard on PC, Touchscreen on smartphone |
|   Shop Owner     	|GUI				|Screen and mouse/keyboard on PC, Touchscreen on smartphone |
|   Warehouse Worker|GUI				|Screen and mouse/keyboard on PC, Touchscreen on smartphone |
|   Shelf Stocker   |GUI 				|Screen and mouse/keyboard on PC, Touchscreen on smartphone |
|   Supplier     	|Web Service		|Internet connection 										|
|   Barcode Reader  |API  				|Local Area Network  										|
|   Cash Register   |API  				|Local Area Network  										|
|   POS     		|API				|Wire/Wireless/Telephone line  								|


# Stories and personas
Meredith is 25, she works every day until 7pm and her schedule is packed with work demands, so she usually visits shops between her obligations or just before closing time. She isn't affectionate to a particular supermarket chain, so she chooses from time to time where to go for her daily shop and usually goes to the most convenient one that offers her the best discounts. In the shop, she wants to go directly to the products she's interested in, and usually asks for help to the shop's employee, that can give her fast and precise support.

Lucy is 70, she is retired, she usually goes grocery shopping every Wednesday and Saturday. She usually prepares a list, and often organizes it by store section. She also visits most aisles looking for items she may have forgotten, getting meal ideas, and stocking up discounted products. She is an affectionate and regular customer who also likes to take advantage of the fidelity program offered by the shop. She would never change her favorite shop.

Josh is 27, he is a cashier, he wants to offer the best possible service even during rush hours when there are a lot of customers in queue at the cash desk. He wants to handle all payments and customer subscriptions as fast and smoothly as possible, without having to deal with unexpected setbacks that could delay the process and make the customers unhappy.

Chadwick is 65, he has been the inventory manager of a shop since he was 35. He is a bit reluctant to change and not very good with computers, but he is the best at his job. His tasks include updating the current inventory availability when new orders arrive and keeping track of their product position, both in shop's aisles or warehouse sections.

Amy is 30, she is working as an IT support specialist. Her role is to help whichever employee that has issues with IT and to solve technical issues that can arise in the application. She wants to be able to help the employee effectively and without having to waste too much of hers and employee’s time. When some technical issues arise, she would like to be able to solve most of them remotely.

Henry is 36, he is the manager of the shop. He mostly needs to have access to the accounting and sales information to perform his management duties. However, he also wants to have access to all the other sections too. As a matter of fact, he likes analytics and he always wants to have everything under control in order to make his decision by having simpler and more aware processes.

# Functional and non functional requirements

## Functional Requirements

| ID        	| Description  	|
| ------------- |:-------------:| 
|  FR1     		|Authenticate an user|
|  FR1.1   		|Log in to the system|
|  FR1.2   		|Log out to the system|
|  FR2   		|Manage an user|
|  FR2.1   		|Create/delete user|
|  FR2.2   		|Manage user permissions|
|  FR3     		|Manage sales|
|  FR3.1   		|Add sale|
|  FR3.2   		|Modify sale|
|  FR3.3   		|Delete sale|
|  FR3.4   		|Show current transaction items|
|  FR3.5   		|Manage different payment methods|
|  FR4   		|Manage inventory|
|  FR4.1   		|Add product (by barcode, manually|
|  FR4.2   		|Modify product information (i.e. name, brand, price, warehouse/shelf, discount …)|
|  FR4.3   		|Delete product|
|  FR4.4   		|Order from suppliers (manually or automatically with triggers)|
|  FR5   		|Manage customer|
|  FR5.1   		|Add customer via Fidelity Card|
|  FR5.2   		|Modify customer (i.e. name, surname, email address, ...)|
|  FR5.3   		|Delete customer and Fidelity Card|
|  FR5.4   		|Add/Remove points to Fidelity Card|
|  FR6   		|Support accounting|
|  FR6.1   		|Compute total sales depending on actual filters|
|  FR6.2   		|Compute total profit depending on actual filters|
|  FR6.3   		|Filter sales|
|  FR6.4 		|Generate financial report|

## Non Functional Requirements

| ID        	| Type (efficiency, reliability, ..)           | Description  | Refers to |
| ------------- |:-------------:| :-----:| -----:|
|  NFR1     	| Performance	| All functions should have response time < 0.5 sec  | |
|  NFR2     	| Usability		| Implementation of intuitive GUI | |
|  NFR3     	| Portability	| Responsive GUI for smartphone usage| |
|  NFR4 		| Localization	| Add different currencies and separators| |
|  NFR5 		| Usability		| Remote IT support| | 


# Use case diagram and use cases


## Use case diagram
```plantuml
actor "Shop Owner" as so
actor "Cashier" as c
actor "Warehouse Worker" as ww
actor "Shelf Stocker" as ss
actor "Barcode Reader" as br
actor "Cash Register" as cr
actor "Supplier" as s
 
rectangle EZShop {
    usecase "Authenticate an user" as F1
    usecase "Manage an user" as F2
    usecase "Manage sales" as F3
    usecase "Manage inventory" as F4
    usecase "Reorder" as F4.4
    usecase "Manage customers" as F5
    usecase "Support accounting " as F6
}
 
F1 <.. F2 : <<include>>
F1 <.. F3 : <<include>>
F1 <.. F4 : <<include>>
F1 <.. F5 : <<include>>
F1 <.. F6 : <<include>>
 
F4.4 ..> F4 : <<extend>>
 
F2 <-- so
 
F3 <-- c
F3 --> cr
F3 ---> br
 
F4 ---> br
F4 <-- ww
F4 <-- ss
 
F4.4 -> s
 
F5 <-- c
F5 ---> br
 
F6 <-- so
```

### Use case 1, UC1 - Authenticate an user
| Actors Involved        | Shop Owner, Cashier, Warehouse Worker, Shelf Stocker |
| ------------- |:-------------:| 
|  Precondition     | The user has an account |  
|  Post condition     | The user can use the system |
|  Nominal Scenario     | The user inserts username and password; the system checks the credentials; the system authorizes the user |
|  Variants     | Username and/or password are incorrect; the user have to follow the reset procedure |

##### Scenario 1.1 

\<describe here scenarios instances of UC1>

\<a scenario is a sequence of steps that corresponds to a particular execution of one use case>

\<a scenario is a more formal description of a story>

\<only relevant scenarios should be described>

| Scenario 1.1 | |
| ------------- |:-------------:| 
|  Precondition     | \<Boolean expression, must evaluate to true before the scenario can start> |
|  Post condition     | \<Boolean expression, must evaluate to true after scenario is finished> |
| Step#        | Description  |
|  1     |  |  
|  2     |  |
|  ...     |  |

##### Scenario 1.2

##### Scenario 1.x

### Use case 2, UC2 - Manage an user
| Actors Involved        | Shop Owner |
| ------------- |:-------------| 
|  Precondition     | The admin has logged in to the system |  
|  Post condition     | The system users are updated |
|  Nominal Scenario     | The admin adds a new user; the admin sets the permissions of the new user |
|  Variants     | The admin changes the permissions of an user |
||The admin deletes an user|

##### Scenario 2.1 

### Use case 3, UC3 - Manage sales
| Actors Involved        | Cashier, Cash Register, Barcode Reader |
| ------------- |:-------------| 
|  Precondition     | The cashier has an account |
||The cashier has logged in to the system|
||The cash register is connected to the system|
||The barcode reader is connected to the system|  
|  Post condition     | The sale information is uploaded to the system |
|  Nominal Scenario     | The cashier scans the barcode of the items with the barcode reader; the cash register calculates the total amount of the sale; the cashier selects the payment method based on customer's needs; the transaction is executed|
|  Variants     | The customer has a fidelity card; the cashier scans the barcode of the fidelity card with the barcode reader; the balance of the fidelity card is updated based on the products purchased|
||The barcode of an item is unreadable; the cashier manually enters the bar code|
||The customer provides an expired/blocked credit/debit card; the sale must be cancelled|

##### Scenario 3.1 

### Use case 4, UC4 - Manage inventory
| Actors Involved        | Shop Owner, Cashier, Warehouse Worker, Shelf Stocker |
| ------------- |:-------------| 
|  Precondition     | The shelf stocker has an account |
||The shelf stocker has logged into the system|
||The warehouse worker has an account|
||The warehouse worker has logged into the system|
||The barcode reader is connected to the system|  
|  Post condition     | The inventory information is uploaded to the system |
||The order list is uploaded|
|  Nominal Scenario     | Warehouse worker receives and stores products from supplier;  when necessary, the shelf stocker will take the products from the warehouse to the supermarket shelves; products on the shelf are purchased; products are deleted from the system when cashier scans the barcode of the products with the barcode reader |
|  Variants     | Product information needs to be updated |
||If the quantity of a product is under a certain threshold an order is sent to the supplier|

##### Scenario 4.1 

### Use case 5, UC5 - Manage customers
| Actors Involved        | Cashier, Barcode Reader |
| ------------- |:-------------| 
|  Precondition     | The cashier has an account |
||The cashier has logged into the system| 
|  Post condition     | Customer information is uploaded to the system |
|  Nominal Scenario     | The cashier adds a customer to the system and provides him a fidelity card; the customer uses his fidelity card to make a purchase; the cashier scans the barcode of the fidelity card with the barcode reader; points are added to the fidelity card based on the products purchased|
|  Variants     | Customer information needs to be updated|
||The barcode of the fidelity card is unreadable; the cashier manually enters the barcode|
||The customer wants to use the points stored in his fidelity card; these points are removed from the card|
||The customer wants to unsubscribe from the system; the fidelity card is removed|

##### Scenario 5.1 

### Use case 6, UC6 - Support accounting
| Actors Involved        | Shop Owner |
| ------------- |:-------------| 
|  Precondition     | The shop owner has an account |
||The shop owner has logged into the system|
|  Post condition     | The shop owner can use some taxes and financial instruments |
|  Nominal Scenario     | The shop owner reviews some statistics |
|  Variants     | Different filters are used depending on the needs of the owner for each moment |

##### Scenario 6.1 

# Glossary

| Term        | Definition |
| ------------- |:-------------| 
|Accounting Tab|The section of the application where is possible to view, filter, edit and delete data related to transactions and countability|
|Admin|A user with all set permission. This role can create and grant permissions to users. The first created user by the application has admin permissions. Usually the shop owner is an admin|
|Barcode Reader|The instrument capable of reading barcodes on products and shipments. It can be an external device or a smartphone|
|Cash Desk|The shop's physical location where customers goes to pay for products and cashiers register sales|
|Cash Register|The electronic device composed by a drawer|
|Cashier|Role in charge of registering sales at the cash desk|
|Customer|Person that has a stake in buying products from the shop|
|Customers Tab|The section of the application where is possible to view, filter, edit and delete data related to customers and their fidelity card|
|European Article Number (EAN)|A 8 or 13 digits code that identifies univocally products on the European market|
|Fidelity Card|A card linked to a single customer that allows for accumulating point for the customer and collecting data from him for the shop|
|Inventory|The entire set of products owned by the shop, both on shelves and in the warehouse|
|Inventory Tab|The section of the application where is possible to view, display, edit and delete products from the inventory, order from suppliers|
|Navigation Bar|The bar in the top of application where is possible to see which tab is currently displayed switch tab and access IT Support|
|Shipping Order|An order of purchase made from the shop to the supplier for a quantity of products|
|Permission|A privacy setting that can be granted by an admin to a user, that allow to a specific user to view and use tabs and/or functionalities of the program. They're set in the users tab|
|Point Of Sale (POS)|Electronic device connected to the application that allows for payments via credit card|
|Product|Good that is stock in inventory and sold to customers. They're identified univocally by a EAN code|
|Sale|Commercial transaction between customer and retailer, where the customer buys a quantity of products from the retailer|
|Sales tab|The section of the application where is possible to register, edit, view and delete sales|
|Shelf|The set of products in the inventory that are visible in the shop to the customers and available for selling|
|Supplier|External company/society that sells products to the shop for reselling. They receive shipping orders as an input from the shop|
|IT Support|The support function offered by the software house to admins of our application. It can be used through the apposite button in the navigation bar|
|User|An authorized person that can access to the application with a username and a password, with set permissions to access specific functionalities of the application|
|Users tab|The section of the application where admins can create and delete users, grant or remove them permissions, and see their login history|
|Warehouse|The set of products that are not visible to the customers and not available for selling, and are stocked outside|

# Deployment Diagram 



```plantuml
skinparam defaultTextAlignment center
 
artifact "EZShop Application" as EZShop
 
node "Application\nServer" as s
node "PC\nClient" as c1
node "Smartphone\nClient" as c2
 
EZShop -[dotted]-> s : <<deploy>>
s -- "*" c1 : LAN
s -- "*" c2 : LAN
```


