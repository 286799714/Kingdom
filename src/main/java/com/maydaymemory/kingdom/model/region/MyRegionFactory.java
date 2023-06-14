package com.maydaymemory.kingdom.model.region;

import org.bukkit.OfflinePlayer;

public class MyRegionFactory implements RegionFactory{
    @SuppressWarnings("unchecked")
    @Override
    public <T extends AdministrativeRegion> T createRegion(RegionTypeToken<T> token, String id, Object... args) {
        String typeName = token.getType().getTypeName();
        if (PrivateRegion.class.getTypeName().equals(typeName)) {
            return (T) new PrivateRegion(id, (OfflinePlayer) args[0], (String) args[1]);
        }
        return null;
    }
}
