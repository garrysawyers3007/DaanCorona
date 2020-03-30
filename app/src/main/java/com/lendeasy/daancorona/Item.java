package com.lendeasy.daancorona;

import java.sql.Timestamp;

public class Item {
    private String name,phonenumber,Uid,amount;
    private Timestamp timestamp;

    public Item(String name, String phonenumber, String uid, String amount, Timestamp timestamp) {
        this.name = name;
        this.phonenumber = phonenumber;
        Uid = uid;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getUid() {
        return Uid;
    }

    public String getAmount() {
        return amount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
