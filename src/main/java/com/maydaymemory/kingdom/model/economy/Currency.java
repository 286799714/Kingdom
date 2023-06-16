package com.maydaymemory.kingdom.model.economy;

public class Currency{
    private String id;
    private String displayName;

    Currency(String id,String displayName){
        this.id=id;
        this.displayName=displayName;
    }

    public String getName() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

}
