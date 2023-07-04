package com.maydaymemory.kingdom.gui;

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
import java.util.stream.Collectors;

public class InvitationGUIHolder extends GUIHolder{
    @LanguageInject
    private static Configuration lang;
    private final Inventory inventory;
    private final String inviterRegionName;

    public InvitationGUIHolder(Player player, PrivateRegion inviter) {
        super(player);
        inventory = Bukkit.createInventory(this, 9, lang.getString("gui.invitation.title","gui.invitation.title"));
        inviterRegionName = inviter.getName();

        ItemStack acceptButton = new ItemStack(Material.NETHER_STAR);
        ItemMeta acceptButtonMeta = acceptButton.getItemMeta();
        if(acceptButtonMeta != null){
            acceptButtonMeta.setDisplayName(lang.getString("gui.invitation.button-accept.name","gui.invitation.button-accept.name"));
            acceptButtonMeta.setLore(lang.getStringList("gui.invitation.button-accept.lore"));
            acceptButton.setItemMeta(acceptButtonMeta);
        }
        inventory.setItem(0, acceptButton);

        ItemStack rejectButton = new ItemStack(Material.BARRIER);
        ItemMeta rejectButtonMeta = rejectButton.getItemMeta();
        if(rejectButtonMeta != null){
            rejectButtonMeta.setDisplayName(lang.getString("gui.invitation.button-reject.name", "gui.invitation.button-reject.name"));
            rejectButtonMeta.setLore(lang.getStringList("gui.invitation.button-reject.lore"));
            rejectButton.setItemMeta(rejectButtonMeta);
        }
        inventory.setItem(8, rejectButton);

        ItemStack infoButton = new ItemStack(Material.BOOK);
        ItemMeta infoButtonMeta = infoButton.getItemMeta();
        if (infoButtonMeta != null){
            infoButtonMeta.setDisplayName(lang.getString("gui.invitation.button-info.name", "gui.invitation.button-info.name"));
            infoButtonMeta.setLore(lang.getStringList("gui.invitation.button-info.lore").stream()
                    .map(str->str.replaceAll("%region%", inviter.getName()).replaceAll("%owner%", String.valueOf(inviter.getOwner().getName())))
                    .collect(Collectors.toList()));
            infoButton.setItemMeta(infoButtonMeta);
        }
        inventory.setItem(4, infoButton);
    }

    @Override
    public void open() {
        player.closeInventory();
        player.openInventory(getInventory());
    }

    @Override
    public void onClick(int slot) {
        if(slot == 0) {
            player.performCommand("pr accept " + inviterRegionName);
            player.closeInventory();
        }
        if(slot == 8) {
            player.performCommand("pr reject " + inviterRegionName);
            player.closeInventory();
        }
    }

    @Nonnull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
