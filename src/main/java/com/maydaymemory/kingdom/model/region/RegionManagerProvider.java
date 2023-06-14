package com.maydaymemory.kingdom.model.region;

public class RegionManagerProvider {
    private RegionManager regionManager = new RegionManager();
    private StrategyManager strategyManager = new StrategyManager();

    private static final class InstanceHolder {
        static final RegionManagerProvider instance = new RegionManagerProvider();
    }

    public static RegionManagerProvider getInstance() {
        return RegionManagerProvider.InstanceHolder.instance;
    }

    public RegionManager getRegionManager(){
        if(regionManager == null) regionManager = new RegionManager();
        return regionManager;
    }

    public StrategyManager getStrategyManager(){
        if(strategyManager == null) strategyManager = new StrategyManager();
        return strategyManager;
    }

    public void setStrategyManager(StrategyManager strategyManager){
        this.strategyManager = strategyManager;
    }
}
