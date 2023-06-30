package com.maydaymemory.kingdom.model.region;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionManager {
    private final Map<String, Region> regionMap = new HashMap<>();
    private final Map<String, RegionFactory> factoryMap = new HashMap<>();

    RegionManager(){}

    public void matchFactory(Class<?> clazz, RegionFactory factory) {
        factoryMap.put(clazz.getTypeName(), factory);
    }

    public <T extends Region> T createRegion(Class<T> clazz, Object... args) {
        RegionFactory factory = factoryMap.get(clazz.getTypeName());
        if (factory == null) return null;
        UUID uuid = UUID.randomUUID();
        T region = factory.createRegion(clazz, uuid.toString(), args);
        regionMap.put(region.getId(), region);
        return region;
    }

    public Region getRegionById(String id) {
        return regionMap.get(id);
    }

    public void removeRegion(String id){
        regionMap.remove(id);
    }

    public Map<String, Region> getRegionMap(){
        return regionMap;
    }

}