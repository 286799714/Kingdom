package com.maydaymemory.kingdom.event.privateregion;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.event.Event;

public abstract class PrivateRegionEvent extends Event {
    private final PrivateRegion region;

    public PrivateRegionEvent(PrivateRegion region){
        this.region = region;
    }

    public PrivateRegion getRegion(){
        return region;
    }
}
