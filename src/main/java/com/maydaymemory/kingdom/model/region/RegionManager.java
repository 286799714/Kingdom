package com.maydaymemory.kingdom.model.region;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionManager {
    private final Map<String, AdministrativeRegion> regionMap = new HashMap<>();
    private final Map<String, RegionFactory> factoryMap = new HashMap<>();

    RegionManager(){}

    public void matchFactory(RegionTypeToken<?> token, RegionFactory factory) {
        factoryMap.put(token.getType().getTypeName(), factory);
    }

    public <T extends AdministrativeRegion> T createRegion(RegionTypeToken<T> token, Object... args) {
        RegionFactory factory = factoryMap.get(token.getType().getTypeName());
        if (factory == null) return null;
        UUID uuid = UUID.randomUUID();
        T region = factory.createRegion(token, uuid.toString(), args);
        regionMap.put(region.getId(), region);
        return region;
    }

    public AdministrativeRegion getRegionById(String id) {
        return regionMap.get(id);
    }

    public void removeRegion(String id){
        regionMap.remove(id);
    }

    public Map<String, AdministrativeRegion> getRegionMap(){
        return regionMap;
    }
}