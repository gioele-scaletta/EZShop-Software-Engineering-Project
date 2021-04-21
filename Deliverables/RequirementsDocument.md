# Requirements Document 

Authors: Jose Antonio Antona Diaz, Giuseppe D'Andrea, Marco Riggio, Gioele Scaletta

Date: 21/04/21

Version: 1.5

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
|   Customer     	|    He is indirectly involved in the software usage. He expects a smooth shop experience, which relies on the staff and his equipment. Including the software.         |
|   Cashier     	|   He manages the shop sales at the cash desk. He wants to have a reliable and intuitive software that supports his daily job.          |
| Warehouse Worker	| He is in charge of managing the warehouse. He is interested in keeping all the data of the warehouse in a detailed and accurate way, as well as to order products easily from the suppliers.            |
|   Shop Owner    	| He is the buyer of the software. He needs to control every aspect of his shop, and for this reason, he needs a complete application for him and his employees. He takes care of keeping track of the revenue and expenditure of its shop.          |
|   Section Head    |  He manages a section of the shop. He needs reliable data about his section to support his daily job and the statistical analysis of the purchases. He is also interested in having the possibility of giving/removing rights to his subordinates. This figure may coincide with the shop owner or the shelf stocker depending on the shop.        |
|   Shelf Stocker   |   He is the person in charge of setting up the shelves and assist to the customer's requests in aisles. He needs a software that helps him in his daily tasks and increment his productivity.          |
| Business Manager  |  It manages the financial situation of the shop. He has to be informed of the shop expenditures and revenues. He has to take care of tax regulator declaration. It may coincide with the shop owner.           |
|   Supplier    	|   It supplies the shop with the products. It receives orders from the shop via email.          |
|   POS    			|   An electronic device that allows to interface with payment cards to make electronic funds transactions.|
|   Cash Register   |   The machine at the cash desk that registers sales, prints receipts and stores money. It is at the cash desk. |
|   Barcode Reader  |   The device that allows reading EAN barcodes of products and fidelity cards. It can be a scanner device or a smartphone.        |
|   Fidelity Card   |   A card that allows customers to participate to fidelity programs of the shop by accumulating points and accessing to special discounts. They can be read via barcode reader. They can be printed by a card printer or be bought from card sellers by the shop owner.|
|Card Printer| An electronic device that can print fidelity cards.|

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
actor "Card Printer" as cp
actor "Supplier" as s
actor "Business Manager" as bm
 
rectangle System {
    usecase EZShop
}
 
so --|> u
c --|> u
ww --|> u
ss --|> u
bm --|> u
 
