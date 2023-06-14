package com.maydaymemory.kingdom.model.chunk;

import com.maydaymemory.kingdom.core.util.Pair;
import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.Map;

public class ChunkInfoManager {
    private final Map<Pair<String, Pair<Integer, Integer>>, ChunkInfo> map = new HashMap();
    private ChunkInfoManager(){}

    private static final class InstanceHolder{
        static final ChunkInfoManager instance = new ChunkInfoManager();
    }

    public static ChunkInfoManager getInstance(){
        return ChunkInfoManager.InstanceHolder.instance;
    }

    public ChunkInfo getOrCreate(Chunk chunk){
        return getOrCreate(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public ChunkInfo getOrCreate(Pair<String, Pair<Integer, Integer>> pair){
        return getOrCreate(pair.getFormer(), pair.getLatter().getFormer(), pair.getLatter().getLatter());
    }

    public ChunkInfo getOrCreate(String world, int x, int z){
        Pair<String, Pair<Integer, Integer>> pair = new Pair<>(world, new Pair<>(x, z));
        return map.compute(pair, (k,v) -> {
            if(v != null) return v;
            else return new ChunkInfo(world, x, z);
        });
    }

    public void removeInfo(Chunk chunk){
        removeInfo(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public void removeInfo(String world, int x, int z){
        Pair<String, Pair<Integer, Integer>> pair = new Pair<>(world, new Pair<>(x, z));
        map.remove(pair);
    }

    public Map<Pair<String, Pair<Integer, Integer>>, ChunkInfo> getInfoMap(){
        return map;
    }
}
