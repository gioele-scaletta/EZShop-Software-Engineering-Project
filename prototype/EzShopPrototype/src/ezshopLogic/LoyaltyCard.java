package ezshopLogic;

public class LoyaltyCard {
String cardId;
String points;

public String getCardId(){
    return this.cardId;
}

public void updatePoints(Integer tmp) {
    this.points=this.points+tmp;
}

}