u -- EZShop
EZShop -- br
EZShop -- cr
EZShop -- p
EZShop -- cp
s - EZShop
```

## Interfaces
| Actor 			| Logical Interface | Physical Interface  	|
| ------------------|:------------------|:----------------------|
|   Cashier     	|GUI				|Screen and mouse/keyboard on PC, Touchscreen on smartphone |
|   Shop Owner     	|GUI				|Screen and mouse/keyboard on PC, Touchscreen on smartphone |
|   Warehouse Worker|GUI				|Screen and mouse/keyboard on PC, Touchscreen on smartphone |
|   Shelf Stocker   |GUI 				|Screen and mouse/keyboard on PC, Touchscreen on smartphone |
|   Business Manager|GUI 				|Screen and mouse/keyboard on PC, Touchscreen on smartphone |
|   Supplier     	|Web Service		|Internet connection 										|
|   Barcode Reader  |API  				|Wire/Local Area Network  					     			|
|   Cash Register   |API  				|Wire                   									|
|   POS     		|API				|Wire/Wireless/Telephone line  								|
|   Card Printer	|API				|Wire								                        |


# Stories and personas
Meredith is 25, she works every day until 7pm and her schedule is packed with work demands, so she usually visits shops between her obligations or just before closing time. She isn't affectionate to a particular supermarket chain, so she chooses from time to time where to go for her daily shop. Usually she goes to the most convenient shop that offers her the best discounts. 

Lucy is 70, she is retired, she usually goes grocery shopping every Wednesday and Saturday. She usually prepares a list, and often organizes it by store section. She also visits most aisles looking for items she may have forgotten, getting meal ideas, and stocking up discounted products. She is an affectionate and regular customer who also likes to take advantage of the fidelity program offered by the shop. She would never change her favorite shop.

Josh is 27, he is a cashier, he wants to offer the best possible service even during rush hours when there are a lot of customers queuing up at the cash desk. He wants to handle all payments and customer subscriptions as fast and smoothly as possible, without having to deal with unexpected setbacks that could delay the process and make the customers unhappy.

Chadwick is 65, he has been the inventory manager of a shop since he was 35. He is a bit reluctant to change and not very good with computers, but he is the best at his job. His tasks include updating the current inventory availability when new orders arrive and keeping track of their product position, both in shop's aisles or warehouse sections.

Henry is 36, he is the owner of the shop. His employees are his bigger resource. For this reason he wants to be sure that all his employees have the tools to perform their daily job in the most efficient way. So he needs a reliable application that supports all of the shop's processes and allows him to assign different roles to his employees with flexibility. He also wants to have a general overview of the accounting situation. 

Jordan is 45, he is the business manager of the shop. He mostly needs to have access to the accounting and sales information to perform his management duties. As a matter of fact, he likes analytics and he always wants to have everything under control in order to make his decision by having simpler and more aware processes. He wants to have a document ready to send to the tax regulator agency for fiscal declarations.

# Functional and non functional requirements

## Functional Requirements

| ID        	| Description  	|
| ------------- |:-------------:| 
|  FR1     		|Authenticate an user|
|  FR1.1   		|Log in to the system|
|  FR1.2   		|Log out to the system|
|  FR2   		|Manage an user|
|  FR2.1   		|Create user|
|  FR2.2   		|Delete user|
|  FR2.3   		|Manage user permissions|
|  FR3     		|Manage sales|
|  FR3.1   		|Add sale|
|  FR3.2   		|Modify sale|
|  FR3.3   		|Delete sale|
|  FR3.4   		|Show current transaction items|
|  FR3.5   		|Manage different payment methods|
|  FR4   		|Manage inventory|
|  FR4.1   		|Add product|
|  FR4.2   		|Modify product information|
|  FR4.3   		|Delete product|
|  FR4.4   		|Order from suppliers (manually or automatically with triggers)|
|  FR5   		|Manage customer|
|  FR5.1   		|Add customer with Fidelity Card|
|  FR5.2   		|Modify customer information|
|  FR5.3   		|Delete customer and Fidelity Card|
|  FR5.4   		|Add/Remove points to Fidelity Card|
|  FR6   		|Support accounting|
|  FR6.1   		|Display accounting analytics based on the actual filters|
|  FR6.2   		|Filter sales|
|  FR6.3 		|Generate accounting report|

## Non Functional Requirements

| ID        	| Type (efficiency, reliability, ..)           | Description  | Refers to |
| ------------- |:-------------:| :-----:| -----:|
|  NFR1     	| Performance	| All functions should have response time < 0.5 sec  | All FR |
|  NFR2     	| Usability		| Implementation of intuitive GUI | All FR |
|  NFR3     	| Portability	| Responsive GUI for smartphone usage| FR4 |
|  NFR4 		| Localization	| Add different currencies and separators| FR3, FR4 and FR6 |
|  NFR5 		| Usability		| Remote IT support| All FR |
|  NFR6         | Security      | Only authorized users can access the application. Safety from hacker attacks| All FR | 
|  NFR7         | Localization  | Multi-language support | All FR |

# Use case diagram and use cases


## Use case diagram
```plantuml
actor "Cash Register" as cr
actor "Shop Owner" as so
actor "Business Manager" as bm
actor "Cashier" as c
actor "Warehouse Worker" as ww
actor "Shelf Stocker" as ss
actor "Barcode Reader" as br
actor "Card Printer" as cp
actor "Supplier" as s

