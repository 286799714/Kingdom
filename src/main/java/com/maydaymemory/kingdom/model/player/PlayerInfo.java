package com.maydaymemory.kingdom.model.player;

import com.maydaymemory.kingdom.model.region.AdministrativeRegion;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import com.maydaymemory.kingdom.model.region.RegionManagerProvider;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerInfo {
    private final UUID uuid;

    private final Set<String> privateRegions = new HashSet<>();

    public PlayerInfo(UUID uuid){
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void addPrivateRegion(String id){
        privateRegions.add(id);
    }

    public void removePrivateRegion(String id){
        privateRegions.remove(id);
    }

    public boolean containsPrivateRegion(String id){
        return privateRegions.contains(id);
    }

    public @Nonnull Set<PrivateRegion> getPrivateRegions(){
        Set<PrivateRegion> regions = new HashSet<>();
        for(String id : privateRegions){
            AdministrativeRegion region = RegionManagerProvider.getInstance().getRegionManager().getRegionById(id);
            if(region instanceof PrivateRegion) regions.add((PrivateRegion) region);
        }
        return regions;
    }
}
