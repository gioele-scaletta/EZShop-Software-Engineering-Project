package it.polito.ezshop.data;

import java.time.LocalDate;

public class BalanceOperationImpl implements BalanceOperation {
    private enum Type{DEBIT, CREDIT}

    private int balanceId;
    private LocalDate date;
    private double money;
    private Type type;

    public BalanceOperationImpl(int balanceId, LocalDate date, double money, String type) {
        this.balanceId = balanceId;
        this.date = date;
        this.money = money;
        this.type = (type != null) ? Type.valueOf(type) : null;
    }

    @Override
    public int getBalanceId() {
        return this.balanceId;
    }

    @Override
    public void setBalanceId(int balanceId) {
        this.balanceId = balanceId;
    }

    @Override
    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public void setDate(LocalDate date) {
        if (date == null) {
            return;
        }
        this.date = date;
    }

    @Override
    public double getMoney() {
        return this.money;
    }

    @Override
    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    public String getType() {
        return this.type.toString();
    }

    @Override
    public void setType(String type) {
        if (type == null) {
            return;
        }
        this.type = Type.valueOf(type);
    }
}
