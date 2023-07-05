package com.maydaymemory.kingdom.event.privateregion;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * This event is called only when player is kicked from a Private Region. Will not be called together with PrivateRegionQuitEvent
 * */
public class PrivateRegionKickEvent extends PrivateRegionEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final OfflinePlayer player;

    private boolean cancel = false;

    public PrivateRegionKickEvent(PrivateRegion region, OfflinePlayer player) {
        super(region);
        this.player = player;
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

    public OfflinePlayer getPlayer() {
        return player;
    }
}
