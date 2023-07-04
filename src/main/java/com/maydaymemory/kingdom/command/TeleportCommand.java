package com.maydaymemory.kingdom.command;

import com.maydaymemory.kingdom.api.PrivateRegionAPI;
import com.maydaymemory.kingdom.api.TeleportAPI;
import com.maydaymemory.kingdom.core.command.CommandHandler;
import com.maydaymemory.kingdom.core.command.ParameterSign;
import com.maydaymemory.kingdom.core.command.SubCommand;
import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
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

    @ConfigInject
    private static Configuration config;

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
            Player p = to.getOwner().getPlayer();
            if(p == null) {
                sender.sendMessage(processMessage("cmd-inf.teleport-to.fail-offline"));
                return;
            }
            PrivateRegion start = PrivateRegionAPI.getInstance().fromChunk(((Player)sender).getLocation().getChunk());
            if(start==null) {
                sender.sendMessage(processMessage("cmd-inf.teleport-to.fail-region"));
                return;
            }
            Player p1=(Player) sender;
            if(TeleportAPI.getInstance().getDestination(p1) != null){
                sender.sendMessage(processMessage("cmd-inf.teleport-to.fail-teleporting"));
                return;
            }
            int cost= TeleportAPI.getInstance().calculateCost(start,to);
            ItemStack item = TeleportAPI.getInstance().getTeleportItem().clone();
            if(!p1.getInventory().containsAtLeast(item,cost)){
                p1.sendMessage(processMessage("teleport.fail"));
                return;
            }
            if (TeleportAPI.getInstance().startTeleportRequest(p1, to)) {
                if(p.getUniqueId().equals(p1.getUniqueId())) sender.sendMessage(processMessage("cmd-inf.teleport-to.success2").replaceAll("%delay%", String.valueOf((int)(config.getInt("teleport.delay", 100)/2d)/10d)));
                else sender.sendMessage(processMessage("cmd-inf.teleport-to.success").replaceAll("%player%", p.getName()));
            }
        }
    };

    @CommandHandler(
            playerOnly = true,
            name = "yes",
            permission = "kingdom.teleport",
            description = "cmd-inf.teleport-yes.description")
    public SubCommand yes=new SubCommand() {
        @ParameterSign(
                name = "requester"
        )
        Player requester;

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player player = (Player) sender;
            if(TeleportAPI.getInstance().acceptRegionTeleportRequest(requester, player)) {
                player.sendMessage(processMessage("teleport.agree1"));
                requester.sendMessage(processMessage("teleport.agree2")
                        .replaceAll("%delay%", String.valueOf((int)(config.getInt("teleport.delay", 100)/2d)/10d))
                        .replaceAll("%region%", TeleportAPI.getInstance().getDestination(requester).getName())
                );
            }
        }
    };

    @CommandHandler(
            playerOnly = true,
            name = "no",
            permission = "kingdom.teleport",
            description = "cmd-inf.teleport-no.description")
    public SubCommand no=new SubCommand() {
        @ParameterSign(
                name = "requester"
        )
        Player requester;

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player player = (Player) sender;
            if(TeleportAPI.getInstance().rejectRegionTeleportRequest(requester, player)) {
                player.sendMessage(processMessage("teleport.refuse1"));
                requester.sendMessage(processMessage("teleport.refuse2"));
            }
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
