package it.polito.ezshop;

import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.SaleTransactionImpl;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.view.EZShopGUI;


public class EZShop {

    public static void main(String[] args) throws InvalidLocationException, UnauthorizedException, InvalidProductIdException, InvalidPasswordException, InvalidUsernameException {


       EZShopInterface ezShop = new it.polito.ezshop.data.EZShop();
        EZShopGUI gui = new EZShopGUI(ezShop);
    }

}