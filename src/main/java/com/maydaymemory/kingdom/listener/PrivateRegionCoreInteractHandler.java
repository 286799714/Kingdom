package com.maydaymemory.kingdom.listener;

import com.maydaymemory.kingdom.event.privateregion.PrivateRegionCoreBreakingEvent;
import com.maydaymemory.kingdom.event.privateregion.PrivateRegionCoreBrokenEvent;
import com.maydaymemory.kingdom.event.privateregion.PrivateRegionCoreInteractEvent;
import com.maydaymemory.kingdom.model.gui.GUI;
import com.maydaymemory.kingdom.model.gui.GUIManager;
import com.maydaymemory.kingdom.model.gui.SearchRegionGUI;
import com.maydaymemory.kingdom.model.region.AdministrativeRegion;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import com.maydaymemory.kingdom.model.region.RegionManagerProvider;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import com.sun.org.apache.regexp.internal.RE;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.AnvilInventory;

public class PrivateRegionCoreInteractHandler implements Listener {
    private boolean isCore(Block block){
        Chunk chunk = block.getChunk();
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
        if(!chunkInfo.isMainChunk()) return false;
        return (block.getX() == chunkInfo.getCoreX() && block.getY() == chunkInfo.getCoreY() && block.getZ() == chunkInfo.getCoreZ());
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event){
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
        if(!chunkInfo.isMainChunk()) return;
        AdministrativeRegion region = RegionManagerProvider.getInstance().getRegionManager().getRegionById(chunkInfo.getPrivateRegionId());
        if(region instanceof PrivateRegion) {
            if (chunkInfo.getCoreX() == block.getX() && chunkInfo.getCoreY() == block.getY() && chunkInfo.getCoreZ() == block.getZ()) {
                PrivateRegionCoreBreakingEvent event1 = new PrivateRegionCoreBreakingEvent((PrivateRegion) region, event.getPlayer());
                Bukkit.getPluginManager().callEvent(event1);
                event.setCancelled(event1.isCancelled());
            }
        }
    }

    @EventHandler
    public void onPlayerBreakingCore(PrivateRegionCoreBreakingEvent event){
        OfflinePlayer player = event.getRegion().getOwner();
        if(player.getUniqueId().equals(event.getBreaker().getUniqueId())){
            event.getBreaker().sendMessage("你不能这样操作");
            event.setCancelled(true);
            return;
        }
        //todo
        PrivateRegionCoreBrokenEvent event1 = new PrivateRegionCoreBrokenEvent(event.getRegion(), event.getBreaker());
        Bukkit.getPluginManager().callEvent(event1);
    }

    @EventHandler
    public void onPlayerBrokenCore(PrivateRegionCoreBrokenEvent event){
        event.getBreaker().sendMessage("你成功破坏了核心");
    }

    //All handlers below are to protect core block.
    @EventHandler
    public void onCoreExtent(BlockPistonExtendEvent event){
        for(Block block : event.getBlocks()){
            if(isCore(block))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCoreRetracted(BlockPistonRetractEvent event){
        for(Block block : event.getBlocks()) {
            if(isCore(block))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCoreBurn(BlockBurnEvent event){
        if(isCore(event.getBlock())) event.setCancelled(true);
    }

    @EventHandler
    public void onCoreFade(BlockFadeEvent event){
        if(isCore(event.getBlock())) event.setCancelled(true);
    }

    @EventHandler
    public void onCoreBeExplodedByBlock(BlockExplodeEvent event){
        for(Block block : event.blockList()){
            if(isCore(block)){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCoreBeExplodedByEntity(EntityExplodeEvent event){
        for(Block block : event.blockList()){
            if(isCore(block)){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCoreBeTradedWhenFertilizing(BlockFertilizeEvent event){
        for(BlockState blockState : event.getBlocks()){
            if(isCore(blockState.getBlock())){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCoreBeTradedWhenStructureGrow(StructureGrowEvent event){
        for(BlockState blockState : event.getBlocks()){
            if(isCore(blockState.getBlock())){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerInteractCore(PlayerInteractEvent event){
        Block block=event.getClickedBlock();
        if(block==null||!isCore(block)){
            return;
        }
        Chunk chunk = block.getChunk();
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
        if(!chunkInfo.isMainChunk()) return;
        AdministrativeRegion region = RegionManagerProvider.getInstance().getRegionManager().getRegionById(chunkInfo.getPrivateRegionId());
        if(region instanceof PrivateRegion) {
            if (chunkInfo.getCoreX() == block.getX() && chunkInfo.getCoreY() == block.getY() && chunkInfo.getCoreZ() == block.getZ()) {
                PrivateRegionCoreInteractEvent event1 = new PrivateRegionCoreInteractEvent((PrivateRegion) region, event.getPlayer());
                Bukkit.getPluginManager().callEvent(event1);
                event.setCancelled(event1.isCancelled());
            }
        }
    }

    @EventHandler
    public void onCoreInteract(PrivateRegionCoreInteractEvent event){
        SearchRegionGUI gui=new SearchRegionGUI(event.getPlayer(),event.getRegion());
        GUIManager.getManager().openGUI(gui);
        event.setCancelled(true);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player)){
            return;
        }
        Player player=(Player)event.getWhoClicked();
        GUI gui=GUIManager.getManager().getGUI(player);
        if(gui==null){
            return;
        }
        event.setCancelled(true);
        if(event.getInventory().getHolder()!=gui){
            return;
        }
        if((gui instanceof SearchRegionGUI)&&event.getClickedInventory().getType()==InventoryType.ANVIL){
            ((SearchRegionGUI)gui).setText(((AnvilInventory)event.getClickedInventory()).getRenameText());
        }
        gui.onClick(event.getSlot());
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event){
        if(!(event.getPlayer() instanceof Player)){
            return;
        }
        GUIManager.getManager().closeGUI((Player)event.getPlayer());
    }
}
