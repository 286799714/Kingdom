package com.maydaymemory.kingdom.core.command;

public class ParameterMeta {
    private final String name;
    private final String hover;

    public ParameterMeta(String name, String hover){
        this.name = name;
        this.hover = hover;
    }

    public String getName() {
        return name;
    }

    public String getHover() {
        return hover;
    }

    public String toString(){
        return "[" + name + "]";
    }
}
