package com.maydaymemory.kingdom.model.gui;

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
import java.util.List;

public class RegionListGUIHolder extends GUIHolder {
    @LanguageInject
    private static Configuration lang;
    private ItemStack next;
    private Inventory inventory;
    private String text;
    private PrivateRegion region;
    private List<PrivateRegion> regions;
    int page;

    public RegionListGUIHolder(Player player, String text, PrivateRegion region, int page) {
        super(player);
        search();
        this.page = page;
        this.text = text;
        inventory = Bukkit.createInventory(this, 54, lang.getString("gui.result.title")
                .replaceAll("%page%", String.valueOf(page))
                .replaceAll("%total_page%", String.valueOf(page % 54 + 1)));
        for (int i = page * 54 - 54; i < page * 54; i++) {
            if (i > regions.size()) {
                inventory.setItem(53, next);
                break;
            }
            inventory.setItem(i, createRegionItem(regions.get(i)));
        }
        next = new ItemStack(Material.matchMaterial(lang.getString("gui.search.material")));
        ItemMeta meta = next.getItemMeta();
        meta.setDisplayName(lang.getString("gui.result.button-next.name")
                .replaceAll("%page%", String.valueOf(page))
                .replaceAll("%total_page%", String.valueOf(page % 54 + 1)));
        List<String> lore = new ArrayList<>(lang.getStringList("gui.result.button-next.lore"));
        lore.replaceAll(s ->
                s.replaceAll("%page%", String.valueOf(page))
                        .replaceAll("%total_page%", String.valueOf(page % 54 + 1))
        );
        meta.setLore(lore);
        next.setItemMeta(meta);
    }

    @Override
    public void open() {
        player.closeInventory();
        player.openInventory(getInventory());
    }

    @Override
    public void onClick(int slot) {
        if (regions.size() == 0) {
            return;
        }
        if (inventory.getItem(slot).equals(next)) {
            player.closeInventory();
            new RegionListGUIHolder(player, text, region, page + 1).open();
            return;
        }
        PrivateRegion region = regions.get(slot + (page - 1) * 54);

    }

    public ItemStack createRegionItem(PrivateRegion teleport) {
        ItemStack regionItem = new ItemStack(Material.matchMaterial(lang.getString("gui.region.material")));
        ItemMeta meta = regionItem.getItemMeta();
        meta.setDisplayName(lang.getString("gui.result.button-region.name")
                .replaceAll("%name%", teleport.getName())
                .replaceAll("%owner%", teleport.getOwner().getName()));
        List<String> lore = new ArrayList<>(lang.getStringList("gui.result.button-region.lore"));
        lore.replaceAll(s ->
                s.replaceAll("%distance%", String.valueOf(TeleportAPI.getInstance().calculateDistance(region, teleport)))
                        .replaceAll("%cost%", String.valueOf(TeleportAPI.getInstance().calculateCost(region, teleport, player)))
        );
        meta.setLore(lore);
        regionItem.setItemMeta(meta);
        return regionItem;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void search() {
        for (PrivateRegion region : PrivateRegionAPI.getInstance().getAllPrivateRegion()) {
            if (region.getName().toLowerCase().contains(text.toLowerCase())) {
                regions.add(region);
            }
        }
        for (int i = 1; i < regions.size() % 54; i++) {
            regions.add(i * regions.size() - 1, null);
        }
    }

}
