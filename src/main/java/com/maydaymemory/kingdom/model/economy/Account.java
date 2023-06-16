package com.maydaymemory.kingdom.model.economy;

import java.util.HashMap;
import java.util.Map;

public class Account {
    private Type type;
    private String name;
    HashMap<Currency,Integer> currencies;

    Account(Type type,String name){
        this.type=type;
        this.name=name;
        this.currencies=new HashMap<>();
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int query(Currency currency){
        Integer res=currencies.get(currency);
        return res==null?0:res;
    }

    public boolean withdraw(Currency currency,int number){
        int num=query(currency);
        if(num<number){
            return false;
        }
        currencies.put(currency,num-number);
        return true;
    }

    public void deposit(Currency currency,int number){
        currencies.put(currency,query(currency)+number);
    }
    public Map<Currency, Integer> getCurrencyMap(){
        return currencies;
    }

    public enum Type{
        REGION,PLAYER
    }
}
