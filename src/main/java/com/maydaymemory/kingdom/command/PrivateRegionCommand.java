package com.maydaymemory.kingdom.command;

import com.maydaymemory.kingdom.api.GUIAPI;
import com.maydaymemory.kingdom.api.PrivateRegionAPI;
import com.maydaymemory.kingdom.core.command.CommandHandler;
import com.maydaymemory.kingdom.core.command.ParameterSign;
import com.maydaymemory.kingdom.core.command.SubCommand;
import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.model.player.PlayerInfoManager;
import com.maydaymemory.kingdom.model.region.*;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
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
                if(!PrivateRegionAPI.getInstance().isBorder(region, chunk) && region.getResidentialChunks().size() != 0){
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
                    getStream(sender).forEach(region -> result.add(region.getName()));
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

    private Stream<PrivateRegion> getStream(CommandSender sender){
        return RegionManagerProvider.getInstance().getRegionManager().getRegionMap().values().stream()
                .filter(region -> region instanceof PrivateRegion)
                .map(region -> (PrivateRegion) region)
                .filter(region -> {
                    if(sender.hasPermission("kingdom.admin")) return true;
                    else return (sender instanceof OfflinePlayer) && region.getOwner().getUniqueId().equals(((OfflinePlayer)sender).getUniqueId());
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
                    getStream(sender).forEach(region -> result.add(region.getName()));
                    return result;
                }
            }
            return new ArrayList<>();
        }
    };

    @CommandHandler(
            name = "invite",
            permission = "kingdom.region.private.invite",
            description = "cmd-inf.invite.description"
    )
    public SubCommand invite = new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.invite.parameter.invitee",
                hover = "cmd-inf.invite.parameter.invitee-hover"
        )
        OfflinePlayer invitee;

        @ParameterSign(
                name = "cmd-inf.invite.parameter.region",
                hover = "cmd-inf.invite.parameter.region-hover"
        )
        PrivateRegion region;

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            if(!sender.hasPermission("kingdom.admin")){
                if( !(sender instanceof Player && ((Player) sender).getUniqueId().equals(region.getOwner().getUniqueId())) ) {
                    sender.sendMessage(processMessage("cmd-inf.invite.not-owner"));
                    return;
                }
            }
            if(region.getResident().stream().anyMatch(resident->resident.getUniqueId().equals(invitee.getUniqueId()))){
                sender.sendMessage(processMessage("cmd-inf.invite.resident-already"));
                return;
            }
            if(region.getOwner().getUniqueId().equals(invitee.getUniqueId())){
                sender.sendMessage(processMessage("cmd-inf.invite.resident-already"));
                return;
            }
            int limit = config.getInt("private-region.resident-limit", 8);
            if(region.getResident().size() >= limit){
                sender.sendMessage(processMessage("cmd-inf.invite.resident-limit").replaceAll("%limit%", String.valueOf(limit)));
                return;
            }
            if(PrivateRegionAPI.getInstance().invite(region, invitee)){
                sender.sendMessage(processMessage("cmd-inf.invite.success")
                        .replaceAll("%region%", region.getName())
                        .replaceAll("%player%", String.valueOf(invitee.getName()))
                );
                if(invitee.getPlayer() != null) invitee.getPlayer().sendMessage(processMessage("cmd-inf.invite.invite-message")
                        .replaceAll("%region%", region.getName()));
            }
        }
        @Override
        public List<String> tabComplete(CommandSender sender, int index){
            if(index == 0){
                return Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList());
            }
            if(index == 1){
                return getStream(sender).map(PrivateRegion::getName).collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
    };

    @CommandHandler(
            name = "accept",
            playerOnly = true,
            permission = "kingdom.region.private.accept",
            description = "cmd-inf.accept.description"
    )
    public SubCommand accept = new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.accept.parameter.region",
                hover = "cmd-inf.accept.parameter.region-hover"
        )
        PrivateRegion region;

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player player = (Player) sender;
            List<PrivateRegion> list = PrivateRegionAPI.getInstance().getInvitationList(player);
            if(list.isEmpty() || !list.contains(region)){
                sender.sendMessage(processMessage("cmd-inf.accept.not-invited"));
                return;
            }
            if(PrivateRegionAPI.getInstance().acceptInvitation(region, player)){
                sender.sendMessage(processMessage("cmd-inf.accept.success").replaceAll("%region%", region.getName()));
                if(region.getOwner().getPlayer() != null) region.getOwner().getPlayer().sendMessage(processMessage("cmd-inf.accept.message")
                        .replaceAll("%player%", player.getName())
                        .replaceAll("%region%", region.getName()));
            }
        }
    };

    @CommandHandler(
            name = "reject",
            playerOnly = true,
            permission = "kingdom.region.private.reject",
            description = "cmd-inf.reject.description"
    )
    public SubCommand reject = new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.reject.parameter.region",
                hover = "cmd-inf.reject.parameter.region-hover"
        )
        PrivateRegion region;

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player player = (Player) sender;
            List<PrivateRegion> list = PrivateRegionAPI.getInstance().getInvitationList(player);
            if(list.isEmpty() || !list.contains(region)){
                sender.sendMessage(processMessage("cmd-inf.reject.not-invited"));
                return;
            }
            if(PrivateRegionAPI.getInstance().rejectInvitation(region, player)){
                sender.sendMessage(processMessage("cmd-inf.reject.success").replaceAll("%region%", region.getName()));
                if(region.getOwner().getPlayer() != null) region.getOwner().getPlayer().sendMessage(processMessage("cmd-inf.reject.message")
                        .replaceAll("%player%", player.getName())
                        .replaceAll("%region%", region.getName()));
            }
        }
    };

    @CommandHandler(
            name = "invitations",
            playerOnly = true,
            permission = "kingdom.region.private.info.invitations",
            description = "cmd-inf.invitations.description"
    )
    public SubCommand invitations = new SubCommand() {
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player player = (Player) sender;
            GUIAPI.getInstance().openInvitationListGUI(player);
        }
    };

    @CommandHandler(
            name = "kick",
            permission = "kingdom.region.private.kick",
            description = "cmd-inf.kick.description"
    )
    public SubCommand kick = new SubCommand() {

        @ParameterSign(
                name = "cmd-inf.kick.parameter.region",
                hover = "cmd-inf.kick.parameter.region-hover"
        )
        PrivateRegion region;

        @ParameterSign(
                name = "cmd-inf.kick.parameter.player",
                hover = "cmd-inf.kick.parameter.player-hover"
        )
        OfflinePlayer player;
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            if(!sender.hasPermission("kingdom.admin") &&
                    !(sender instanceof Player && ((Player) sender).getUniqueId().equals(region.getOwner().getUniqueId())))
            {
                sender.sendMessage(processMessage("cmd-inf.kick.not-owner"));
                return;
            }
            if(player.getUniqueId().equals(region.getOwner().getUniqueId())){
                sender.sendMessage(processMessage("cmd-inf.kick.kick-owner"));
                return;
            }
            if(!region.containsResident(player)){
                sender.sendMessage(processMessage("cmd-inf.kick.not-resident"));
                return;
            }
            if(PrivateRegionAPI.getInstance().kick(player, region)){
                sender.sendMessage(processMessage("cmd-inf.kick.success")
                        .replaceAll("%player%", args[1])
                        .replaceAll("%region%", args[0]));
                if(player.getPlayer() != null){
                    player.getPlayer().sendMessage(processMessage("cmd-inf.kick.kick-message")
                            .replaceAll("%region%", args[0]));
                }
            }
        }
        @Override
        public List<String> tabComplete(CommandSender sender, int index){
            if(index == 0){
                return getStream(sender).map(PrivateRegion::getName).collect(Collectors.toList());
            }
            if(index == 1){
                return region.getResident().stream().map(OfflinePlayer::getName).collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
    };

    @CommandHandler(
            name = "quit",
            playerOnly = true,
            permission = "kingdom.region.private.quit",
            description = "cmd-inf.quit.description"
    )
    public SubCommand quit = new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.quit.parameter.region",
                hover = "cmd-inf.quit.parameter.region-hover"
        )
        PrivateRegion region;

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Player player = (Player) sender;
            if(player.getUniqueId().equals(region.getOwner().getUniqueId())){
                sender.sendMessage(processMessage("cmd-inf.quit.owner-quit"));
                return;
            }
            if(!region.containsResident(player)){
                sender.sendMessage(processMessage("cmd-inf.quit.not-resident"));
                return;
            }
            if(PrivateRegionAPI.getInstance().quit(region, player)){
                sender.sendMessage(processMessage("cmd-inf.quit.success")
                        .replaceAll("%region%", region.getName()));
                if(region.getOwner().getPlayer() != null) region.getOwner().getPlayer().sendMessage(processMessage("cmd-inf.quit.message")
                        .replaceAll("%player%", player.getName())
                        .replaceAll("%region%", region.getName()));
            }
        }
        @Override
        public List<String> tabComplete(CommandSender sender, int index){
            if(!(sender instanceof Player)) return new ArrayList<>();
            Player player = (Player) sender;
            if(index == 0){
                return PlayerInfoManager.getInstance().getOrCreate(player.getUniqueId()).getResidences().stream()
                        .map(PrivateRegion::getName)
                        .collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
    };

    @CommandHandler(
            name = "resident",
            permission = "kingdom.region.private.info.resident",
            description = "cmd-inf.resident.description"
    )
    public SubCommand resident = new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.resident.parameter.region",
                hover = "cmd-inf.resident.parameter.region-hover"
        )
        PrivateRegion region;

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            region.getResident().forEach(player -> {
                sender.sendMessage(String.valueOf(player.getName()));
            });
        }
    };
}
