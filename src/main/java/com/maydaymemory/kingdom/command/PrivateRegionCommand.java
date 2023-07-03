package com.maydaymemory.kingdom.command;

import com.maydaymemory.kingdom.api.PrivateRegionAPI;
import com.maydaymemory.kingdom.core.command.CommandHandler;
import com.maydaymemory.kingdom.core.command.ParameterSign;
import com.maydaymemory.kingdom.core.command.SubCommand;
import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.model.player.PlayerInfoManager;
import com.maydaymemory.kingdom.model.region.*;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.stream.Stream;

public class PrivateRegionCommand extends BaseCommand{
    @ConfigInject
    private static Configuration config;

    public PrivateRegionCommand() {
        super("privateregion");
        this.setAliases(Collections.singletonList("pr"));
    }

    @CommandHandler(
            playerOnly = true,
            name = "create",
            permission = "kingdom.region.private.create",
            description = "cmd-inf.create-private.description")
    public SubCommand create = new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.create-private.parameter.name",
                hover = "cmd-inf.create-private.parameter.name-hover")
        private String name;
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player player = (Player) sender;
            int upper = config.getInt("private-region.amount-limit", 1);
            if(PlayerInfoManager.getInstance().getOrCreate(player.getUniqueId()).getPrivateRegions().size() >= upper){
                sender.sendMessage(processMessage("cmd-inf.create-private.amount-limit").replaceAll("%limit%", String.valueOf(upper)));
                return;
            }
            if(PrivateRegionAPI.getInstance().fromName(name) != null){
                sender.sendMessage(processMessage("cmd-inf.create-private.name-taken"));
                return;
            }
            if(PrivateRegionAPI.getInstance().createPrivateRegion((OfflinePlayer) sender, name) != null)
                sender.sendMessage(processMessage("cmd-inf.create-private.success").replaceAll("%name%", name));
        }
    };

    @CommandHandler(
            playerOnly = true,
            name = "claim",
            permission = "kingdom.region.private.claim",
            description = "cmd-inf.claim.description")
    public SubCommand claim = new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.claim.parameter.name",
                hover = "cmd-inf.claim.parameter.name-hover"
        )
        private String name;
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player player = (Player) sender;
            Optional<PrivateRegion> optional = getStream(player)
                    .filter(region -> region.getName().equals(name))
                    .findFirst();
            if(optional.isPresent()){
                PrivateRegion region = optional.get();
                Chunk chunk = player.getLocation().getChunk();
                List<String> worlds = config.getStringList("private-region.allow-worlds");
                if(!worlds.contains(chunk.getWorld().getName())) {
                    sender.sendMessage(processMessage("cmd-inf.claim.world-unsupported"));
                    return;
                }
                ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
                if(chunkInfo.isClaimed()){
                    sender.sendMessage(processMessage("cmd-inf.claim.claimed"));
                    return;
                }
                int limit = config.getInt("private-region.claim.limit", 8);
                if(region.getResidentialChunks().size() >= limit) {
                    sender.sendMessage(processMessage("cmd-inf.claim.amount-limit").replaceAll("%limit%", String.valueOf(limit)));
                    return;
                }
                if(!PrivateRegionAPI.getInstance().isBorder(region, chunk)){
                    sender.sendMessage(processMessage("cmd-inf.claim.not-border"));
                    return;
                }
                Material type = Material.matchMaterial(config.getString("private-region.claim.core-block", "BEACON"));
                if(type == null || !type.isBlock()) {
                    sender.sendMessage(processMessage("cmd-inf.core-type-err"));
                    return;
                }
                if(PrivateRegionAPI.getInstance().claim(region, chunk)) {
                    sender.sendMessage(processMessage("cmd-inf.claim.success-living")
                            .replaceAll("%region%", name)
                            .replaceAll("%chunk%", "(" + chunk.getX() + "," + chunk.getZ() + ")"));
                }
            }else {
                sender.sendMessage(processMessage("cmd-inf.claim.not-found"));
            }
        }
        @Override
        public List<String> tabComplete(CommandSender sender, int index){
            if(sender instanceof Player) {
                if (index == 0) {
                    List<String> result = new ArrayList<>();
                    getStream((Player) sender).forEach(region -> result.add(region.getName()));
                    return result;
                }
            }
            return new ArrayList<>();
        }
    };

    @CommandHandler(
            name = "recant",
            playerOnly = true,
            permission = "kingdom.region.private.claim",
            description = "cmd-inf.recant.description"
    )
    public SubCommand recant = new SubCommand() {
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player player = (Player) sender;
            Chunk chunk = player.getLocation().getChunk();
            ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
            if(!chunkInfo.isClaimed() || chunkInfo.getPrivateRegionId() == null){
                sender.sendMessage(processMessage("cmd-inf.recant.not-claimed"));
                return;
            }
            PrivateRegion region = PrivateRegionAPI.getInstance().fromId(chunkInfo.getPrivateRegionId());
            if(region == null || !region.getOwner().getUniqueId().equals(player.getUniqueId())){
                sender.sendMessage(processMessage("cmd-inf.recant.not-claimed"));
                return;
            }
            if(PrivateRegionAPI.getInstance().recant(chunk)){
                sender.sendMessage(processMessage("cmd-inf.recant.success").replaceAll("%region%", region.getName()));
            }
        }
    };

    private Stream<PrivateRegion> getStream(Player player){
        boolean flag = player.hasPermission("kingdom.admin");
        return RegionManagerProvider.getInstance().getRegionManager().getRegionMap().values().stream()
                .filter(region -> region instanceof PrivateRegion)
                .map(region -> (PrivateRegion) region)
                .filter(region -> {
                    if(flag) return true;
                    else return region.getOwner().getUniqueId().equals(player.getUniqueId());
                });
    }

    @CommandHandler(
            playerOnly = true,
            name = "movecore",
            permission = "kingdom.region.private.claim",
            description = "cmd-inf.move-core.description")
    public SubCommand moveCore = new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.move-core.parameter.name",
                hover = "cmd-inf.move-core.parameter.name-hover"
        )
        private String name;

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player player = (Player) sender;
            Optional<PrivateRegion> optional = getStream(player)
                    .filter(region -> region.getName().equals(name))
                    .findFirst();
            if(optional.isPresent()) {
                PrivateRegion region = optional.get();
                Chunk chunk = player.getLocation().getChunk();
                ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
                if(!chunkInfo.isClaimed() || !region.getId().equals(chunkInfo.getPrivateRegionId())){
                    sender.sendMessage(processMessage("cmd-inf.move-core.not-claimed"));
                    return;
                }
                Material type = Material.matchMaterial(config.getString("private-region.claim.core-block", "BEACON"));
                if(type == null || !type.isBlock()) {
                    sender.sendMessage(processMessage("cmd-inf.core-type-err"));
                    return;
                }
                PrivateRegionAPI.getInstance().moveCoreBlockTo(region, chunk.getWorld().getBlockAt(player.getLocation()));
            }else {
                sender.sendMessage(processMessage("cmd-inf.move-core.not-found"));
            }
        }
        @Override
        public List<String> tabComplete(CommandSender sender, int index){
            if(sender instanceof Player) {
                if (index == 0) {
                    List<String> result = new ArrayList<>();
                    getStream((Player) sender).forEach(region -> result.add(region.getName()));
                    return result;
                }
            }
            return new ArrayList<>();
        }
    };


}
