package ezshopLogic;

public class Main {

    public static void main(String[] args) throws Exception {
        EZShop ez = new EZShop();
        ez.createUser("marco","password","administrator");
        ez.login("marco","password");
    }
}