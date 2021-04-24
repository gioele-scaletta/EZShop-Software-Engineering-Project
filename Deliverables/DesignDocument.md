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

<UC6 and UC7 (Gioele part)>

```plantuml
left to right direction

class Financial_Transaction {
    -description
    -amount
    -date
    -time

    public boolean recordBalanceUpdate(double toBeAdded)
 
}


class EZ_Shop{
    public Integer startSaleTransaction()
    public double computeBalance()
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to)
    public Integer startReturnTransaction(Integer ticketNumber)
    public List<ProductType> getAllProductTypes()
    public List<ProductType> getProductTypesByDescription(String description)
}



class Sale_payment{
    -ticketNumber
    -paymentType
    -__transactionId
    -creditCard

    public Ticket getTicketByNumber(Integer ticketNumber)
    public boolean deleteSaleTicket(Integer ticketNumber)
    public double receiveCashPayment(Integer ticketNumber, double cash)
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard)
    public Integer getTransactionId(Integer ticketNumber)
}


Order "1"--"1" Order_payment
Sale_Transaction "1" - "1" Sale_payment
Sale_payment "1"--"0...*" Return_Transaction
Return_payment "1" -- "1" Return_Transaction
Sale_payment --|> Financial_Transaction
Return_payment --|> Financial_Transaction
Order_payment --|> Financial_Transaction


class Sale_Transaction {
    -transactionId
    -__ticketNumber
    -__card_ID
    -amount_tmp
    -sale_discountRate
    -__Map<String, Integer>(productCode, quantity)   

    public boolean addProductToSale(Integer transactionId, String productCode, int amount)
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount)
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate)
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate)
    public int computePointsForSale(Integer transactionId)
    public Ticket getSaleTicket(Integer transactionId)
}
Sale_Transaction - "*" Product_Type

class Return_payment{
    __returnID
    -creditCard

    public double returnCreditCardPayment(Integer returnId, String creditCard)
    public double returnCashPayment(Integer returnId)
}

class Return_Transaction {
    -__transactionId
    -__ticketNumber
    -__Map<String, Integer>(productCode, quantity)   
    -amount_tmp
    -returnId
    -quantity
    -returnedValue

    public boolean returnProduct(Integer returnId, String productCode, int amount)
    public boolean endReturnTransaction(Integer returnId, boolean commit)
    public boolean deleteReturnTransaction(Integer returnId)
}

class Quantity {
    -quantity
}

(Sale_Transaction, Product_Type)  .. Quantity

class Product_Type{
    -productCode
    -barCode
    -description
    -sellPrice
    -inventory_quantity
    -product_discountRate
    -notes

    public Integer createProductType(String description, String productCode, double pricePerUnit, String note)
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote)
    public boolean deleteProductType(Integer id)
    public ProductType getProductTypeByBarCode(String barCode)

    public void updateProductQuantity(Map<Integer, Integer>(productCode, quantity))
    public void updateProductQuantity(Integer , Integer quantity)
}

class Loyalty_Card {
    -card_ID
    -points
    -name
    -surname
    
}

Sale_Transaction "1...*" -- "0..1" Loyalty_Card

class Order {
    -order_Id
    -supplier
    -pricePerUnit
    -quantity
    -status
}

class Order_payment{
    -__orderId
}

Order "*" - Product_Type

Return_Transaction "0...*" - "1" Sale_Transaction
Return_Transaction "0...*" - "1...*" Product_Type


```




# Verification traceability matrix

\<for each functional requirement from the requirement document, list which classes concur to implement it>











# Verification sequence diagrams 
\<select key scenarios from the requirement document. For each of them define a sequence diagram showing that the scenario can be implemented by the classes and methods in the design>

