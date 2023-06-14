package com.maydaymemory.kingdom.event.privateregion;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class PrivateRegionCoreEvent extends PrivateRegionEvent{
    private final Block core;

    public PrivateRegionCoreEvent(PrivateRegion region) {
        super(region);
        if(region == null || region.getMainChunk() == null) {
            core = null;
            return;
        }
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(region.getMainChunk());
        World world = Bukkit.getWorld(chunkInfo.getWorld());
        if(!chunkInfo.isMainChunk() || world == null){
            core = null;
            return;
        }
        core = world.getBlockAt(chunkInfo.getCoreX(), chunkInfo.getCoreY(), chunkInfo.getCoreZ());
    }

    public Block getCore(){
        return core;
    }
}
