package com.maydaymemory.kingdom.model.chunk;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.maydaymemory.kingdom.model.region.PrivateRegion;

import javax.annotation.Nullable;

@DatabaseTable(tableName = "chunk_info")
public class ChunkInfo{
    @DatabaseField(canBeNull = false)
    private String world;
    @DatabaseField(canBeNull = false)
    private int x;
    @DatabaseField(canBeNull = false)
    private int z;
    @DatabaseField(canBeNull = false)
    private boolean isResidentChunk;
    @DatabaseField
    private String privateRegionId;
    @DatabaseField(canBeNull = false)
    private boolean isMainChunk;

    /**The absolute coordinates of the position of the core block*/
    @DatabaseField
    private int cx;
    @DatabaseField
    private int cy;
    @DatabaseField
    private int cz;
    @DatabaseField(id = true)
    private String id;

    private ChunkInfo(){}

    public ChunkInfo(String world, int x, int z){
        this.world = world;
        this.x = x;
        this.z = z;
        id = world + ";" + x + ";" + z;
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

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
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
