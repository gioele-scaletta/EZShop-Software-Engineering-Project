package it.polito.ezshop.model;

public class ProductImpl {
    private Integer productID;
    private String RFID;

    public static boolean isValidRFID(String rfid) {
        if(rfid==null)
            return false;

        Long p;
        //Checking if length is correct
        if(rfid.length()!=12)
            return false;

        //Checking if string contains an integer
        try {
            p = Long.parseLong(rfid);
        } catch (NumberFormatException e){
            return false;
        }

        if(p<=0){
            return false;
        }
        return true;
    }
}
