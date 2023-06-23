package com.maydaymemory.kingdom.event.teleport;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nonnull;

public class TeleportPrivateRegionRequestEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private PrivateRegion target;

    public TeleportPrivateRegionRequestEvent(Player who, PrivateRegion target) {
        super(who);
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

    public PrivateRegion getTarget() {
        return target;
    }

    public void setTarget(PrivateRegion target) {
        this.target = target;
    }
}
