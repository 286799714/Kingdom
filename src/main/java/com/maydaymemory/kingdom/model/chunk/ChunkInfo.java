package com.maydaymemory.kingdom.model.chunk;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import javax.annotation.Nullable;

public class ChunkInfo{
    private String world;
    private int x;
    private int z;
    private boolean isResidentChunk;
    private String privateRegionId;
    private boolean isMainChunk;
    /**The absolute coordinates of the position of the core block*/
    private int cx,cy,cz;

    public ChunkInfo(String world, int x, int z){
        this.world = world;
        this.x = x;
        this.z = z;
        isResidentChunk = false;
        privateRegionId = null;
        isMainChunk = false;
        cx = 0;
        cy = 0;
        cz = 0;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean isResidentChunk() {
        return isResidentChunk;
    }

    public void setResidentChunk(@Nullable PrivateRegion region) {
        if(region == null) isResidentChunk = false;
        else {
            privateRegionId = region.getId();
            isResidentChunk = true;
        }
    }

    public @Nullable String getPrivateRegionId() {
        return privateRegionId;
    }

    public void setPrivateRegionId(@Nullable String id) {
        if(id == null) this.isResidentChunk = false;
        this.privateRegionId = id;
    }

    public boolean isClaimed(){
        return isResidentChunk||isMainChunk;
    }

    public boolean isMainChunk() {
        return isMainChunk;
    }

    public void setMainChunk(boolean mainChunk) {
        isMainChunk = mainChunk;
    }

    public int getCoreX() {
        return cx;
    }

    public int getCoreY() {
        return cy;
    }

    public int getCoreZ() {
        return cz;
    }

    public void setCoreX(int cx) {
        this.cx = cx;
    }

    public void setCoreY(int cy) {
        this.cy = cy;
    }

    public void setCoreZ(int cz) {
        this.cz = cz;
    }
}
