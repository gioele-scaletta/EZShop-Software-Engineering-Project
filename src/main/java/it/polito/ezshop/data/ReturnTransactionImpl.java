package it.polito.ezshop.data;

import java.util.Map;
import java.util.HashMap;

public class ReturnTransactionImpl {
    private enum State{INPROGRESS, UNDONE, COMMIT, DELETED};
    private enum PaymentType{CARD, CASH};

    private Integer returnId;
    private SaleTransaction saleTransaction;
    private Map<ProductTypeImpl, Integer> listOfProductsReturn;
    private State state;
    private PaymentType paymentType;
    private Double amount;
    private BalanceOperation balanceOperation;

    public ReturnTransactionImpl(Integer returnId, SaleTransaction saleTransaction, Map<ProductTypeImpl, Integer> listOfProductsReturn, String state, String paymentType, Double amount, BalanceOperation balanceOperation) {
        this.returnId = returnId;
        this.saleTransaction = saleTransaction;
        this.listOfProductsReturn = listOfProductsReturn;
        this.state = State.valueOf(state);
        this.paymentType = PaymentType.valueOf(paymentType);
        this.amount = amount;
        this.balanceOperation = balanceOperation;
    }

    public Integer getReturnId() {
        return returnId;
    }

    public void setReturnId(Integer returnId) {
        this.returnId = returnId;
    }

    public SaleTransaction getSaleTransaction() {
        return saleTransaction;
    }

    public void setSaleTransaction(SaleTransaction saleTransaction) {
        this.saleTransaction = saleTransaction;
    }

    public Map<ProductTypeImpl, Integer> getListOfProductsReturn() {
        return listOfProductsReturn;
    }

    public void setListOfProductsReturn(Map<ProductTypeImpl, Integer> listOfProductsReturn) {
        this.listOfProductsReturn = listOfProductsReturn;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public BalanceOperation getBalanceOperation() {
        return balanceOperation;
    }

    public void setBalanceOperation(BalanceOperation balanceOperation) {
        this.balanceOperation = balanceOperation;
    }
}
