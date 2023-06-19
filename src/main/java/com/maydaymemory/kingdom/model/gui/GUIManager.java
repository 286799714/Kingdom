package com.maydaymemory.kingdom.model.gui;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager{

    private Map<UUID,GUI> map;

    private GUIManager(){
        map=new HashMap<>();
    }

    public void openGUI(GUI gui){
        closeGUI(gui.getPlayer());
        gui.getPlayer().closeInventory();
        map.put(gui.getPlayer().getUniqueId(),gui);
        gui.getPlayer().openInventory(gui.getInventory());
    }

    public void closeGUI(Player p){
        map.remove(p.getUniqueId());
    }

    public GUI getGUI(Player p){
        return map.get(p.getUniqueId());
    }

    private static GUIManager manager=new GUIManager();
    public static GUIManager getManager() {
        return manager;
    }
}
