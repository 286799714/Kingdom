package com.maydaymemory.kingdom.model.region;

import java.util.HashMap;
import java.util.Map;

public class StrategyManager {
    protected Map<String, RegionStrategy> strategiesMap = new HashMap<>();

    protected StrategyManager(){}

    public RegionStrategy fromName(String name){
        return strategiesMap.get(name);
    }

    public RegionStrategy createStrategy(String name, Object... args){
        RegionStrategy strategy = new RegionStrategy(name);
        strategiesMap.put(name, strategy);
        return strategy;
    }
}
