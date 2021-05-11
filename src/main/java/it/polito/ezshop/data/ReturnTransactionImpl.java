package it.polito.ezshop.data;

import java.util.Map;
import java.util.HashMap;

public class ReturnTransactionImpl {
    private Integer returnId;
    private Integer saleTransactionId;
    Map<ProductTypeImpl, Integer> listOfProductsReturn = new HashMap<>();
    private Double amount;
    enum PaymentType{CREDIT_CARD, CASH};
    SaleTransactionImpl.PaymentType paymentType;
    private Boolean commit;

    public ReturnTransactionImpl(Integer returnId, Integer saleTransactionId) {
        this.returnId = returnId;
        this.saleTransactionId = saleTransactionId;
    }

    public Integer getReturnId() {
        return returnId;
    }

    public void setReturnId(Integer returnId) {
        this.returnId = returnId;
    }

    public Integer getSaleTransactionId() {
        return saleTransactionId;
    }

    public void setSaleTransactionId(Integer saleTransactionId) {
        this.saleTransactionId = saleTransactionId;
    }

    public Map<ProductTypeImpl, Integer> getListOfProductsReturn() {
        return listOfProductsReturn;
    }

    public void setListOfProductsReturn(Map<ProductTypeImpl, Integer> listOfProductsReturn) {
        this.listOfProductsReturn = listOfProductsReturn;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public SaleTransactionImpl.PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(SaleTransactionImpl.PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Boolean getCommit() {
        return commit;
    }

    public void setCommit(Boolean commit) {
        this.commit = commit;
    }
}
