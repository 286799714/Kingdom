package com.maydaymemory.kingdom.event.privateregion;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Call after the core is broken.
 * */
public class PrivateRegionCoreBrokenEvent extends PrivateRegionCoreEvent{
    private static final HandlerList handlers = new HandlerList();

    private final Player breaker;

    public PrivateRegionCoreBrokenEvent(PrivateRegion region, Player breaker) {
        super(region);
        this.breaker = breaker;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getBreaker() {
        return breaker;
    }
}
