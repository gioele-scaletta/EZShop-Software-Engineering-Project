package it.polito.ezshop.data;

public class TicketEntryImpl implements TicketEntry{

    private String barCode = "";
    private String productDescription = "";
    private int amount = -1;
    private double pricePerUnit = -1.0D;
    private double discountRate = -1.0D;


    public TicketEntryImpl(String barCode, String productDescription, int amount, double pricePerUnit, double discountRate) {
        this.barCode = barCode;
        this.productDescription = productDescription;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.discountRate = discountRate;
    }

    public String getBarCode() {
        return this.barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getProductDescription() {
        return this.productDescription;
    }

    public void setProductDescription(String productDescription) {
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
