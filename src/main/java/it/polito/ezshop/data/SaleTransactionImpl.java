package it.polito.ezshop.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SaleTransactionImpl implements SaleTransaction {


    Integer transactionId;
    Double amount;
    enum State{PAYED, CLOSED, INPROGRESS};
    State state;
    enum PaymentType{CARD, CASH};
    PaymentType pay;
    Double discountRate;
    HashMap< ProductTypeImpl, Integer> listOfProductsSale =new HashMap<>();
    List<TicketEntry> listOfProductsEntries;
    CustomerImpl transactionCard;
    BalanceOperation saleOperationRecord;


    //contructor for new sale trasaction
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


    //constructor for loading data from db
    public SaleTransactionImpl(int transactionId, String stat, String paymentType, double amount, double discountRate, CustomerImpl transactionCardId, BalanceOperationImpl balanceOperationId, HashMap< ProductTypeImpl, Integer> listofprod) {
        this.transactionId = transactionId;
        this.state= State.valueOf(stat);
        this.amount = amount;
		this.discountRate = discountRate;
		this.listOfProductsSale=listofprod;
        listofprod.entrySet().stream().forEach(e->{
		   TicketEntryImpl t=  new TicketEntryImpl(e.getKey().getBarCode(), e.getKey().getProductDescription(), e.getValue(), e.getKey().getSellPrice(), e.getKey().getProductDiscountRate());

            listOfProductsEntries.add(t);
        });
		this.transactionCard = transactionCardId;
		this.saleOperationRecord = balanceOperationId;


    }

    public HashMap<ProductTypeImpl, Integer> getListOfProductsSale() {
        return listOfProductsSale;
    }
    public String getPayString() {
        return pay.toString();
    }
    public String getStateString() {
        return state.toString();
    }
    public CustomerImpl getTransactionCard() {
        return transactionCard;
    }
    public BalanceOperation getSaleOperationRecord() {return saleOperationRecord; }

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
       return this.listOfProductsEntries;
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
      this.listOfProductsEntries= entries;

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
        return this.amount;
    }

    @Override
    public void setPrice(double price) {
        this.amount=price;

    }

    public Double getCurrentAmount() {
        return this.amount;
    }



    public boolean EditProductInSale(ProductTypeImpl product, Integer amount ){

    //some checks and the excetions are handled outside of this function

        if(isProductInSale(product)){
            this.listOfProductsSale.put(product, this.listOfProductsSale.get(product) + amount);
            this.amount=this.amount+amount*product.getSellPrice();
            if(this.amount==0) {
                this.listOfProductsSale.remove(product);
                return true;
            }

            return true;
        } else{

            this.listOfProductsSale.put(product, amount);

            return true;
        }

    }

    public void AbortSaleUpdateProductQuantity() {
        this.listOfProductsSale.entrySet().stream().forEach(e->e.getKey().updateProductQuantity(-e.getValue()));

    }

    public boolean isProductInSale(ProductTypeImpl product){
        return this.listOfProductsSale.containsKey(product);
    }

    public boolean ApplyDiscountToSaleProduct(Double disc, ProductTypeImpl product) {

        if(this.listOfProductsSale.containsKey(product)){
            this.amount = this.amount - disc*this.listOfProductsSale.get(product)*product.getSellPrice();
            return true;
        }
        return false;
    }


    public boolean ApplyDiscountToSaleAll(Double disc) {
        this.amount=this.amount*(1-disc);

        if (disc <0 || disc >1) {
            return false;
        }
        return true;
    }


    public int PointsForSale() {
        Integer tmp=(int) ((this.amount-5)/10);
        //this.transactionCard.updatePoints(tmp);
        return tmp;
    }

    public boolean EndSaleUpdateProductQuantity() {
        this.state=State.CLOSED;
        this.listOfProductsSale.entrySet().stream().forEach(e->e.getKey().updateProductQuantity(e.getValue()));
        return false;

    }

    public double PaySaleAndReturnChange(Double amount, Boolean method) {
        if(this.amount<=amount) {
            this.state=State.PAYED;
            if(method) {
                this.pay=PaymentType.CASH;
            }else {
                this.pay=PaymentType.CARD;
            }
            return amount-this.amount;
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
