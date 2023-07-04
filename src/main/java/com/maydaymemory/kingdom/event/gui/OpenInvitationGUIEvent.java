package com.maydaymemory.kingdom.event.gui;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nonnull;

public class OpenInvitationGUIEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancel = false;

    private final PrivateRegion inviterRegion;

    public OpenInvitationGUIEvent(Player player, PrivateRegion inviterRegion) {
        super(player);
        this.inviterRegion = inviterRegion;
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

    public PrivateRegion getInviterRegion() {
        return inviterRegion;
    }
}
