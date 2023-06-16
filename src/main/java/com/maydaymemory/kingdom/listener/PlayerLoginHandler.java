package com.maydaymemory.kingdom.listener;

import com.maydaymemory.kingdom.economy.Account;
import com.maydaymemory.kingdom.economy.EconomyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginHandler implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        Account account=EconomyManager.getManager().getAccount(Account.Type.PLAYER,event.getPlayer().getName());
        if(account==null){
            EconomyManager.getManager().createAccount(Account.Type.PLAYER,event.getPlayer().getName());
        }
    }

}
