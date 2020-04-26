package com.codingclub.daancorona;

public class TransactionModel {
    private String name;
    private String amount;
    private int type;

    public TransactionModel(String name, String amount, int type) {
        this.name = name;
        this.amount = amount;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public int getType() {
        return type;
    }
}
