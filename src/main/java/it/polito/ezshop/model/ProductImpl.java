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

    public static String nextRFID(String rfid){
        //Checking if parameter is a valid RFID
        if(!isValidRFID(rfid))
            return null;

        //If is latest RFID, return first RFID
        if(rfid.equals("999999999999"))
            return "000000000000";

        //Actual increment of RFID
        Long p = Long.parseLong(rfid);
        p = p+1;
        String s = Long.toString(p);

        //Padding RFID string
        while(s.length() != 12)
            s = "0" + s;

        return s;
    }
}
