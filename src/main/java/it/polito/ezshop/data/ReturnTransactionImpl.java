package it.polito.ezshop.data;

import java.util.Map;

public class ReturnTransactionImpl {
    private enum State{INPROGRESS, CLOSED, DELETED}
    private enum PaymentType{CARD, CASH}

    private Integer returnId;
    private SaleTransactionImpl saleTransaction;
    private Map<ProductTypeImpl, Integer> listOfProductsReturn;
    private State state;
    private PaymentType paymentType;
    private Double amount;
    private BalanceOperationImpl balanceOperation;

    public ReturnTransactionImpl(Integer returnId, SaleTransactionImpl saleTransaction, Map<ProductTypeImpl, Integer> listOfProductsReturn, String state, String paymentType, Double amount, BalanceOperationImpl balanceOperation) {
        this.returnId = returnId;
        this.saleTransaction = saleTransaction;
        this.listOfProductsReturn = listOfProductsReturn;
        this.state = (state != null) ? State.valueOf(state) : null;
        this.paymentType = (paymentType != null) ? PaymentType.valueOf(paymentType) : null;
        this.amount = amount;
        this.balanceOperation = balanceOperation;
    }

    public Integer getReturnId() {
        return returnId;
    }

    public void setReturnId(Integer returnId) {
        this.returnId = returnId;
    }

    public SaleTransactionImpl getSaleTransaction() {
        return saleTransaction;
    }

    public void setSaleTransaction(SaleTransactionImpl saleTransaction) {
        this.saleTransaction = saleTransaction;
    }

    public Map<ProductTypeImpl, Integer> getListOfProductsReturn() {
        return listOfProductsReturn;
    }

    public void setListOfProductsReturn(Map<ProductTypeImpl, Integer> listOfProductsReturn) {
        this.listOfProductsReturn = listOfProductsReturn;
    }

    public String getState() {
        return (state != null) ? state.toString() : null;
    }

    public void setState(String state) {
        this.state = State.valueOf(state);
    }

    public String getPaymentType() {
        return (paymentType != null) ? paymentType.toString() : null;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = PaymentType.valueOf(paymentType);
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public BalanceOperationImpl getBalanceOperation() {
        return balanceOperation;
    }

    public void setBalanceOperation(BalanceOperationImpl balanceOperation) {
        this.balanceOperation = balanceOperation;
    }

    public boolean isInProgress() {
        return (this.state == State.INPROGRESS);
    }

    public boolean isClosed() {
        return (this.state == State.CLOSED);
    }

    public boolean isDeleted() {
        return (this.state == State.DELETED);
    }

    public void addProduct(ProductTypeImpl productType, Integer quantity) {
        this.listOfProductsReturn.put(productType, quantity);
        // TODO update amount
    }
}
