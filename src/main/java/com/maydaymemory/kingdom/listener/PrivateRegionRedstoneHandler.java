package com.maydaymemory.kingdom.listener;

import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class PrivateRegionRedstoneHandler implements Listener {
    @ConfigInject
    private static Configuration config;

    @EventHandler
    public void onBlockExtent(BlockPistonExtendEvent event){
        if(!config.getBoolean("private-region.ban-breaking", true)) return;
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
        if(!chunkInfo.isClaimed()) return;
        if(chunkInfo.getPrivateRegionId() == null) return;
        int dx = 0, dy = 0, dz = 0;
        switch (event.getDirection()){
            case UP: dy = 1; break;
            case DOWN: dy = -1; break;
            case NORTH: dz = -1; break;
            case SOUTH: dz = 1; break;
            case EAST: dx = 1; break;
            case WEST: dx = -1; break;
        }
        for(Block block1 : event.getBlocks()) {
            Block b = chunk.getWorld().getBlockAt(block1.getX() + dx , block1.getY() + dy, block1.getZ() + dz);
            ChunkInfo chunkInfo2 = ChunkInfoManager.getInstance().getOrCreate(b.getChunk());
            if (!chunkInfo.getPrivateRegionId().equals(chunkInfo2.getPrivateRegionId())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockRetracted(BlockPistonRetractEvent event){
        if(!config.getBoolean("private-region.ban-breaking", true)) return;
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
        if(!chunkInfo.isClaimed()) return;
        if(chunkInfo.getPrivateRegionId() == null) return;
        for(Block b : event.getBlocks()) {
            ChunkInfo chunkInfo2 = ChunkInfoManager.getInstance().getOrCreate(b.getChunk());
            if (!chunkInfo.getPrivateRegionId().equals(chunkInfo2.getPrivateRegionId())) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