rectangle EZShop {
    usecase "Authenticate an user" as F1
    usecase "Manage sales" as F3
    usecase "Manage customers" as F5
    usecase "Manage inventory" as F4
    usecase "Support accounting " as F6
    usecase "Manage an user" as F2
}
 
F1 <.. F2 : <<include>>
F1 <.. F3 : <<include>>
F1 <.. F4 : <<include>>
F1 <.. F5 : <<include>>
F1 <.. F6 : <<include>>
 
F2 <-- so

F3 <--- c
F3 --> cr
F3 ----> br
 
F4 ----> br
F4 <--- ww
F4 <--- ss
F4 --> s
 
F5 <--- c
F5 --> cp
F5 ----> br
 
F6 <-- bm
F6 <-- so
```

```plantuml


(Manage an user) as (user)


(Manage inventory) as (inv) 


(Manage customers) as (Mc) 


(New order) as (nor)


(Create new User) as (Cu)


(Modify user) as (Mu)


(Register incoming shipment) as (Ns)
(Product information management) as (Mpi)
(Order Products) as (ws)
(Create new Loyalty card) as (nlc)
(Modify Loyalty card) as (mlc)



user ..> Cu : <<include>>
user .> Mu : <<include>>


inv .> Ns : <<include>>
inv ..> Mpi : <<include>>
inv ..> ws : <<include>>
Mc .> nlc : <<include>>
Mc ..> mlc : <<include>>
nor ..> Mpi : <<extend>>
```

## Use cases

### Use case 1, UC1 - Authenticate an user
| Actors Involved       | Shop Owner, Cashier, Warehouse Worker, Shelf Stocker, Business Manager |
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | A person has a user profile in the application |  
|  Post condition       | The person is logged in the application and can use its functionalities |
|  Nominal Scenario     | The user inserts username and password; the application checks the credentials; the application authorizes the user |
|  Variants             | Username and/or password are incorrect; the user has to try again |

##### Scenario 1.1 - User Log In
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The person has a user profile |  
|  Post condition       | The person can use the application |
|  Step #               | Description |
|  1                    | The application is accessed via Web Browser and a login screen prompts|
|  2                    | User's username is inserted|
|  3                    | Correct user password is inserted|
|  4                    | Click on "Log in"|
|  5                    | Home menu is displayed (or directly a tab, if the user can access only one)|

##### Scenario 1.2 - Failed User Log In
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | None |  
|  Post condition       | Access to the application is forbidden |
|  Step #               | Description |
|  1                    | The application is accessed via Web Browser and a login screen prompts|
|  2                    | User's username is inserted. It can exist or not on the users list|
|  3                    | Wrong user password is inserted|
|  4                    | Click on "Log in"|
|  5                    | An error is displayed on the screen. Click on "Ok" to return to the login page|

### Use case 2, UC2 - Edit a user profile
| Actors Involved       | Shop Owner, Business Manager |
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The admin is logged in to the application |  
|  Post condition       | User information is modified |
|  Nominal Scenario     | The admin changes the information about a user |
|  Variants             | The admin deletes a user|

##### Scenario 2.1 - Edit a user profile
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | A user with "admin" permission set is logged in to the application (scenario 1.1) |
|                       | A person has a user profile (Scenario 3.1)|
|  Post condition       | A user profile has been edited |
|  Step #               | Description |
|  1                    | Access the users tab|
|  2                    | Search for an user's username on the search bar|
|  3                    | Click on the user profile displayed in the users list|
|  4                    | Change the user's username and password|
|  5                    | Change permission's flags|
|  6                    | Click on "Confirm"|
|  7                    | Success page is prompted. Click on "Ok"|

##### Scenario 2.2 - Delete a user profile
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | A user with "admin" permission set is logged in to the application (scenario 1.1) |
|                       | A person has a user profile (Scenario 3.1)|
|  Post condition       | A user profile has been deleted |
|  Step #               | Description |
|  1                    | Access the users tab|
|  2                    | Search for an user's username on the search bar|
|  3                    | Click on the user profile displayed in the users list|
|  4                    | Click on "Delete"|
|  5                    | Click on "Yes" in the confirmation prompt|
|  6                    | Success page is prompted. Click on "Ok"|

### Use case 3, UC3 - Create a new user profile
| Actors Involved       | Shop Owner, Business Manager |
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The admin is logged in to the application |  
|  Post condition       | A new user profile is created and added to the application |
|  Nominal Scenario     | The admin adds a new user to the application; its permissions are set |

##### Scenario 3.1 - Create a user
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | A user with "admin" permission set is logged in to the application (scenario 1.1) |
|  Post condition       | A new user profile is created and added to the application |
|  Step #               | Description |
|  1                    | Access the users tab|
|  2                    | Click on "New user"|
|  3                    | Insert new user's username|
|  4                    | Insert new user's password|
|  5                    | Set permission's flags|
|  6                    | Click on "Confirm"|
|  7                    | Success page is prompted. Click on "Ok"|

### Use case 4, UC4 - Manage sales
| Actors Involved       | Cashier, Cash Register, Barcode Reader, POS, Fidelity Card |
| ----------------------|:---------------------------------------| 
|  Precondition         | The cashier has a user profile and is logged in to the application |
|                       | The cash register is connected to the application|
|                       | The barcode reader is connected to the application|  
|  Post condition       | The sale information is uploaded to the application |
|  Nominal Scenario     | The cashier scans the EAN barcode of products with the barcode reader; the cash register calculates the total amount of the sale; the cashier selects the payment method based on customer's needs; the transaction is executed|
|  Variants             | The customer has a fidelity card; the cashier adds a fidelity card to the sale; the balance of points on the fidelity card is updated based on the products purchased|
|                       |The EAN barcode of an item is unreadable; the cashier manually enters the EAN barcode|
|                       |The customer provides an expired/blocked credit/debit card; the cashier goes back to the checkout and tries a new card or deletes the sale if no more cards are available|
|                       |The customer pays by cash|
|                       |The customer pays by credit card|

##### Scenario 4.1 - Checkout at the cash desk
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The cashier has an user profile with "sales" set permission and is logged into the application (scenario 1.1) |
|                       | The cash register is connected to the application|
|                       | The barcode reader is connected to the application|
|                       | The POS is connected to the application|
|  Post condition       | The sale information is uploaded to the application |
|  Step #               | Description |
|  1                    | Access the sales tab|
|  2                    | Click on "New Sale"|
|  3                    | Scan the EAN barcode of product X (If product EAN barcode is unreadable by the barcode reader, click on "Add manually", insert EAN Code and the quantity and presses "ok" button)|
|                       | Repeat step 3 for N products|
|  4                    | Click on "Checkout"|
|  5                    | Click on "Scan" to add a fidelity card if present (or if the add it manually by clicking on "Add manually" and insert the fidelity card barcode)|
|  6                    | Select the payment method between "Credit Card" and "Cash"|
|  7                    | Complete the transaction|
|  8                    | Print the receipt|
|  9                    | Sale transaction is ended. Success page is prompted. Click on "Ok"|
|  10                   | For a new sale transaction return to step 2|

##### Scenario 4.2 - Checkout at the cash desk with refused credit card
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The cashier has an user profile with "sales" permission set and is logged into the application (scenario 1.1) |
|                       | The cash register is connected to the application|
|                       | The barcode reader is connected to the application|
|                       | The POS is connected to the application|
|  Post condition       | The sale information is uploaded to the application |
|  Step #               | Description |
|  1                    | Access the sales tab|
|  2                    | Click on "New Sale"|
|  3                    | Scan the EAN barcode of product X (If product EAN barcode is unreadable by the barcode reader, click on "Add manually", insert EAN Code and the quantity and presses "ok" button)|
|                       | Repeat step 3 for N products|
|  4                    | Click on "Checkout"|
|  5                    | Click on "Scan" to add a fidelity card if present (or if the add it manually by clicking on "Add manually" and insert the fidelity card barcode)|
|  6                    | Between "Cash" and "Credit Card", the second is clicked|
|  7                    | A transaction attempt is tried but the credit Card is refused and an error is displayed. Click on "Ok" to return to the choose of the payment method|
|  8                    | Attempt a new payment with another method or abort the transaction by clicking on the "Abort" Button|

### Use case 5, UC5 - Register incoming shipment
| Actors Involved       | Warehouse Worker, Barcode Reader, Supplier |
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The warehouse worker has a user profile and is logged in to the application |
|                       | The barcode reader is connected to the application|
|  Post condition       | The inventory information is updated in the application |
|  Nominal Scenario     | A new shipment arrives and the shipment barcode is scanned; the warehouse worker scans a product of the new shipment and adds it to inventory after having selected the total quantity of new products; the warehouse worker stores the new products |

##### Scenario 5.1 - Scan of a new incoming shipment
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The warehouse worker has an user profile with "inventory" permission set and is logged into the application (scenario 1.1) |
|                       | An order has been done in precedence and is registered in the orders list|
|                       | The barcode reader is connected to the application|
|  Post condition       | The inventory information is updated|
|  Step #               | Description |
|  1                    | A new expected shipment arrives|
|  2                    | Access the inventory tab|
|  3                    | Click on "Shipments"|
|  4                    | Search for the shipment on the shipments list (or scan its shipment barcode or insert its shipment barcode manually)|
|  5                    | Scan the EAN barcode of product X (If product EAN barcode is unreadable by the barcode reader, click on "Add manually" and insert the barcode)|
|  6                    | Set the quantity of arrived products|
|  7                    | Return to Inventory Tab when scan is finished|

### Use case 6, UC6 -  Product information management
| Actors Involved       | Shelf Stocker, Warehouse worker, Barcode Reader, Supplier |
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The shelf stocker has a user profile and is logged in to the application |
|                       | The warehouse worker has a user profile and is logged in to the application
|                       | The barcode reader is connected to the application|
|  Post condition       | The product information is updated in the application |
|  Nominal Scenario     | The shelf stocker or the warehouse worker scans an EAN barcode or searches for the product in the inventory; The selected product information is edited and updated in the inventory|
|  Variants             | The EAN barcode of a scanned product is not found in the inventory; an error is displayed|

##### Scenario 6.1 - Move products from warehouse to shelves
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The warehouse worker has a user profile  with "inventory" permission set and is logged in to the application (scenario 1.1) |
|                       | The barcode reader is connected to the application|
|  Post condition       | Product information are updated|
|  Step #               | Description |
|  1                    | Access the inventory tab|
|  2                    | Search for a product on the search bar (or scan its EAN barcode with the barcode read by clicking on "Search by scanning barcode")|
|  3                    | Click on the product that has to be moved from the warehouse to the shelves|
|  4                    | Modify the quantities in warehouse and shelves accordingly|
|  5                    | Click on "Confirm"|
|  6                    | The product's information is updated. Success page is prompted. Click on "Ok"|
|  7                    | Products are moved from the warehouse to the shelves|

##### Scenario 6.2 - Add a new supplier for a product
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The warehouse worker has a user profile  with "inventory" permission set and is logged in to the application (scenario 1.1) |
|                       | The barcode reader is connected to the application|
|  Post condition       | Product information are updated|
|  Step #               | Description |
|  1                    | Access the inventory tab|
|  2                    | Search for a product on the search bar (or scan its EAN barcode with the barcode read by clicking on "Search by scanning barcode")|
|  3                    | Click on the product for which a supplier has to be added|
|  4                    | Click on the "+" button near the supplier voice|
|  5                    | A new supplier name and email is added to the application|
|  6                    | Click on "Confirm"|
|  7                    | The product's list of suppliers for the product is updated. Success page is prompted. Click on "Ok"|

### Use case 7, UC7 -  Order products
| Actors Involved       | Warehouse Worker, Barcode Reader, Suppliers|
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The warehouse worker has a user profile and is logged in to the application |
|                       | The barcode reader is connected to the application |
|  Post condition       | Orders are sent to suppliers; Orders list is updated |
|  Nominal Scenario     | A warehouse worker adds products to the order via EAN barcode or searches it on the products list; quantity and supplier are chosen for the products; the order is sent to the supplier;|
|  Variants             | A quantity threshold is set for a product; if the quantity of products in inventory is under the threshold an automatic order is sent to the set supplier for the set quantity|
|                       | Add a new supplier |

##### Scenario 7.1 - Order products manually from supplier
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The warehouse worker has a user profile with "inventory" permission set and is logged in to the application (scenario 1.1)|
|                       | The barcode reader is connected to the application|
|  Post condition       | New order is sent to suppliers; Orders list is updated|
|  Step #               | Description |
|  1                    | Access the inventory tab|
|  2                    | Click on "Orders"|
|  3                    | Click on "New Order"|
|  4                    | Click on "Scan Barcode" to insert the EAN barcode via the barcode scanner (or click "Type barcode" to insert it manually)|
|  5                    | Set the quantity to order|
|  6                    | Select the specific supplier from which to order the product|
|  7                    | Click on "Confirm"|
|  8                    | Orders are sent to suppliers and order list is updated|
|  9                    | Success page is prompted. Click on "Ok"|

##### Scenario 7.2 - Set automatic order from suppliers
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The warehouse worker has a user profile  with "inventory" permission set and is logged in to the application (scenario 1.1) |
|                       | The barcode reader is connected to the application|
|  Post condition       | Automatic order is set|
|  Step #               | Description |
|  1                    | Access the inventory tab|
|  2                    | Search for a product on the search bar (or scan its EAN barcode with the barcode read by clicking on "Search by scanning barcode")|
|  3                    | Click on the product to be ordered|
|  4                    | Set the quantity, the threshold and the supplier for the automatic order (0 in quantity means that the automatic order is not set)|
|  5                    | Click on "Confirm"|
|  6                    | The automatic order is set. Success page is prompted. Click on "Ok"|

### Use case 8, UC8 - Manage customers
| Actors Involved       | Cashier, Barcode Reader, Card Printer, Fidelity Card|
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The cashier has a user profile and is logged in to the application|
|                       | The barcode reader is connected to the application |
|  Post condition       | Customer information has been managed |
|  Nominal Scenario     | The cashier adds a customer to the application and provides him a fidelity card by inserting his personal information in the application;|
|  Variants             | The barcode of the fidelity card is unreadable; the cashier manually enters the barcode|
|                       | The customer wants to unsubscribe from the fidelity program; the fidelity card is removed|
|                       | The customer wants to update the information of his fidelity card|

##### Scenario 8.1 - A fidelity card is created (print)
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The cashier has a user profile  with "customer" permission set and is logged in to the application (scenario 1.1) |
|                       | The card printer is connected to the application|
|  Post condition       | A new customer is registered with a fidelity card|
|  Step #               | Description |
|  1                    | Access the customers tab|
|  2                    | Click on "New Fidelity Card"|
|  3                    | Insert the customer's information|
|  4                    | Click on "Print card". The fidelity card is printed|
|  5                    | Success page is prompted. Click on "Ok"|
|  6                    | The new fidelity card is given to the customer|

##### Scenario 8.2 - A fidelity card is created (manually inserted)
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The cashier has a user profile  with "customer" permission set and is logged in to the application (scenario 1.1) |
|                       | The barcode reader is connected to the application|
|  Post condition       | A new customer is registered with a fidelity card|
|  Step #               | Description |
|  1                    | Access the customers tab|
|  2                    | Click on "New Fidelity Card"|
|  3                    | Insert the customer's information|
|  4                    | Click on "Insert fidelity card barcode"|
|  5                    | The code is inserted or scanned via the barcode reader|
|  6                    | Click on "Confirm"|
|  7                    | Success page is prompted. Click on "Ok"|
|  8                    | The new fidelity card is given to the customer|

##### Scenario 8.3 - Edit customer information
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The cashier has a user profile  with "customer" permission set and is logged in to the application (scenario 1.1) |
|                       | The barcode reader is connected to the application|
|  Post condition       | Customer information is updated|
|  Step #               | Description |
|  1                    | Access the customers tab|
|  2                    | Search for the user in the search bar by writing (or by scanning the fidelity card with the barcode reader)|
|  3                    | Click on the customer profile to be edited|
|  4                    | Edit the customer information|
|  5                    | Click on "Confirm"|
|  6                    | Success page is prompted. Click on "Ok"|

##### Scenario 8.4 - Delete customer information
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The cashier has a user profile  with "customer" permission set and is logged in to the application (scenario 1.1) |
|                       | The barcode reader is connected to the application|
|  Post condition       | Customer information is updated|
|  Step #               | Description |
|  1                    | Access the customers tab|
|  2                    | Search for the user in the search bar by writing (or by scanning the fidelity card with the barcode reader)|
|  3                    | Click on the customer profile to be deleted|
|  4                    | Click on "Delete"|
|  5                    | Click on "Yes"|
|  6                    | Success page is prompted. Click on "Ok"|

### Use case 9, UC9 - Support accounting
| Actors Involved       | Business Manager |
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The business manager has a user profile and is logged in to the application|
|                       | The business manager can use some taxes and financial instruments |
|  Post condition       | The business manager reviews some statistics |
|  Nominal Scenario     | The cashier adds a customer to the application and provides him a fidelity card by inserting his personal information in the application;|
|  Variants             | Different filters are used depending on the needs of the owner for each moment|
|                       | The business manager downloads accounting information in tabular format in a csv file|

##### Scenario 9.1 - Apply filters to accounting data
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The business manager has a user profile  with "accounting" permission set and is logged in to the application (scenario 1.1)|
|  Post condition       | The displayed accounting data are filtered based on the set filters|
|  Step #               | Description |
|  1                    | Access the accounting tab|
|  2                    | Click on "Filters"|
|  3                    | Set the desired filters|
|  4                    | Click on "Apply"|
|  5                    | The filtered accounting data is displayed|

##### Scenario 9.2 - Download a .csv financial report
|||
| ----------------------|:-----------------------------------------------------------------------| 
|  Precondition         | The business manager has a user profile  with "accounting" permission set and is logged in to the application (scenario 1.1)|
|  Post condition       | The displayed accounting data are filtered based on the set filters|
|  Step #               | Description |
|  1                    | Access the accounting tab|
|  2                    | Set eventual filters (Scenario 9.1)|
|  3                    | Click on "Download CSV"|
|  4                    | The financial report is downloaded to the system|

# Glossary

| Term        | Definition |
| ------------- |:-------------| 
|Accounting Tab|The section of the application where is possible to view, filter and download data related to sales transactions and accounting|
|Admin|A user with all permissions set. This role can create and grant permissions to users, by accessing the users tab, and can modify the application settings. The first created user by the application has admin permissions. Usually the shop owner is an admin|
|Barcode Reader|The instrument capable of reading barcodes on products, shipments and fidelity cards. It can be an external device or a smartphone.|
|Card Printer|A device that prints new fidelity cards for customers|
|Cash Desk|The shop's physical location where customers goes to pay for products and cashiers register sales|
|Cash Register|The electronic device composed by a drawer|
|Cashier|Role in charge of registering sales at the cash desk|
|Customer|Person that has a stake in buying products from the shop|
|Customers Tab|The section of the application where is possible to view, edit and delete data related to customers fidelity cards|
|European Article Number (EAN)|A 8 or 13 digits code that identifies univocally products on the European market|
|Fidelity Card|A card linked to a single customer that allows the customer to accumulate points and collecting data for the shop. It can be printed with a Card Printer or a shop can receive already be printed fidelity cards and add them manually to the application.|
|Home menu|A menu displayed after login that shows a general overview from where the main sections can be navigated| 
|Inventory|The entire set of products owned by the shop, both on shelves and in the warehouse|
|Inventory Tab|The section of the application where is possible to view, display, edit and delete products from the inventory, order from suppliers|
|Navigation Bar|The bar on the left of the application where it is possible to see which tab is currently displayed, switch tab and access IT Support|
|Shipping Order|An order of purchase made from the shop to the supplier for a quantity of products|
|Permission|A setting that can be granted by an admin to a user, that allow to a specific user to view and use specific tabs and/or functionalities of the program. They're set in the users tab|
|Point Of Sale (POS)|Electronic device connected to the application that allows for payments via credit card|
|Product|Good that is stored in the inventory and sold to customers. They're identified univocally by a EAN code|
|Sale|Commercial transaction between customer and retailer, where the customer buys a quantity of products from the retailer|
|Sales tab|The section of the application where it is possible to register, edit, view and delete sales|
|Shelf|The set of products in the inventory that are visible in the shop to the customers and available for selling|
|Supplier|External company/society that sells products to the shop for reselling. They receive shipping orders as an input from the shop|
|IT Support|The support function offered by the software house to admins of our application. It can be used through the apposite button in the navigation bar|
|User|An authorized person that can access to the application with a username and a password, with set permissions to access specific functionalities of the application|
|Users tab|The section of the application where admins can create and delete users, grant or remove them permissions, and see their login history|
|Warehouse|The set of products that are not visible to the customers and not available for selling, and are stocked outside|

```plantuml



