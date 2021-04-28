package ezshopLogic;

import java.util.HashMap;
import java.util.stream.*;
import java.util.Map;

public class SaleTransaction {  
    Integer transactionId;
    enum State{FAILED, COMPLETED, INPROGRESS};
    State state;
    enum PaymentType{CARD, CASH};
    PaymentType pay;
    Double currentamount;
    Double salediscountRate;
    HashMap<ProductType, Integer> listOfProductsSale =new HashMap<>();
    LoyaltyCard transactionCard;
    BalanceOperation saleOperationRecord;
    
	public SaleTransaction(Integer transactionId/*, Double currrentamount, Double salediscountRate,
			Map<ProductType, Integer> listOfProductsSale, LoyaltyCard transactionCard,
			BalanceOperation saleOperationRecord*/) {
	
		this.transactionId = transactionId;
		this.state=State.INPROGRESS;
		
		//this.currentamount = currrentamount;
	/*	this.salediscountRate = salediscountRate;
		this.listOfProductsSale = listOfProductsSale;
		this.transactionCard = transactionCard;
		this.saleOperationRecord = saleOperationRecord;*/
	}
	
	 public boolean AddUpdateDeleteProductInSale(ProductType product, Integer amount ){
		 
		 
		 if(amount>0) {
			if( product.getQuantity()<amount) {
				return false;
				//quantity not available in inventory
			}
		 }
    	
    	 if(this.listOfProductsSale.containsKey(product)){
    		 this.listOfProductsSale.put(product, this.listOfProductsSale.get(product) + amount);
             this.currentamount=this.currentamount+amount*product.getSellPrice();
             if(this.currentamount==0) {
            	 this.listOfProductsSale.remove(product);
            	  return true;
             }
             if(this.currentamount<0) {
              	//removed too many
            return false;}
             return true;
         } else{
        	 if(amount<0) {
             	//tried to remove element not in current sale
           	  return false;}
        	 
        	 this.listOfProductsSale.put(product, amount);
        	 
             return true;
         }
  
        
         //MISSING handle invalid productCode ret false
         //MISSING handle invalid amount ret false

    }
	
	 public boolean ApplyDiscountToSaleProduct(Double disc, ProductType product) {
		 if (disc <0 || disc >1) {
			 return false;
			// throw......
		 }
		 if(this.listOfProductsSale.containsKey(product)){
	            this.currentamount = this.currentamount - disc*this.listOfProductsSale.get(product)*product.getSellPrice();
	            return true;
	        }
		 //throw....
		 return false;
	 }
	 
	 
	 public boolean ApplyDiscountToSaleAll(Double disc) {
         this.currentamount=this.currentamount*(1-disc);
         
		 if (disc <0 || disc >1) {
			 return false;
			// throw......
		 }
		 return true;
	 }
	 

	public int PointsForSale() {
			return (int) ((this.currentamount-5)/10);
		}

	public boolean EndSaleUpdateProductQuantity() {
		this.listOfProductsSale.entrySet().stream().forEach(e->e.getKey().updateProductQuantity(e.getValue()));
		return false;
		
		//exceptions amnca come al solito
	}
	
	
	public Integer getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}
	public Double getCurrentAmount() {
		return currentamount;
	}
	public void setCurrentAmount(Double currrentamount) {
		this.currentamount = currrentamount;
	}
	public Double getSalediscountRate() {
		return salediscountRate;
	}
	public void setSalediscountRate(Double salediscountRate) {
		this.salediscountRate = salediscountRate;
	}
	public HashMap<ProductType, Integer> getListOfProductsSale() {
		return listOfProductsSale;
	}
	public void setListOfProductsSale(HashMap<ProductType, Integer> listOfProductsSale) {
		this.listOfProductsSale = listOfProductsSale;
	}
	public LoyaltyCard getTransactionCard() {
		return transactionCard;
	}
	public void setTransactionCard(LoyaltyCard transactionCard) {
		this.transactionCard = transactionCard;
	}
	public BalanceOperation getSaleOperationRecord() {
		return saleOperationRecord;
	}
	public void setSaleOperationRecord(BalanceOperation saleOperationRecord) {
		this.saleOperationRecord = saleOperationRecord;
	}

	public double PaySaleAndReturnChange(Double cash, Boolean method) {
		if(this.currentamount<=cash) {
    		this.state=State.COMPLETED;
    		if(method) {
    			this.pay=PaymentType.CASH;
    		}else {
    			this.pay=PaymentType.CARD;
    		}
    		return cash-this.currentamount;
    	}
	
		return -1;
	
	}

	

}
