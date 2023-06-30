package com.maydaymemory.kingdom.model.region;

import org.bukkit.OfflinePlayer;

public class MyRegionFactory implements RegionFactory{
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Region> T createRegion(Class<T> clazz, String id, Object... args) {
        String typeName = clazz.getTypeName();
        if (PrivateRegion.class.getTypeName().equals(typeName)) {
            return (T) new PrivateRegion(id, (OfflinePlayer) args[0], (String) args[1]);
        }
        return null;
    }
}