class User{
    String username
    String password
    Int userID
    Boolean permission_Sales
    Boolean permission_Customers
    Boolean permission_Accounting
    Boolean permission_Inventory
    Boolean permission_Admin
}


class Admin
class Cashier
class Warehouse_worker
class Shelf_stocker


Admin --|> User 
Cashier --|> User
Warehouse_worker --|> User
Shelf_stocker --|> User



class Sale{
    int n_products
    int timestamp
    int tot_discount_perc
    int n_receipt
    int tot_amount
    Boolean fidelity
}


class Supplier{
    String name
    String email
}


class Product{
    int Quantity_shelves
    int Quantity_warehouse
    String Product_name
    int EAN
    int discount_perc
    int price
}


class Fidelity_card{
    String name
    String surname
    String province
    String address
    int CAP
    int card_ID
    int points
    String City
}





class Sales_Tab
class Customer_Tab
class Accounting_Tab
class Users_Tab
class Inventory_Tab




User "*"-- Users_Tab
Admin -- Users_Tab
Inventory_Tab - Product
Product "*"- Supplier
Sale_transaction - Fidelity_card
Sale_transaction -- "*" Product
Cashier - Customer_Tab
Customer_Tab - Fidelity_card
Warehouse_worker -- Inventory_Tab
Shelf_stocker -- Inventory_Tab
Sales_Tab -- Cashier
Sales_Tab -- Sale_transaction
Admin - Accounting_Tab
Accounting_Tab --"*" Sale_transaction


```

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


