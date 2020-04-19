package com.lendeasy.daancorona;

import java.sql.Timestamp;

public class Item {
    private String name,amount;

    public Item(String name, String amount) {
        this.name = name;
        this.amount = amount;

    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }


}
