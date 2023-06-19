package com.maydaymemory.kingdom.model.gui;

import com.maydaymemory.kingdom.core.language.LanguageInject;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SearchRegionGUI extends GUI{

    private Inventory inventory;
    private PrivateRegion region;
    private String text;

    public SearchRegionGUI(Player player,PrivateRegion region){
        super(player);
        inventory=/*(AnvilInventory)*/ Bukkit.createInventory(this, InventoryType.ANVIL);
        inventory.setItem(0,search);
        this.region=region;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void onClick(int slot) {
        if(slot!=0&&slot!=2){
            return;
        }
        GUIManager.getManager().closeGUI(player);
        GUIManager.getManager().openGUI(new RegionListGUI(player,text,region,1));
    }
    @LanguageInject
    private static Configuration lang;
    private static ItemStack search;

    {
        if(search==null) {
            search = new ItemStack(Material.matchMaterial(lang.getString("gui.search.material")));
            ItemMeta meta=search.getItemMeta();
            meta.setDisplayName(lang.getString("gui.search.name"));
            meta.setLore(lang.getStringList("gui.search.lore"));
            search.setItemMeta(meta);
        }
    }

}
