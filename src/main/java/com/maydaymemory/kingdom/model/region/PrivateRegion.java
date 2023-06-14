package com.maydaymemory.kingdom.model.region;

import com.maydaymemory.kingdom.core.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;
import java.util.*;

public class PrivateRegion extends AdministrativeRegion{
    protected UUID owner;
    protected Set<UUID> resident = new HashSet<>();
    protected Set<Pair<String, Pair<Integer, Integer>>> residentialChunks = new TreeSet<>();
    protected Pair<String, Pair<Integer, Integer>> mainChunk = null;
    private String name;

    protected PrivateRegion(String id, OfflinePlayer owner, String name){
        super(id);
        this.name = id;
        setName(name);
        this.owner = owner.getUniqueId();
    }

    public void setOwner(OfflinePlayer owner){
        this.owner = owner.getUniqueId();
    }

    public OfflinePlayer getOwner(){
        return Bukkit.getOfflinePlayer(owner);
    }

    public void addResident(OfflinePlayer player){
        resident.add(player.getUniqueId());
    }

    public void removeResident(OfflinePlayer player){
        resident.remove(player.getUniqueId());
    }

    public Set<OfflinePlayer> getResident(){
        Set<OfflinePlayer> result = new HashSet<>();
        for(UUID uuid : resident){
            result.add(Bukkit.getOfflinePlayer(uuid));
        }
        return result;
    }

    public boolean containsResident(OfflinePlayer player){
        return resident.contains(player.getUniqueId());
    }

    public void addResidentialChunk(String world, int x, int z){
        residentialChunks.add(new Pair<>(world, new Pair<>(x, z)));
    }

    public void removeResidentialChunk(String world, int x, int z){
        residentialChunks.remove(new Pair<>(world, new Pair<>(x, z)));
    }

    public boolean containsResidentialChunk(String world, int x, int z){
        return residentialChunks.contains(new Pair<>(world, new Pair<>(x, z)));
    }

    public Set<Pair<String, Pair<Integer, Integer>>> getResidentialChunks(){
        return residentialChunks;
    }

    public String getName(){
        return name;
    }

    /**
     * The name must be different from all other private regions,
     * otherwise it will not be replaced.
     * */
    public void setName(String name){
        if(fromName(name) == null) this.name = name;
    }

    public @Nullable Pair<String, Pair<Integer, Integer>> getMainChunk(){
        return mainChunk;
    }

    public void setMainChunk(@Nullable Chunk chunk){
        if(chunk == null){
            if(mainChunk != null) residentialChunks.remove(mainChunk);
            mainChunk = null;
            return;
        }
        Pair<String, Pair<Integer, Integer>> pair = new Pair<>(chunk.getWorld().getName(), new Pair<>(chunk.getX(), chunk.getZ()));
        residentialChunks.add(pair);
        mainChunk = pair;
    }

    public static PrivateRegion fromName(String name){
        Optional<PrivateRegion> result = RegionManagerProvider.getInstance().getRegionManager().getRegionMap().values().stream()
                .filter(region->{
                    if(region instanceof PrivateRegion){
                        return ((PrivateRegion) region).getName().equals(name);
                    }
                    return false;
                })
                .map(region-> (PrivateRegion)region)
                .findFirst();
        return result.orElse(null);
    }
}
