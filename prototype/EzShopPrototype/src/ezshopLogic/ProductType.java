package ezshopLogic;

public class ProductType {

    private Integer productID;
    private String barcode;
    private String description;
    private Double sellPrice;
    private Integer quantity;
    private Double productDiscountRate;
    private String notes;
    private Integer aisleID;
    private String rackID;
    private Integer levelID;

    public ProductType(Integer productID, String barcode, String description, Double sellPrice, String notes) {
        this.productID = productID;
        this.barcode = barcode;
        this.description = description;
        this.sellPrice = sellPrice;
        this.setQuantity(0);
        this.notes = notes;
        this.productDiscountRate = 0.0;
        this.aisleID = 0;
        this.rackID = "";
        this.levelID = 0;
    }

    public static boolean isValidBarcode(String barcode) {
        if (barcode.isBlank() || barcode == null) {
            return false;
        }

        for (char c : barcode.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }
    
    //metodo aggiunto da Gioele per cambiare quantit� di un prodotto in inventory serve per le sales
    public void updateProductQuantity(Integer quantitytorem) {
    	this.quantity=this.quantity-quantitytorem;
    }
    

    public Integer getProductID() {
        return this.productID;
    }
    public String getBarcode() {
        return this.barcode;
    }

    public String getDescription(){
        return this.description;
    }

    public boolean setBarcode(String barcode) {
        if(!ProductType.isValidBarcode(barcode))
            return false;
        this.barcode = barcode;
        return true;
    }

    public boolean setDescription(String description) {
        if(description.isBlank() || description == null)
            return false;
        this.description = description;
        return true;
    }

    public boolean setSellPrice(Double sellPrice) {
        if(sellPrice<=0.0)
            return false;
        this.sellPrice = sellPrice;
        return true;
    }

    public boolean setNotes(String notes) {
        this.notes = notes;
        return true;
    }

	public Double getSellPrice() {
		return sellPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
