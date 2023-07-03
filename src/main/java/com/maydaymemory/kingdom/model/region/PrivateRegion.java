package com.maydaymemory.kingdom.model.region;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.maydaymemory.kingdom.core.util.Pair;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import org.bukkit.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@DatabaseTable(tableName = "private_region")
public class PrivateRegion extends Region {
    @DatabaseField(canBeNull = false)
    protected UUID owner;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    protected HashSet<UUID> resident = new HashSet<>();

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    protected TreeSet<Pair<String, Pair<Integer, Integer>>> residentialChunks = new TreeSet<>();

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    protected Pair<String, Pair<Integer, Integer>> mainChunk = null;

    @DatabaseField
    private String name;

    protected PrivateRegion(String id, OfflinePlayer owner, String name){
        super(id);
        this.name = id;
        setName(name);
        this.owner = owner.getUniqueId();
    }

    /**For ROM requirements*/
    private PrivateRegion(){
        super(null);
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

    public Set<ChunkInfo> getResidentialChunksInfo(){
        return residentialChunks.stream().map(pair -> ChunkInfoManager.getInstance().getOrCreate(pair)).collect(Collectors.toSet());
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

    public @Nullable Chunk getMainChunk(){
        if(mainChunk == null) return null;
        World world = Bukkit.getWorld(mainChunk.getFormer());
        if(world == null) return null;
        return world.getChunkAt(mainChunk.getLatter().getFormer(), mainChunk.getLatter().getLatter());
    }

    public @Nullable ChunkInfo getMainChunkInfo(){
        if(mainChunk == null) return null;
        return ChunkInfoManager.getInstance().getOrCreate(mainChunk);
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

    public @Nullable Location getCoreLocation(){
        ChunkInfo info = getMainChunkInfo();
        if(info == null) return null;
        World world = Bukkit.getWorld(info.getWorld());
        if(world == null) return null;
        return new Location(world, info.getCoreX(), info.getCoreY(), info.getCoreZ());
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
