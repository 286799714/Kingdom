package com.maydaymemory.kingdom.model.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public abstract class GUI implements InventoryHolder{

    protected Player player;

    public GUI(Player player){
        this.player=player;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract void onClick(int slot);
}
