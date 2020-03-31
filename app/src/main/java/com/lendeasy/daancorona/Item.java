package com.lendeasy.daancorona;

import java.sql.Timestamp;

public class Item {
    private String name,Uid,amount;

    public Item(String name, String uid, String amount) {
        this.name = name;
        Uid = uid;
        this.amount = amount;

    }

    public String getName() {
        return name;
    }


    public String getUid() {
        return Uid;
    }

    public String getAmount() {
        return amount;
    }


}
