package com.maydaymemory.kingdom.listener;

import com.maydaymemory.kingdom.api.EconomyAPI;
import com.maydaymemory.kingdom.model.economy.Account;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class EconomyHandler implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
        EconomyAPI.getInstance().createAccount(Account.Type.PLAYER, event.getPlayer().getName());
    }
}
