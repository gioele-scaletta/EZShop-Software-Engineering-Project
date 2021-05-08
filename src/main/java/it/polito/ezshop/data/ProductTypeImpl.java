package it.polito.ezshop.data;

public class ProductTypeImpl implements ProductType{
    private Integer productID;
    private String barcode;
    private String description;
    private Double sellPrice;
    private Integer quantity;

    public Double getProductDiscountRate() {
        return productDiscountRate;
    }

    private Double productDiscountRate;
    private String notes;
    private Integer aisleID;
    private String rackID;
    private Integer levelID;

    public ProductTypeImpl (Integer productID, String barcode, String description, Double sellPrice, String notes) {
        this.productID = productID;
        this.barcode = barcode;
        this.description = description;
        this.sellPrice = sellPrice;
        this.quantity=0;
        this.notes = notes;
        this.productDiscountRate = 0.0;
        this.aisleID = 0;
        this.rackID = "";
        this.levelID = 0;
    }




    @Override
    public Integer getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity=quantity;

    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public void setLocation(String location) {

    }

    @Override
    public String getNote() {
        return null;
    }

    @Override
    public void setNote(String note) {

    }

    @Override
    public String getProductDescription() {
        return null;
    }

    @Override
    public void setProductDescription(String productDescription) {

    }

    @Override
    public String getBarCode() {
        return this.barcode;
    }

    @Override
    public void setBarCode(String barCode) {
    this.barcode=barcode;
    }

    @Override
    public Double getPricePerUnit() {
        return null;
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) {

    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public void setId(Integer id) {

    }

    public double getSellPrice(){
        return this.sellPrice;
    }




    public void updateProductQuantity(Integer changequantity) {
        this.quantity=this.quantity-changequantity;
    }







}
