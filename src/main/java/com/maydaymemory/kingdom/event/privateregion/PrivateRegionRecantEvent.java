package com.maydaymemory.kingdom.event.privateregion;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.Chunk;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PrivateRegionRecantEvent extends PrivateRegionEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Chunk chunk;

    private boolean cancel = false;

    public PrivateRegionRecantEvent(PrivateRegion region, Chunk chunk) {
        super(region);
        this.chunk = chunk;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Chunk getChunk() {
        return chunk;
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
