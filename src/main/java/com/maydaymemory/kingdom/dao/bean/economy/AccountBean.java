package com.maydaymemory.kingdom.dao.bean.economy;

import com.maydaymemory.kingdom.model.economy.Account;
import com.maydaymemory.kingdom.model.economy.Currency;

import java.util.HashMap;

public class AccountBean {
    private Account.Type type;
    private String name;
    HashMap<Currency,Integer> currencies;

    public Account.Type getType() {
        return type;
    }

    public void setType(Account.Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Currency, Integer> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(HashMap<Currency, Integer> currencies) {
        this.currencies = currencies;
    }
}
