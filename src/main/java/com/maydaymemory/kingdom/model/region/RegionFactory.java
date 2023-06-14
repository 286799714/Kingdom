package com.maydaymemory.kingdom.model.region;

public interface RegionFactory{
    <T extends AdministrativeRegion> T createRegion(RegionTypeToken<T> token, String id, Object... args);
}
