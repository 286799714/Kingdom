package com.maydaymemory.kingdom.dao.bean.chunk;

public class ChunkInfoBean {
    private String world;
    private int x;
    private int z;
    private boolean isResidentChunk;
    private String privateRegionId;
    private boolean isMainChunk;
    /**The absolute coordinates of the position of the core block*/
    private int cx,cy,cz;

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

    public void setResidentChunk(boolean residentChunk) {
        isResidentChunk = residentChunk;
    }

    public String getPrivateRegionId() {
        return privateRegionId;
    }

    public void setPrivateRegionId(String privateRegionId) {
        this.privateRegionId = privateRegionId;
    }

    public boolean isMainChunk() {
        return isMainChunk;
    }

    public void setMainChunk(boolean mainChunk) {
        isMainChunk = mainChunk;
    }

    public int getCx() {
        return cx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public int getCy() {
        return cy;
    }

    public void setCy(int cy) {
        this.cy = cy;
    }

    public int getCz() {
        return cz;
    }

    public void setCz(int cz) {
        this.cz = cz;
    }
}
