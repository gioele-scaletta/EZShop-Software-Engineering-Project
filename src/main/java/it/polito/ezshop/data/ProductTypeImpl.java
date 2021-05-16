package it.polito.ezshop.data;

import java.awt.datatransfer.FlavorEvent;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductTypeImpl implements ProductType{
    private Integer productID;
    private String barcode;
    private String description;
    private Double sellPrice;
    private Integer quantity;

    public void setProductDiscountRate(Double productDiscountRate) {
        this.productDiscountRate = productDiscountRate;
    }

    private Double productDiscountRate;
    private String notes;
    private Integer aisleId;
    private String rackId;
    private Integer levelId;


    public ProductTypeImpl(Integer productID, String barcode, String description, Double sellPrice, Integer quantity, Double productDiscountRate, String notes, Integer aisleID, String rackID, Integer levelID) {
        this.productID = productID;
        this.barcode = barcode;
        this.description = description;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.productDiscountRate = productDiscountRate;
        this.notes = notes;
        this.aisleId = aisleID;
        this.rackId = rackId;
        this.levelId = levelId;
    }

    public ProductTypeImpl (Integer productID, String barcode, String description, Double sellPrice, String notes) {
        this.productID = productID;
        this.barcode = barcode;
        this.description = description;
        this.sellPrice = sellPrice;
        this.quantity=0;
        this.notes = notes;
        this.productDiscountRate = 0.0;
        this.aisleId = aisleId;
        this.rackId = rackId;
        this.levelId= levelId;
    }

    public static boolean isValidCode(String productCode) {
        Long p;
        //Checking if length is correct
        if(productCode.length()<12 || productCode.length()>14)
            return false;

        //Checking if string contains an integer
        try {
            p = Long.parseLong(productCode);
        } catch (NumberFormatException e){
            return false;
        }

        //Converting string to array of digits
        int[] digits = productCode.chars().map(c -> c-'0').toArray();
        //Multiplying elements of array x3 and x1 in an alternate way
        boolean alt = true;
        for(int i = digits.length - 2; i >= 0; i--) {
            digits[i] = alt ? digits[i]*3 : digits[i];
            alt = !alt;
        }
        //Summing elements of array
        Integer sum = Arrays.stream(digits).sum() - digits[digits.length - 1];
        //Checking if the closest higher tens to sum, minus sum is equal to 3
        if((sum+(10-(sum%10)))-sum==digits[digits.length - 1])
            return true;
        else
            return false;
    }

    public static boolean isValidLocation(String location) {
        if(location.equals(""))
            return true;
        return Pattern.compile("^[a-zA-Z0-9]+[-][0-9]+[-][a-zA-Z0-9]+$").matcher(location).matches();
    }

    public static Integer extractAisleId(String location) {
        String[] parts = location.split("-");
        return Integer.parseInt(parts[0]);
    }

    public static String extractRackId(String location) {
        String[] parts = location.split("-");
        return parts[1];
    }

    public static Integer extractLevelId(String location) {
        String[] parts = location.split("-");
        return Integer.parseInt(parts[2]);
    }

    public Double getProductDiscountRate() {
        return productDiscountRate;
    }

    @Override
    public Integer getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(Integer quantity) { this.quantity=quantity; }

    @Override
    public String getLocation() {
        if(aisleId==0 && rackId.equals("empty") && levelId==0)
            return "";
        return this.aisleId+"-"+this.rackId+"-"+this.levelId;
    }

    @Override
    public void setLocation(String location) {
        String[] parts = location.split("-");
        this.aisleId = Integer.parseInt(parts[0]);
        this.rackId = parts[1];
        this.levelId = Integer.parseInt(parts[2]);
    }

    @Override
    public String getNote() {
        return notes;
    }

    @Override
    public void setNote(String note) { this.notes = note; }

    @Override
    public String getProductDescription() {
        return description;
    }

    @Override
    public void setProductDescription(String productDescription) { this.description = productDescription; }

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
        return sellPrice;
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) { this.sellPrice = pricePerUnit;}

    @Override
    public Integer getId() {
        return productID;
    }

    @Override
    public void setId(Integer id) { this.productID = id; }

    public double getSellPrice(){
        return this.sellPrice;
    }

    public void updateProductQuantity(Integer changeQuantity) {
        this.quantity=this.quantity+changeQuantity;
    }

}
