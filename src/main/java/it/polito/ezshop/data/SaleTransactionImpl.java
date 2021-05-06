package it.polito.ezshop.data;

import java.util.HashMap;
import java.util.stream.*;
import java.util.Map;
import java.util.List;

public class SaleTransactionImpl implements SaleTransaction {

    Integer transactionId;
    enum State{PAYED, CLOSED, INPROGRESS};
    State state;
    enum PaymentType{CARD, CASH};
    PaymentType pay;
    Double currentamount;
    Double discountRate;
    HashMap<ProductTypeImpl, Integer> listOfProductsSale =new HashMap<>();
    LoyaltyCardImpl transactionCard;
    BalanceOperation saleOperationRecord;


    @Override
    public Integer getTicketNumber() {
        return this.transactionId;
    }

    @Override
    public void setTicketNumber(Integer ticketNumber) {
        this.transactionId=ticketNumber;
    }

    @Override
    public List<TicketEntry> getEntries() {
        return (List<TicketEntry>) this.listOfProductsSale;
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        this.listOfProductsSale= (HashMap<ProductTypeImpl, Integer>) entries;

    }

    @Override
    public double getDiscountRate() {
        return this.discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
    this.discountRate=discountRate;
    }

    @Override
    public double getPrice() {
        return this.currentamount;
    }

    @Override
    public void setPrice(double price) {
        this.currentamount=price;

    }

    public Double getCurrentAmount() {
        return this.currentamount;
    }

    public SaleTransactionImpl(Integer transactionId/*, Double currrentamount, Double salediscountRate,
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

    public boolean EditProductInSale(ProductTypeImpl product, Integer amount ){


        if(amount>0) {
            if( product.getQuantity()<amount) {
                return false;
                //quantity not available in inventory
            }
        }

        if(isProductInSale(product)){
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

    public void AbortSaleUpdateProductQuantity() {
        this.listOfProductsSale.entrySet().stream().forEach(e->e.getKey().updateProductQuantity(-e.getValue()));

    }

    public boolean isProductInSale(ProductTypeImpl product){
        return this.listOfProductsSale.containsKey(product);
    }

    public boolean ApplyDiscountToSaleProduct(Double disc, ProductTypeImpl product) {
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
        Integer tmp=(int) ((this.currentamount-5)/10);
        this.transactionCard.updatePoints(tmp);
        return tmp;
    }

    public boolean EndSaleUpdateProductQuantity() {
        this.state=State.CLOSED;
        this.listOfProductsSale.entrySet().stream().forEach(e->e.getKey().updateProductQuantity(e.getValue()));
        return false;

        //exceptions amnca come al solito
    }

    public double PaySaleAndReturnChange(Double amount, Boolean method) {
        if(this.currentamount<=amount) {
            this.state=State.PAYED;
            if(method) {
                this.pay=PaymentType.CASH;
            }else {
                this.pay=PaymentType.CARD;
            }
            return amount-this.currentamount;
        }

        return -1;

    }

    /* OLD VERSION EGTTERS AND SETTERS
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
	}*/
}
