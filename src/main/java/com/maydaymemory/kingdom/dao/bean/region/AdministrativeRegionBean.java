package com.maydaymemory.kingdom.dao.bean.region;

import java.util.HashSet;
import java.util.Set;

public class AdministrativeRegionBean {
    private String root;
    private Set<String> children = new HashSet<>();
    private String id;
    protected Set<String> strategies = new HashSet<>();

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public Set<String> getChildren() {
        return children;
    }

    public void setChildren(Set<String> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getStrategies() {
        return strategies;
    }

    public void setStrategies(Set<String> strategies) {
        this.strategies = strategies;
    }
}
