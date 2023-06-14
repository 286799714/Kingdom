package com.maydaymemory.kingdom.model.region;

import java.util.*;

public abstract class AdministrativeRegion {
    private String root;
    private final Set<String> children = new HashSet<>();
    private final String id;
    protected Set<String> strategies = new HashSet<>();

    protected AdministrativeRegion(String id){
        this.id = id;
    }

    public void setRoot(AdministrativeRegion root){
        if(this.root != null) {
            AdministrativeRegion region = RegionManagerProvider.getInstance().getRegionManager().getRegionById(this.root);
            if(region != null) region.children.remove(id);
        }
        if(root == null) {
            this.root = null;
            return;
        }
        this.root = root.getId();
        root.children.add(id);
    }

    public AdministrativeRegion getRoot() {
        return root == null ? null : RegionManagerProvider.getInstance().getRegionManager().getRegionById(root);
    }

    public void addChild(AdministrativeRegion child){
        child.setRoot(this);
    }

    public void removeChild(AdministrativeRegion child){
        String id = child.getId();
        if(children.contains(id)){
            child.setRoot(null);
            children.remove(id);
        }
    }

    public boolean containsChild(String id) {
        return children.contains(id);
    }

    public Set<AdministrativeRegion> getChildren(){
        Set<AdministrativeRegion> set = new HashSet<>();
        for(String id : children){
            AdministrativeRegion region = RegionManagerProvider.getInstance().getRegionManager().getRegionById(id);
            if(region != null) set.add(region);
        }
        return set;
    }

    public String getId(){
        return id;
    }

    public Set<RegionStrategy> getAllStrategies(){
        Set<RegionStrategy> set = new HashSet<>();
        for(String name : strategies){
            RegionStrategy strategy = RegionManagerProvider.getInstance().getStrategyManager().fromName(name);
            if(strategy != null) set.add(strategy);
        }
        if(getRoot() != null) set.addAll(getRoot().getAllStrategies());
        return set;
    }

    public boolean hasStrategy(String name){
        if(strategies.contains(name)) return true;
        if(getRoot() != null) return getRoot().hasStrategy(name);
        return false;
    }

    public void addStrategy(RegionStrategy strategy){
        strategies.add(strategy.getName());
    }

    public void removeStrategy(RegionStrategy strategy){
        strategies.remove(strategy.getName());
    }

    public Map<String, Integer> getAllState(){
        Map<String, Integer> map = new HashMap<>();
        for(RegionStrategy strategy : getAllStrategies()){
            Map<String, Integer> states = strategy.getAllState();
            for(Map.Entry<String, Integer> entry : states.entrySet()){
                map.compute(entry.getKey(), (k, v)->{
                    if(v == null) return entry.getValue();
                    else return v + entry.getValue();
                });
            }
        }
        return map;
    }
}
