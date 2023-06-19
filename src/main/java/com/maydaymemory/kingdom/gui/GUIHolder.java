package com.maydaymemory.kingdom.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public abstract class GUIHolder implements InventoryHolder{

    protected Player player;

    public GUIHolder(Player player){
        this.player=player;
    }

    public Player getPlayer() {
        return player;
    }

    abstract public void open();

    public abstract void onClick(int slot);
}
