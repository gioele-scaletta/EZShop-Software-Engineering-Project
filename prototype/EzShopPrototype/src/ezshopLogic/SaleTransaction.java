package ezshopLogic;

import java.util.HashMap;
import java.util.Map;

public class SaleTransaction {  
    Integer transactionId;
    enum State{FAILED, COMPLETED, DELETE};
    enum PaymentType{CARD, CASH};
    Double currentamount;
    Double salediscountRate;
    HashMap<ProductType, Integer> listOfProductsSale =new HashMap<>();
    LoyaltyCard transactionCard;
    BalanceOperation saleOperationRecord;
    
	public SaleTransaction(Integer transactionId, Double currrentamount/*, Double salediscountRate,
			Map<ProductType, Integer> listOfProductsSale, LoyaltyCard transactionCard,
			BalanceOperation saleOperationRecord*/) {
	
		this.transactionId = transactionId;
		this.currentamount = currrentamount;
	/*	this.salediscountRate = salediscountRate;
		this.listOfProductsSale = listOfProductsSale;
		this.transactionCard = transactionCard;
		this.saleOperationRecord = saleOperationRecord;*/
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

    //getSaleTransactionById maybe it is getsaleticket
}
