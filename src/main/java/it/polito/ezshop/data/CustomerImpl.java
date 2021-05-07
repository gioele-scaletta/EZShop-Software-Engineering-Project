package it.polito.ezshop.data;

public class CustomerImpl implements Customer{

    private String customerName;
    private String customerCard;
    private Integer id;
    private Integer points;

    public CustomerImpl(String customerName, Integer id){

        // Customer name
        this.customerName = customerName;

        // A fidelity card has not been attached to the client
        this.customerCard = "";

        // Customer id
        this.id = id;

        // The customer starts with 0 points on the fidelity card
        this.points = 0; 
    }

    @Override
    public String getCustomerName() {
        return this.customerName;
    }

    @Override
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String getCustomerCard() {
        return this.customerCard;
    }

    @Override
    public void setCustomerCard(String customerCard) {
        this.customerCard = customerCard;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getPoints() {
        return this.points;
    }

    @Override
    public void setPoints(Integer points) {
        this.points = points;
    }

}
