package com.maydaymemory.kingdom.event.privateregion;

import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PrivateRegionInviteRejectEvent extends PrivateRegionEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final OfflinePlayer invitee;

    private boolean cancel = false;

    public PrivateRegionInviteRejectEvent(PrivateRegion region, OfflinePlayer invitee) {
        super(region);
        this.invitee = invitee;
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

    public OfflinePlayer getInvitee() {
        return invitee;
    }
}
