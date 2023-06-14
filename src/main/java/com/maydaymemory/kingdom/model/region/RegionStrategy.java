package com.maydaymemory.kingdom.model.region;

import java.util.HashMap;
import java.util.Map;

public class RegionStrategy {
    private final Map<String, Integer> states = new HashMap<>();

    private final String name;

    protected RegionStrategy(String name){
        this.name = name;
    }

    public void addState(String id, int level) {
        states.compute(id, (k, v)->{
            if(v == null) return level;
            else return v + level;
        });
    }

    public void removeState(String id) {
        states.remove(id);
    }

    public void reduceLevel(String id, int level){
        states.computeIfPresent(id, (k, v)->{
            if(v > level) return v - level;
            else return null;
        });
    }

    public Map<String, Integer> getAllState(){
        return states;
    }

    public String getName(){
        return name;
    }
}
