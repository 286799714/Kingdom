package com.maydaymemory.kingdom.model.player;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.maydaymemory.kingdom.model.region.Region;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import com.maydaymemory.kingdom.model.region.RegionManagerProvider;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@DatabaseTable(tableName = "player_info")
public class PlayerInfo {
    @DatabaseField(id = true)
    private UUID uuid;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private final HashSet<String> privateRegions = new HashSet<>();
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private final HashSet<String> residence = new HashSet<>();

    private PlayerInfo(){}

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
            Region region = RegionManagerProvider.getInstance().getRegionManager().getRegionById(id);
            if(region instanceof PrivateRegion) regions.add((PrivateRegion) region);
        }
        return regions;
    }

    public void addResidence(String id){
        residence.add(id);
    }

    public void removeResidence(String id){
        residence.remove(id);
    }

    public boolean containsResidence(String id){
        return residence.contains(id);
    }

    public @Nonnull Set<PrivateRegion> getResidences(){
        Set<PrivateRegion> regions = new HashSet<>();
        for(String id : residence){
            Region region = RegionManagerProvider.getInstance().getRegionManager().getRegionById(id);
            if(region instanceof PrivateRegion) regions.add((PrivateRegion) region);
        }
        return regions;
    }
}
