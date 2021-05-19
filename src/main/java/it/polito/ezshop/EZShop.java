package it.polito.ezshop;

import it.polito.ezshop.data.BalanceOperationImpl;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.SaleTransactionImpl;
import it.polito.ezshop.view.EZShopGUI;

import java.time.LocalDate;


public class EZShop {

    public static void main(String[] args) {
        EZShopInterface ezShop = new it.polito.ezshop.data.EZShop();
        EZShopGUI gui = new EZShopGUI(ezShop);
    }

}
