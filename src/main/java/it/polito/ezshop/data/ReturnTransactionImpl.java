package it.polito.ezshop.data;

import java.util.Map;

public class ReturnTransactionImpl {
    private enum State{INPROGRESS, CLOSED, PAYED}
    private enum PaymentType{CARD, CASH}

    private Integer returnId;
    private SaleTransactionImpl saleTransaction;
    private Map<ProductTypeImpl, Integer> returnProducts;
    private State state;
    private Double amount;
    private PaymentType paymentType;
    private BalanceOperationImpl balanceOperation;

    public ReturnTransactionImpl(Integer returnId, SaleTransactionImpl saleTransaction, Map<ProductTypeImpl, Integer> returnProducts, String state, Double amount, String paymentType, BalanceOperationImpl balanceOperation) {
        this.returnId = returnId;
        this.saleTransaction = saleTransaction;
        this.returnProducts = returnProducts;
        this.state = (state != null) ? State.valueOf(state) : null;
        this.amount = amount;
        this.paymentType = (paymentType != null) ? PaymentType.valueOf(paymentType) : null;
        this.balanceOperation = balanceOperation;
    }

    public Integer getReturnId() {
        return returnId;
    }

    public void setReturnId(Integer returnId) {
        if (returnId == null) {
            return;
        }
        this.returnId = returnId;
    }

    public SaleTransactionImpl getSaleTransaction() {
        return saleTransaction;
    }

    public void setSaleTransaction(SaleTransactionImpl saleTransaction) {
        if (saleTransaction == null) {
            return;
        }
        this.saleTransaction = saleTransaction;
    }

    public Map<ProductTypeImpl, Integer> getReturnProducts() {
        return returnProducts;
    }

    public void setReturnProducts(Map<ProductTypeImpl, Integer> returnProducts) {
        if (returnProducts == null) {
            return;
        }
        this.returnProducts = returnProducts;
    }

    public String getState() {
        return (state != null) ? state.toString() : null;
    }

    public void setState(String state) {
        if (state == null) {
            return;
        }
        this.state = State.valueOf(state);
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        if (amount == null) {
            return;
        }
        this.amount = amount;
    }

    public String getPaymentType() {
        return (paymentType != null) ? paymentType.toString() : null;
    }

    public void setPaymentType(String paymentType) {
        if (paymentType == null) {
            return;
        }
        this.paymentType = PaymentType.valueOf(paymentType);
    }

    public BalanceOperationImpl getBalanceOperation() {
        return balanceOperation;
    }

    public void setBalanceOperation(BalanceOperationImpl balanceOperation) {
        if (balanceOperation == null) {
            return;
        }
        this.balanceOperation = balanceOperation;
    }

    public boolean isInProgress() {
        return (this.state == State.INPROGRESS);
    }

    public boolean isClosed() {
        return (this.state == State.CLOSED);
    }

    public boolean isPayed() {
        return (this.state == State.PAYED);
    }

    public void addProduct(ProductTypeImpl productType, Integer quantity) {
        // Add product to the returnProducts
        this.returnProducts.put(productType, quantity);
        // Update amount applying the product discount and the sale discount, if any
        this.amount += quantity * productType.getPricePerUnit() * (1 - productType.getProductDiscountRate()) * (1 - this.saleTransaction.getDiscountRate());
    }
}
