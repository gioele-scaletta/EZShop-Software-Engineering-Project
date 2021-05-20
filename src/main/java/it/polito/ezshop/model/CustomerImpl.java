package it.polito.ezshop.model;

import it.polito.ezshop.data.Customer;

public class CustomerImpl implements Customer {

    private String customerName;
    private String customerCard;
    private Integer id;
    private Integer points;

    public CustomerImpl(String customerName, String customerCard, Integer customerId, Integer points) {
        // Customer name
        this.customerName = customerName;

        // Customer card
        this.customerCard = customerCard;

        // Customer id
        this.id = customerId;

        // Points
        this.points = points;
    }

    @Override
    public String getCustomerName() {
        return this.customerName;
    }

    @Override
    public void setCustomerName(String customerName) {
        if (customerName == null) {return;}
        this.customerName = customerName;
    }

    @Override
    public String getCustomerCard() {
        return this.customerCard;
    }

    @Override
    public void setCustomerCard(String customerCard) {
        if (customerCard == null) {return;}
        this.customerCard = customerCard;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        if (id == null) {return;}
        this.id = id;
    }

    @Override
    public Integer getPoints() {
        return this.points;
    }

    @Override
    public void setPoints(Integer points) {
        if (points == null) {return;}
        this.points = points;
    }

}
