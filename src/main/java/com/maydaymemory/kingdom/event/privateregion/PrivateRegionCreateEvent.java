package com.maydaymemory.kingdom.event.privateregion;


import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PrivateRegionCreateEvent extends PrivateRegionEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancel = false;

    public PrivateRegionCreateEvent(PrivateRegion region) {
        super(region);
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
}
