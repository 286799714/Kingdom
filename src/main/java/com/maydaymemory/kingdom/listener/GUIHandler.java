package com.maydaymemory.kingdom.listener;

import com.maydaymemory.kingdom.event.privateregion.PrivateRegionCoreInteractEvent;
import com.maydaymemory.kingdom.gui.GUIHolder;
import com.maydaymemory.kingdom.gui.SearchRegionGUIHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public class GUIHandler implements Listener {

    @EventHandler
    public void onCoreInteract(PrivateRegionCoreInteractEvent event) {
        SearchRegionGUIHolder gui = new SearchRegionGUIHolder(event.getPlayer(), event.getRegion());
        gui.open();
        event.setCancelled(true);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if(event.getClickedInventory()==null){
            return;
        }
        InventoryHolder holder = event.getClickedInventory().getHolder();
        if (!(holder instanceof GUIHolder)) {
            if(event.getInventory().getHolder() instanceof GUIHolder){
                if(event.getClick().isShiftClick() || event.getClick().equals(ClickType.DOUBLE_CLICK) || event.getClick().equals(ClickType.UNKNOWN))
                    event.setCancelled(true);
            }
            return;
        }
        GUIHolder guiHolder = (GUIHolder) holder;
        event.setCancelled(true);
        if(event.getSlot() >= 0) guiHolder.onClick(event.getSlot());
    }

    @EventHandler
    public void onDragInventory(InventoryDragEvent event){
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GUIHolder) event.setCancelled(true);
    }
}
