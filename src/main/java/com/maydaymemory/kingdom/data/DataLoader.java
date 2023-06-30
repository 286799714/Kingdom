package com.maydaymemory.kingdom.data;

import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.player.PlayerInfo;
import com.maydaymemory.kingdom.model.region.PrivateRegion;

import java.util.Collection;
import java.util.List;

public interface DataLoader {
    List<PrivateRegion> loadPrivateRegions();

    void savePrivateRegions(Collection<PrivateRegion> regions);

    List<ChunkInfo> loadAllChunkInfo();

    void saveAllChunkInfo(Collection<ChunkInfo> playerInfo);

    List<PlayerInfo> loadAllPlayerInfo();

    void saveAllPlayerInfo(Collection<PlayerInfo> playerInfo);

    void discard();
}
