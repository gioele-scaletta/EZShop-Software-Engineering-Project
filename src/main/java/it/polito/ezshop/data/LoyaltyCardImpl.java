package it.polito.ezshop.data;

public class LoyaltyCardImpl {
    String cardId;
    String points;

    public String getCardId(){
        return this.cardId;
    }
    public void updatePoints(Integer tmp) {
        this.points=this.points+tmp;
    }
}
