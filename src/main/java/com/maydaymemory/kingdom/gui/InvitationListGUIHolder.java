package com.maydaymemory.kingdom.gui;

import com.maydaymemory.kingdom.api.GUIAPI;
import com.maydaymemory.kingdom.api.PrivateRegionAPI;
import com.maydaymemory.kingdom.core.language.LanguageInject;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class InvitationListGUIHolder extends GUIHolder{
    @LanguageInject
    private static Configuration lang;
    private final Inventory inventory;
    private int page = 1;

    public InvitationListGUIHolder(Player player) {
        super(player);
        inventory = Bukkit.createInventory(this, 9, lang.getString("gui.invitation-list.title","gui.invitation-list.title"));
        fillItem();
    }

    @Override
    public void open() {
        player.closeInventory();
        player.openInventory(getInventory());
    }

    @Override
    public void onClick(int slot) {
        if(slot == 8) {
            page++;
            fillItem();
        }else {
            int index = (page - 1) * 8 + slot;
            player.sendMessage(slot + ";" + index);
            List<PrivateRegion> list = PrivateRegionAPI.getInstance().getInvitationList(player);
            if(index < list.size()) GUIAPI.getInstance().openInvitationGUI(player, list.get(index));
        }
    }

    @Nonnull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void fillItem(){
        ItemStack buttonNext = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta buttonNextMeta = buttonNext.getItemMeta();
        if(buttonNextMeta != null){
            buttonNextMeta.setDisplayName(lang.getString("gui.invitation-list.button-next.name", "gui.invitation-list.button-next.name"));
            buttonNextMeta.setLore(lang.getStringList("gui.invitation-list.button-next.lore").stream()
                    .map(str->str.replaceAll("%page%", String.valueOf(page)))
                    .map(str->str.replaceAll("%total_page%", String.valueOf((PrivateRegionAPI.getInstance().getInvitationList(player).size()-1) / 8 + 1)))
                    .collect(Collectors.toList()));
            buttonNext.setItemMeta(buttonNextMeta);
        }
        inventory.setItem(8, buttonNext);

        for(int i = 0; i < 8; i++) inventory.setItem(i, new ItemStack(Material.AIR));

        List<PrivateRegion> list = PrivateRegionAPI.getInstance().getInvitationList(player);
        for(int i = 0; i < 8; i++){
            int index = (page - 1) * 8 + i;
            if(index >= list.size()) break;
            ItemStack buttonItem = new ItemStack(Material.PAPER);
            ItemMeta buttonItemMeta = buttonItem.getItemMeta();
            if(buttonItemMeta != null){
                buttonItemMeta.setDisplayName(lang.getString("gui.invitation-list.button.name", "gui.invitation-list.button.name")
                        .replaceAll("%region%", list.get(index).getName()));
                buttonItem.setItemMeta(buttonItemMeta);
            }
            inventory.setItem(i, buttonItem);
        }
    }
}
