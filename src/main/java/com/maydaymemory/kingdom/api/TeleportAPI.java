package com.maydaymemory.kingdom.api;

import com.maydaymemory.kingdom.Reference;
import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeleportAPI {
    private static final class InstanceHolder {
        static final TeleportAPI instance = new TeleportAPI();
    }

    public static TeleportAPI getInstance() {
        return TeleportAPI.InstanceHolder.instance;
    }

    @ConfigInject
    private static Configuration config;

    private ItemStack teleportItem;
    private int per;
    private int cost;
    private int world;

    private TeleportAPI() {
        per = config.getInt("teleport.per");
        cost = config.getInt("teleport.cost");
        world = config.getInt("teleport.world");
        teleportItem = new ItemStack(Material.matchMaterial(config.getString("teleport.item.material")));
        ItemMeta meta = teleportItem.getItemMeta();
        meta.setDisplayName(config.getString("teleport.item.name"));
        meta.setLore(config.getStringList("teleport.item.lore"));
        teleportItem.setItemMeta(meta);
    }

    public int calculateDistance(PrivateRegion region1, PrivateRegion region2) {
        return (int) Math.pow(Math.pow(region1.getMainChunk().getX() - region2.getMainChunk().getX(), 2)
                        + Math.pow(region1.getMainChunk().getZ() - region2.getMainChunk().getZ(), 2)
                , 0.5);
    }

    public int calculateCost(PrivateRegion start, PrivateRegion to) {
        int result = calculateDistance(start, to);
        result = (result - (result % per)) / per * cost;
        if (start.getMainChunk().getWorld() != to.getMainChunk().getWorld()) {
            result += world;
        }
        return result;
    }

    public ItemStack getTeleportItem() {
        return teleportItem;
    }

    public int getPer() {
        return per;
    }

    public int getCost() {
        return cost;
    }

    public int getWorld() {
        return world;
    }
}
