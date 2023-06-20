package com.maydaymemory.kingdom.command;

import com.maydaymemory.kingdom.api.PrivateRegionAPI;
import com.maydaymemory.kingdom.api.TeleportAPI;
import com.maydaymemory.kingdom.core.command.CommandHandler;
import com.maydaymemory.kingdom.core.command.ParameterSign;
import com.maydaymemory.kingdom.core.command.SubCommand;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TeleportCommand extends BaseCommand{
    public TeleportCommand() {
        super("regionteleport");
        this.setAliases(Collections.singletonList("rt"));
    }

    private static HashMap<UUID,UUID> map=new HashMap<>();
    private static HashMap<UUID,PrivateRegion> startMap=new HashMap<>();
    private static HashMap<UUID,PrivateRegion> toMap=new HashMap<>();


    public static void removeUUID(UUID uuid){
        for(Map.Entry<UUID,UUID> entry: map.entrySet()){
            if(entry.getValue().equals(uuid)){
                map.remove(uuid);
            }
        }
        map.remove(uuid);
        startMap.remove(uuid);
        toMap.remove(uuid);
    }

    @CommandHandler(
            playerOnly = true,
            name = "to",
            permission = "kingdom.teleport",
            description = "cmd-inf.teleport-to.description")
    public SubCommand to=new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.teleport-to.parameter.name",
                hover = "cmd-inf.teleport-to.parameter.name-hover"
        )
        private String regionName;
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            PrivateRegion to= PrivateRegionAPI.getInstance().fromName(regionName);
            if(to==null){
                sender.sendMessage(processMessage("cmd-inf.teleport-to.fail-name"));
                return;
            }
            if(!to.getOwner().isOnline()){
                sender.sendMessage(processMessage("cmd-inf.teleport-to.fail-offline"));
                return;
            }
            PrivateRegion start=PrivateRegionAPI.getInstance().fromChunk(((Player)sender).getLocation().getChunk());
            if(start==null) {
                sender.sendMessage(processMessage("cmd-inf.teleport-to.fail-region"));
                return;
            }
            Player p1=(Player) sender;
            int cost= TeleportAPI.getInstance().calculateCost(start,to);
            ItemStack item=TeleportAPI.getInstance().getTeleportItem().clone();
            if(!p1.getInventory().containsAtLeast(item,cost)){
                p1.sendMessage(processMessage("teleport.fail"));
                return;
            }
            Player p=Bukkit.getPlayer(to.getOwner().getUniqueId());
            if(p.getUniqueId().equals(((Player) sender).getUniqueId())){
                if(!consume(p,cost)){
                    return;
                }
                ChunkInfo info=ChunkInfoManager.getInstance().getOrCreate(to.getMainChunk());
                p.teleport(new Location(to.getMainChunk().getWorld(),info.getCoreX(),info.getCoreY(),info.getCoreZ()));
                return;
            }
            map.put(p.getUniqueId(),((Player) sender).getUniqueId());
            startMap.put(((Player) sender).getUniqueId(),start);
            toMap.put(((Player) sender).getUniqueId(),to);
            p1.sendMessage(processMessage("cmd-inf.teleport-to.success").replaceAll("%player%",p.getName()));
            p.sendMessage(processMessage("teleport.request").replaceAll("%player%",sender.getName()).replaceAll("%region%",to.getName()));
            p.spigot().sendMessage(new ComponentBuilder(processMessage("teleport.request-agree")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/rt yes")).getCurrentComponent());
            p.spigot().sendMessage(new ComponentBuilder(processMessage("teleport.request-refuse")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/rt no")).getCurrentComponent());
        }
    };

    @CommandHandler(
            playerOnly = true,
            name = "yes",
            permission = "kingdom.teleport",
            description = "cmd-inf.teleport-yes.description")
    public SubCommand yes=new SubCommand() {
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player p=(Player) sender;
            UUID uuid=map.get(p.getUniqueId());
            if(uuid==null){
                sender.sendMessage(processMessage("teleport.null"));
                return;
            }
            Player p1=Bukkit.getPlayer(uuid);
            if(p1==null){
                removeUUID(p.getUniqueId());
                sender.sendMessage(processMessage("teleport.null"));
                return;
            }
            PrivateRegion start=startMap.get(p1.getUniqueId());
            PrivateRegion to=toMap.get(p1.getUniqueId());
            int cost= TeleportAPI.getInstance().calculateCost(start,to);
            ItemStack item=TeleportAPI.getInstance().getTeleportItem().clone();
            if(!p1.getInventory().containsAtLeast(item,cost)){
                p1.sendMessage(processMessage("teleport.fail"));
                return;
            }
            removeUUID(p1.getUniqueId());
            p.sendMessage(processMessage("teleport.agree1"));
            if(!consume(p1,cost)){
                return;
            }
            p1.sendMessage(processMessage("teleport.agree2"));
            ChunkInfo info=ChunkInfoManager.getInstance().getOrCreate(to.getMainChunk());
            p1.teleport(new Location(to.getMainChunk().getWorld(),info.getCoreX(),info.getCoreY(),info.getCoreZ()));
        }
    };

    private int getItem(Player p){
        AtomicInteger count= new AtomicInteger();
        p.getInventory().forEach(i->{
            if(item.equals(i)){
                count.addAndGet(i.getAmount());
            }
        });
        return count.get();
    }

    private boolean consume(Player p,int cost){
        ItemStack item=TeleportAPI.getInstance().getTeleportItem().clone();
        if(!p.getInventory().containsAtLeast(item,cost)){
            p.sendMessage(processMessage("teleport.fail"));
            return false;
        }
        p.getInventory().remove(TeleportAPI.getInstance().getTeleportItem());
        item.setAmount(getItem(p)-cost);
        p.getInventory().addItem(item);
        return true;
    }

    @CommandHandler(
            playerOnly = true,
            name = "no",
            permission = "kingdom.teleport",
            description = "cmd-inf.teleport-no.description")
    public SubCommand no=new SubCommand() {
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player p=(Player) sender;
            UUID uuid=map.get(p.getUniqueId());
            if(uuid==null){
                sender.sendMessage(processMessage("teleport.null"));
                return;
            }
            removeUUID(p.getUniqueId());
            Player p1=Bukkit.getPlayer(uuid);
            if(p1==null){
                sender.sendMessage(processMessage("teleport.null"));
                return;
            }
            sender.sendMessage(processMessage("teleport.refuse1"));
            p1.sendMessage("teleport.refuse2");
        }
    };

    @CommandHandler(
            playerOnly = true,
            name = "item",
            permission = "kingdom.teleport.item",
            description = "cmd-inf.teleport-item.description"
    )
    public SubCommand item=new SubCommand() {
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player p=(Player) sender;
            ItemStack item=TeleportAPI.getInstance().getTeleportItem().clone();
            item.setAmount(64);
            p.getInventory().addItem(item);
        }
    };

}
