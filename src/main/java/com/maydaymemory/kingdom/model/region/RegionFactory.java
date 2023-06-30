package com.maydaymemory.kingdom.model.region;

public interface RegionFactory{
    <T extends Region> T createRegion(Class<T> clazz, String id, Object... args);
}
