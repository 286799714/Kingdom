package com.maydaymemory.kingdom.api;

import com.maydaymemory.kingdom.Reference;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeleportAPI {
    private static final class InstanceHolder{
        static final TeleportAPI instance = new TeleportAPI();
    }

    public static TeleportAPI getInstance(){
        return TeleportAPI.InstanceHolder.instance;
    }

    private ItemStack item;
    private int per;
    private int cost;
    private int world;
    private TeleportAPI(){
        Configuration config= Bukkit.getPluginManager().getPlugin(Reference.PLUGIN_NAME).getConfig();
        per=config.getInt("teleport.per");
        cost=config.getInt("teleport.cost");
        world=config.getInt("teleport.world");
        item=new ItemStack(Material.matchMaterial(config.getString("teleport.item.material")));
        ItemMeta meta=item.getItemMeta();
        meta.setDisplayName(config.getString("teleport.item.name"));
        meta.setLore(config.getStringList("teleport.item.lore"));
        item.setItemMeta(meta);
    }

    public int calculateDistance(PrivateRegion region1,PrivateRegion region2){
        return (int)Math.pow(Math.pow(region1.getMainChunk().getX()-region2.getMainChunk().getX(),2)
                        +Math.pow(region1.getMainChunk().getZ()-region2.getMainChunk().getZ(),2)
                ,0.5);
    }

    public int calculateCost(PrivateRegion start, PrivateRegion to, Player p){
        if(start.getOwner().getUniqueId().equals(p.getUniqueId())){
            return 0;
        }
        int result=0;
        if(start.getMainChunk().getWorld()!=to.getMainChunk().getWorld()){
            result+=world;
        }
        result+=(calculateDistance(start,to)%per+1)*cost;
        return result;
    }

    public ItemStack getTeleportItem() {
        return item;
    }


}
