package com.maydaymemory.kingdom.model.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInfoManager {

    private final Map<UUID, PlayerInfo> infoMap = new HashMap<>();

    private PlayerInfoManager(){}

    private static final class InstanceHolder{
        static final PlayerInfoManager instance = new PlayerInfoManager();
    }

    public static PlayerInfoManager getInstance(){
        return InstanceHolder.instance;
    }

    public PlayerInfo getOrCreate(UUID uuid){
        return infoMap.compute(uuid, (k,v) -> {
            if(v != null) return v;
            else return new PlayerInfo(uuid);
        });
    }

    public void removeInfo(UUID uuid){
        infoMap.remove(uuid);
    }

    public Map<UUID, PlayerInfo> getInfoMap(){
        return infoMap;
    }
}
