package it.polito.ezshop;

import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.view.EZShopGUI;


public class EZShop {

    public static void main(String[] args) {
        EZShopInterface ezShop = new it.polito.ezshop.model.EZShop();
        EZShopGUI gui = new EZShopGUI(ezShop);
    }

}
