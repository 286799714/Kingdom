package com.maydaymemory.kingdom.economy;

public class Currency{
    private String name;
    private String displayName;

    Currency(String name,String displayName){
        this.name=name;
        this.displayName=displayName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

}
