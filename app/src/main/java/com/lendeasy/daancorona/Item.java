package com.lendeasy.daancorona;

import java.sql.Timestamp;

public class Item {
    private String name,amount,uid;

    public Item(String name, String amount,String uid) {
        this.name = name;
        this.amount = amount;
        this.uid=uid;

    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getUid() {
        return uid;
    }
}
