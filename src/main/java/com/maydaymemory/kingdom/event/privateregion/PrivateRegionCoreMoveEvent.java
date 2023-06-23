package com.maydaymemory.kingdom.event.privateregion;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PrivateRegionCoreMoveEvent extends PrivateRegionCoreEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancel = false;

    private Block target;

    public PrivateRegionCoreMoveEvent(PrivateRegion region, Block target) {
        super(region);
        this.target = target;
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

    public Block getTarget() {
        return target;
    }

    public void setTarget(Block target) {
        this.target = target;
    }
}
