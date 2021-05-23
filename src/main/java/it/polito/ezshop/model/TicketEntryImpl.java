package it.polito.ezshop.model;

import it.polito.ezshop.data.TicketEntry;

public class TicketEntryImpl implements TicketEntry {

    private String barCode;
    private String productDescription;
    private int amount;
    private double pricePerUnit;
    private double discountRate;


    public TicketEntryImpl(String barCode, String productDescription, int amount, double pricePerUnit, double discountRate) {
        this.barCode = barCode;
        this.productDescription = productDescription;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.discountRate = discountRate;
    }

    public TicketEntryImpl(ProductTypeImpl p, Integer quantity ) {
        this.barCode = p.getBarCode();
        this.productDescription = p.getProductDescription();
        this.amount = quantity;
        this.pricePerUnit = p.getPricePerUnit();
        this.discountRate = p.getProductDiscountRate();
    }

    public String getBarCode() {
        return this.barCode;
    }

    public void setBarCode(String barCode) {
        if (barCode == null) {
            return;
        }
        this.barCode = barCode;
    }

    public String getProductDescription() {
        return this.productDescription;
    }

    public void setProductDescription(String productDescription) {
        if (productDescription == null) {
            return;
        }
        this.productDescription = productDescription;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPricePerUnit() {
        return this.pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public double getDiscountRate() {
        return this.discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }
}
