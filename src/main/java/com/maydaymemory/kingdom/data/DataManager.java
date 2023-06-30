package com.maydaymemory.kingdom.data;

import com.maydaymemory.kingdom.core.util.Pair;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import com.maydaymemory.kingdom.model.player.PlayerInfo;
import com.maydaymemory.kingdom.model.player.PlayerInfoManager;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import com.maydaymemory.kingdom.model.region.Region;
import com.maydaymemory.kingdom.model.region.RegionManagerProvider;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;
    private DataLoader loader;

    private DataManager(){}

    public static DataManager getInstance(){
        if(instance == null) return instance = new DataManager();
        else return instance;
    }

    public DataLoader getLoader() {
        return loader;
    }

    public void setLoader(DataLoader loader) {
        if(this.loader != null) this.loader.discard();
        this.loader = loader;
    }

    public void loadPrivateRegions(){
        if(loader == null){
            throw new NullPointerException("DataLoader must be set before load region. call \"DataManager.getInstance().setLoader()\"");
        }
        Map<String, Region> map = RegionManagerProvider.getInstance().getRegionManager().getRegionMap();
        map.replaceAll((s, region) -> {
            if(region instanceof PrivateRegion) return null;
            return region;
        });
        loader.loadPrivateRegions().forEach(region -> map.put(region.getId(), region));
    }

    public void savePrivateRegions(){
        if(loader == null){
            throw new NullPointerException("DataLoader must be set before save region. call \"DataManager.getInstance().setLoader()\"");
        }
        loader.savePrivateRegions(
                RegionManagerProvider.getInstance().getRegionManager().getRegionMap().values().stream()
                        .filter(region -> region instanceof PrivateRegion)
                        .map(region -> (PrivateRegion)region)
                        .collect(Collectors.toList())
        );
    }

    public void loadAllChunkInfo(){
        if(loader == null){
            throw new NullPointerException("DataLoader must be set before load chunk info. call \"DataManager.getInstance().setLoader()\"");
        }
        Map<Pair<String, Pair<Integer, Integer>>, ChunkInfo> map = ChunkInfoManager.getInstance().getInfoMap();
        loader.loadAllChunkInfo().forEach(info ->
                map.put(new Pair<>(info.getWorld(), new Pair<>(info.getX(), info.getZ())), info));
    }

    public void saveAllChunkInfo(){
        if(loader == null){
            throw new NullPointerException("DataLoader must be set before save chunk info. call \"DataManager.getInstance().setLoader()\"");
        }
        Map<Pair<String, Pair<Integer, Integer>>, ChunkInfo> map = ChunkInfoManager.getInstance().getInfoMap();
        loader.saveAllChunkInfo(map.values());
    }

    public void loadAllPlayerInfo(){
        if(loader == null){
            throw new NullPointerException("DataLoader must be set before load chunk info. call \"DataManager.getInstance().setLoader()\"");
        }
        Map<UUID, PlayerInfo> map = PlayerInfoManager.getInstance().getInfoMap();
        loader.loadAllPlayerInfo().forEach(info -> map.put(info.getUuid(), info));
    }

    public void saveAllPlayerInfo(){
        if(loader == null){
            throw new NullPointerException("DataLoader must be set before load chunk info. call \"DataManager.getInstance().setLoader()\"");
        }
        Map<UUID, PlayerInfo> map = PlayerInfoManager.getInstance().getInfoMap();
        loader.saveAllPlayerInfo(map.values());
    }
}
