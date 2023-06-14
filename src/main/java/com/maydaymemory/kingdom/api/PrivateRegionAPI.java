package com.maydaymemory.kingdom.api;

import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.event.privateregion.PrivateRegionClaimEvent;
import com.maydaymemory.kingdom.event.privateregion.PrivateRegionCreateEvent;
import com.maydaymemory.kingdom.model.player.PlayerInfoManager;
import com.maydaymemory.kingdom.model.region.*;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PrivateRegionAPI {
    @ConfigInject
    private static Configuration config;
    private PrivateRegionAPI(){}

    private static final class InstanceHolder{
        static final PrivateRegionAPI instance = new PrivateRegionAPI();
    }

    public static PrivateRegionAPI getInstance(){
        return InstanceHolder.instance;
    }

    /**
     * @param owner the owner of new Private Region
     * @param name the name of new Private Region
     * @return the new private region, or null if the creation process was interrupted.
     * */
    public PrivateRegion createPrivateRegion(OfflinePlayer owner, String name){
        int upper = config.getInt("private-region.amount-limit", 1);
        if(PlayerInfoManager.getInstance().getOrCreate(owner.getUniqueId()).getPrivateRegions().size() >= upper) return null;
        RegionTypeToken<PrivateRegion> token = new RegionTypeToken<PrivateRegion>(){};
        PrivateRegion region = RegionManagerProvider.getInstance().getRegionManager().createRegion(token, owner, name);
        PrivateRegionCreateEvent event = new PrivateRegionCreateEvent(region);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()){
            RegionManagerProvider.getInstance().getRegionManager().removeRegion(region.getId());
            return null;
        }
        PlayerInfoManager.getInstance().getOrCreate(owner.getUniqueId()).addPrivateRegion(region.getId());
        return region;
    }

    /**
     * @param region the region you want to claim for.
     * @param chunk the chunk you want to claim.
     * @return false if the claim is not successful.
     * */
    public boolean claim(PrivateRegion region, Chunk chunk){
        List<String> worlds = config.getStringList("private-region.allow-worlds");
        if(!worlds.contains(chunk.getWorld().getName())) return false;
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
        if(chunkInfo.isClaimed()) return false;
        int limit = config.getInt("private-region.claim.limit", 8);
        if(region.getResidentialChunks().size() >= limit) return false;
        Material type = Material.matchMaterial(config.getString("private-region.claim.core-block", "BEACON"));
        if(type == null || !type.isBlock()) return false;
        PrivateRegionClaimEvent event = new PrivateRegionClaimEvent(region, chunk);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        chunkInfo.setResidentChunk(region);
        if(region.getMainChunk() == null){
            region.setMainChunk(chunk);
            chunkInfo.setMainChunk(true);
            chunk.getWorld().setType(chunkInfo.getCoreX(), chunkInfo.getCoreY(), chunkInfo.getCoreZ(), type);
        }
        else region.addResidentialChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        return true;
    }

    public PrivateRegion fromName(String name){
        return PrivateRegion.fromName(name);
    }

    public PrivateRegion fromId(String id){
        AdministrativeRegion region = RegionManagerProvider.getInstance().getRegionManager().getRegionById(id);
        if(region instanceof PrivateRegion) return (PrivateRegion) region;
        else return null;
    }

    public Set<PrivateRegion> getAllPrivateRegion(){
        return RegionManagerProvider.getInstance().getRegionManager().getRegionMap().values().stream()
                .filter(region -> region instanceof PrivateRegion)
                .map(region -> (PrivateRegion) region)
                .collect(Collectors.toSet());
    }
}
