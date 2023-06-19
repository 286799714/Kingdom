package com.maydaymemory.kingdom.event.privateregion;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PrivateRegionCoreInteractEvent extends PrivateRegionCoreEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private boolean cancelled;

    public PrivateRegionCoreInteractEvent(PrivateRegion region, Player player) {
        super(region);
        this.player=player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled=b;
    }
}
