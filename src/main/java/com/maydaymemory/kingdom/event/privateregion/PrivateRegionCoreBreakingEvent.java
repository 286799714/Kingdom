package com.maydaymemory.kingdom.event.privateregion;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Call before the core is broken.
 * */
public class PrivateRegionCoreBreakingEvent extends PrivateRegionCoreEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancel = false;

    private final Player breaker;

    public PrivateRegionCoreBreakingEvent(PrivateRegion region, Player breaker) {
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

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }

    public Player getBreaker() {
        return breaker;
    }
}
