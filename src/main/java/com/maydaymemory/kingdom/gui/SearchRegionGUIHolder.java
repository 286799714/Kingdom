package com.maydaymemory.kingdom.gui;

import com.maydaymemory.kingdom.PluginKingdom;
import com.maydaymemory.kingdom.core.language.LanguageInject;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

public class SearchRegionGUIHolder extends GUIHolder {
    private final AnvilGUI.Builder builder;
    private final PrivateRegion region;

    public SearchRegionGUIHolder(Player player, PrivateRegion region) {
        super(player);
        builder =
                new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    return Arrays.asList(
                            AnvilGUI.ResponseAction.close(),
                            AnvilGUI.ResponseAction.run(()->new RegionListGUIHolder(player, stateSnapshot.getText(), region, 1).open()));
                })
                .itemLeft(button)
                .text(lang.getString("gui.search.text"))
                .title(lang.getString("gui.search.name"))
                .plugin(PluginKingdom.getInstance());
        this.region = region;
    }

    public PrivateRegion getRegion(){
        return region;
    }

    @Override
    public void open() {
        builder.open(player);
    }

    @Deprecated
    @Override
    public void onClick(int slot) {}

    @LanguageInject
    private static Configuration lang;
    private static ItemStack button;

    {
        if (button == null) {
            Material material = Material.matchMaterial(lang.getString("gui.search.material",""));
            if(material != null) {
                button = new ItemStack(material);
                ItemMeta meta = button.getItemMeta();
                if(meta != null) {
                    meta.setDisplayName("");
                    meta.setLore(lang.getStringList("gui.search.lore"));
                    button.setItemMeta(meta);
                }
            }
        }
    }

    @Deprecated
    @Nonnull
    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(null, InventoryType.PLAYER);
    }
}
