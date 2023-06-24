package com.maydaymemory.kingdom.dao.bean.region;

import com.maydaymemory.kingdom.core.util.Pair;
import com.maydaymemory.kingdom.dao.bean.region.AdministrativeRegionBean;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class PrivateRegionBean extends AdministrativeRegionBean {
    protected UUID owner;
    protected Set<UUID> resident = new HashSet<>();
    protected Set<Pair<String, Pair<Integer, Integer>>> residentialChunks = new TreeSet<>();
    protected Pair<String, Pair<Integer, Integer>> mainChunk = null;
    private String name;

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Set<UUID> getResident() {
        return resident;
    }

    public void setResident(Set<UUID> resident) {
        this.resident = resident;
    }

    public Set<Pair<String, Pair<Integer, Integer>>> getResidentialChunks() {
        return residentialChunks;
    }

    public void setResidentialChunks(Set<Pair<String, Pair<Integer, Integer>>> residentialChunks) {
        this.residentialChunks = residentialChunks;
    }

    public Pair<String, Pair<Integer, Integer>> getMainChunk() {
        return mainChunk;
    }

    public void setMainChunk(Pair<String, Pair<Integer, Integer>> mainChunk) {
        this.mainChunk = mainChunk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
