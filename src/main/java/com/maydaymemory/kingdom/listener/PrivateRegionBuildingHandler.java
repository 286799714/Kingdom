package com.maydaymemory.kingdom.listener;

import com.maydaymemory.kingdom.api.PrivateRegionAPI;
import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.core.language.LanguageInject;
import com.maydaymemory.kingdom.model.player.PlayerInfo;
import com.maydaymemory.kingdom.model.player.PlayerInfoManager;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.Chunk;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Set;

public class PrivateRegionBuildingHandler implements Listener {
    @LanguageInject
    private static Configuration lang;

    @ConfigInject
    private static Configuration config;

    private boolean placeOrBreakCheck(Player player, Chunk chunk){
        if(player.hasPermission("kingdom.admin")) return false;
        if(!config.getStringList("private-region.allow-worlds").contains(chunk.getWorld().getName())) return false;
        PlayerInfo info = PlayerInfoManager.getInstance().getOrCreate(player.getUniqueId());
        Set<PrivateRegion> regions = info.getPrivateRegions();
        PrivateRegion region = PrivateRegionAPI.getInstance().fromChunk(player.getLocation().getChunk());
        if(region == null) return true;
        if(regions.contains(region)) return false;
        return !region.containsResident(player);
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event){
        if(!config.getBoolean("private-region.ban-breaking", true)) return;
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        if(placeOrBreakCheck(player, chunk)) {
            player.sendMessage(lang.getString("region.illegal-break", "region.illegal-break"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event){
        if(!config.getBoolean("private-region.ban-breaking", true)) return;
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        if(placeOrBreakCheck(player, chunk)) {
            player.sendMessage(lang.getString("region.illegal-break", "region.illegal-break"));
            event.setCancelled(true);
        }
    }
}
