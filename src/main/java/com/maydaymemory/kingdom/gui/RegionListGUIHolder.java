package com.maydaymemory.kingdom.gui;

import com.maydaymemory.kingdom.api.PrivateRegionAPI;
import com.maydaymemory.kingdom.api.TeleportAPI;
import com.maydaymemory.kingdom.core.language.LanguageInject;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionListGUIHolder extends GUIHolder {
    @LanguageInject
    private static Configuration lang;
    private ItemStack next;
    private Inventory inventory;
    private String text;
    private PrivateRegion region;
    private List<PrivateRegion> regions;
    private Map<Integer,PrivateRegion> map;
    int page;

    public static final int SIZE=9;

    public RegionListGUIHolder(Player player, String text, PrivateRegion region, int page) {
        super(player);
        this.page = page;
        this.text = text;
        this.region=region;
        search();
        next = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = next.getItemMeta();
        meta.setDisplayName(lang.getString("gui.result.button-next.name")
                .replaceAll("%page%", String.valueOf(page))
                .replaceAll("%total_page%", String.valueOf(mod(regions.size(),SIZE)+1)));
        List<String> lore = new ArrayList<>(lang.getStringList("gui.result.button-next.lore"));
        lore.replaceAll(s ->
                s.replaceAll("%page%", String.valueOf(page))
                        .replaceAll("%total_page%", String.valueOf(mod(regions.size(),SIZE)+1))
        );
        meta.setLore(lore);
        next.setItemMeta(meta);
        createInventory();
    }

    public void createInventory(){
        this.map=new HashMap<>();
        inventory = Bukkit.createInventory(this, SIZE, lang.getString("gui.result.title")
                .replaceAll("%page%", String.valueOf(page))
                .replaceAll("%total_page%", String.valueOf(mod(regions.size(),SIZE)+1)));
        if(regions.size()==0){
            return;
        }
        for (int i = 0; i < SIZE; i++) {
            if(regions.size()==0){
                break;
            }
            if(i==SIZE-1&&regions.size()>1){
                inventory.addItem(next);
                break;
            }
            PrivateRegion region=regions.remove(0);
            inventory.addItem(createRegionItem(region));
            if(regions.size()==1&&getInventory().firstEmpty()==-1){
                map.put(SIZE-1,region);
                break;
            }
            map.put(getInventory().firstEmpty()-1,region);
        }
    }

    @Override
    public void open() {
        player.closeInventory();
        player.openInventory(getInventory());
    }

    @Override
    public void onClick(int slot) {
        if (next.equals(inventory.getItem(slot))){
            this.page++;
            createInventory();
            this.open();
            return;
        }
        PrivateRegion region=map.get(slot);
        if(region==null){
            return;
        }
        player.performCommand("rt to "+region.getName());
        player.closeInventory();
    }

    public ItemStack createRegionItem(PrivateRegion teleport) {
        ItemStack regionItem = new ItemStack(
                teleport.getOwner().getUniqueId().equals(player.getUniqueId()) ? Material.GOLD_BLOCK : Material.DIAMOND_BLOCK);
        ItemMeta meta = regionItem.getItemMeta();
        meta.setDisplayName(lang.getString("gui.result.button-region.name")
                .replaceAll("%name%", teleport.getName())
                .replaceAll("%owner%", teleport.getOwner().getName()));
        List<String> lore = new ArrayList<>();
        for(String str:lang.getStringList("gui.result.button-region.lore")) {
            lore.add(str.replaceAll("%distance%", String.valueOf(TeleportAPI.getInstance().calculateDistance(region, teleport)))
                    .replaceAll("%cost%", String.valueOf(TeleportAPI.getInstance().calculateCost(region, teleport))));
        }
        meta.setLore(lore);
        regionItem.setItemMeta(meta);
        return regionItem;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void search() {
        regions=new ArrayList<>();
        for (PrivateRegion region : PrivateRegionAPI.getInstance().getAllPrivateRegion()) {
            if(region.getMainChunk()==null||region==this.region){
                continue;
            }
            if (region.getName().toLowerCase().contains(text.toLowerCase())){
                regions.add(region);
            }
        }
    }

    private int mod(int a,int b){
        if(a<b){
            return 0;
        }
        if(a%b==0){
            return a/b-1;
        }
        return (a-a%b)/b;
    }

}
