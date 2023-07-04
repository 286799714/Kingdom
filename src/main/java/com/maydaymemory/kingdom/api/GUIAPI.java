package com.maydaymemory.kingdom.api;

import com.maydaymemory.kingdom.event.gui.OpenInvitationGUIEvent;
import com.maydaymemory.kingdom.event.gui.OpenInvitationListGUIEvent;
import com.maydaymemory.kingdom.gui.GUIHolder;
import com.maydaymemory.kingdom.gui.InvitationGUIHolder;
import com.maydaymemory.kingdom.gui.InvitationListGUIHolder;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GUIAPI {
    private GUIAPI(){}

    private static final class InstanceHolder{
        static final GUIAPI instance = new GUIAPI();
    }

    public static GUIAPI getInstance(){
        return GUIAPI.InstanceHolder.instance;
    }

    public boolean openInvitationListGUI(Player player){
        OpenInvitationListGUIEvent event = new OpenInvitationListGUIEvent(player);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        GUIHolder holder = new InvitationListGUIHolder(player);
        holder.open();
        return true;
    }

    public boolean openInvitationGUI(Player player, PrivateRegion inviterRegion){
        OpenInvitationGUIEvent event = new OpenInvitationGUIEvent(player, inviterRegion);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        GUIHolder holder = new InvitationGUIHolder(player, inviterRegion);
        holder.open();
        return true;
    }
}
