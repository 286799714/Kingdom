package com.maydaymemory.kingdom.api;

import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.core.language.LanguageInject;
import com.maydaymemory.kingdom.core.util.Pair;
import com.maydaymemory.kingdom.event.privateregion.*;
import com.maydaymemory.kingdom.model.player.PlayerInfoManager;
import com.maydaymemory.kingdom.model.region.*;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;

import java.util.*;
import java.util.stream.Collectors;

public class PrivateRegionAPI {
    @ConfigInject
    private static Configuration config;
    @LanguageInject
    private static Configuration lang;

    private PrivateRegionAPI(){}

    private static final class InstanceHolder{
        static final PrivateRegionAPI instance = new PrivateRegionAPI();
    }

    public static PrivateRegionAPI getInstance(){
        return InstanceHolder.instance;
    }

    private final Set<Pair<String, UUID>> invitations = new TreeSet<>();

    /**
     * @param owner the owner of new Private Region
     * @param name the name of new Private Region
     * @return the new private region, or null if the creation process was interrupted.
     * */
    public PrivateRegion createPrivateRegion(OfflinePlayer owner, String name){
        int upper = config.getInt("private-region.amount-limit", 1);
        if(PlayerInfoManager.getInstance().getOrCreate(owner.getUniqueId()).getPrivateRegions().size() >= upper) return null;
        PrivateRegion region = RegionManagerProvider.getInstance().getRegionManager().createRegion(PrivateRegion.class, owner, name);
        PrivateRegionCreateEvent event = new PrivateRegionCreateEvent(region);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()){
            RegionManagerProvider.getInstance().getRegionManager().removeRegion(region.getId());
            return null;
        }
        PlayerInfoManager.getInstance().getOrCreate(owner.getUniqueId()).addPrivateRegion(region.getId());
        return region;
    }

    /**
     * @param region the region you want to claim for.
     * @param chunk the chunk you want to claim.
     * @return false if the claim is not successful.
     * */
    public boolean claim(PrivateRegion region, Chunk chunk){
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
        if(chunkInfo.isClaimed()) return false;
        int limit = config.getInt("private-region.claim.limit", 9);
        if(region.getResidentialChunks().size() >= limit) return false;
        if(!isBorder(region, chunk) && region.getResidentialChunks().size() != 0) return false;
        PrivateRegionClaimEvent event = new PrivateRegionClaimEvent(region, chunk);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        Material type = Material.matchMaterial(config.getString("private-region.claim.core-block", "BEACON"));
        if(type == null || !type.isBlock()) return false;
        chunkInfo.setResidentChunk(region);
        if(region.getMainChunk() == null){
            Block block = chunk.getBlock(7,0,7);
            Block hb = chunk.getWorld().getHighestBlockAt(block.getLocation());
            Block target = chunk.getWorld().getBlockAt(hb.getLocation().add(0, 1 ,0));
            moveCoreBlockTo(region, target);
        }
        else region.addResidentialChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        return true;
    }

    /**
     * Returns true only if the target chunk borders on chunks that have already been claimed by the target Private Region
     * */
    public boolean isBorder(PrivateRegion region, Chunk chunk){
        ChunkInfo[] chunksInfo = new ChunkInfo[4];
        chunksInfo[0] = ChunkInfoManager.getInstance().getOrCreate(chunk.getWorld().getName(), chunk.getX() + 1, chunk.getZ());
        chunksInfo[1] = ChunkInfoManager.getInstance().getOrCreate(chunk.getWorld().getName(), chunk.getX(), chunk.getZ() + 1);
        chunksInfo[2] = ChunkInfoManager.getInstance().getOrCreate(chunk.getWorld().getName(), chunk.getX() - 1, chunk.getZ());
        chunksInfo[3] = ChunkInfoManager.getInstance().getOrCreate(chunk.getWorld().getName(), chunk.getX(), chunk.getZ() - 1);
        for(ChunkInfo info : chunksInfo){
            if(!info.isClaimed()) continue;
            if(info.getPrivateRegionId() == null) continue;
            if(region.getId().equals(info.getPrivateRegionId())) return true;
        }
        return false;
    }

    public boolean recant(Chunk chunk){
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
        if(!chunkInfo.isClaimed()) return false;
        if(chunkInfo.getPrivateRegionId() == null) return false;
        PrivateRegion region = PrivateRegionAPI.getInstance().fromId(chunkInfo.getPrivateRegionId());
        if(region == null) return false;
        PrivateRegionRecantEvent event = new PrivateRegionRecantEvent(region, chunk);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        if(chunkInfo.isMainChunk()){
            World world = Bukkit.getWorld(chunkInfo.getWorld());
            if(world == null) return false;
            world.setType(chunkInfo.getCoreX(), chunkInfo.getCoreY(), chunkInfo.getCoreZ(), Material.AIR);
            if(region.getId().equals(chunkInfo.getPrivateRegionId())) region.setMainChunk(null);
        }
        chunkInfo.setMainChunk(false);
        chunkInfo.setResidentChunk(null);
        region.removeResidentialChunk(chunkInfo.getWorld(), chunkInfo.getX(), chunkInfo.getZ());
        return true;
    }

    public boolean moveCoreBlockTo(PrivateRegion region, Block target){
        PrivateRegionCoreMoveEvent event = new PrivateRegionCoreMoveEvent(region, target);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        target = event.getTarget();
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(target.getChunk());
        if(!chunkInfo.isClaimed()) return false;
        if(!region.getId().equals(chunkInfo.getPrivateRegionId())) return false;
        Material type = Material.matchMaterial(config.getString("private-region.claim.core-block", "BEACON"));
        if(type == null || !type.isBlock()) return false;
        Chunk oldChunk = region.getMainChunk();
        if(oldChunk != null) {
            ChunkInfo oldChunkInfo = ChunkInfoManager.getInstance().getOrCreate(oldChunk);
            oldChunkInfo.setMainChunk(false);
            oldChunk.getWorld().setType(oldChunkInfo.getCoreX(), oldChunkInfo.getCoreY(), oldChunkInfo.getCoreZ(), Material.AIR);
        }
        region.setMainChunk(target.getChunk());
        chunkInfo.setMainChunk(true);
        chunkInfo.setCoreX(target.getX());
        chunkInfo.setCoreY(target.getY());
        chunkInfo.setCoreZ(target.getZ());
        target.setType(type);
        return true;
    }

    public boolean invite(PrivateRegion region, OfflinePlayer player){
        if(region.getResident().stream().anyMatch(resident->resident.getUniqueId().equals(player.getUniqueId())))
            return false;
        int limit = config.getInt("private-region.resident-limit", 8);
        if(region.getResident().size() >= limit)
            return false;
        PrivateRegionInviteEvent event = new PrivateRegionInviteEvent(region, player);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        invitations.add(new Pair<>(region.getId(), player.getUniqueId()));
        if(player.getPlayer() != null) player.getPlayer().sendMessage(lang.getString("region.invite-message","region.invite-message"));
        return true;
    }

    public boolean acceptInvitation(PrivateRegion region, OfflinePlayer player){
        List<PrivateRegion> list = PrivateRegionAPI.getInstance().getInvitationList(player);
        if(list.isEmpty() || !list.contains(region))
            return false;
        PrivateRegionInviteAcceptEvent event = new PrivateRegionInviteAcceptEvent(region, player);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        region.addResident(player);
        invitations.remove(new Pair<>(region.getId(), player.getUniqueId()));
        return true;
    }

    public boolean rejectInvitation(PrivateRegion region, OfflinePlayer player){
        List<PrivateRegion> list = PrivateRegionAPI.getInstance().getInvitationList(player);
        if(list.isEmpty() || !list.contains(region))
            return false;
        PrivateRegionInviteRejectEvent event = new PrivateRegionInviteRejectEvent(region, player);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        invitations.remove(new Pair<>(region.getId(), player.getUniqueId()));
        return true;
    }

    public List<PrivateRegion> getInvitationList(OfflinePlayer player){
        return invitations.stream()
                .filter(pair->pair.getLatter().equals(player.getUniqueId()))
                .map(pair->fromId(pair.getFormer()))
                .collect(Collectors.toList());
    }

    public List<OfflinePlayer> getInvitationList(PrivateRegion region){
        return invitations.stream()
                .filter(pair->pair.getFormer().equals(region.getId()))
                .map(pair->Bukkit.getOfflinePlayer(pair.getLatter()))
                .collect(Collectors.toList());
    }

    public PrivateRegion fromName(String name){
        return PrivateRegion.fromName(name);
    }

    public PrivateRegion fromId(String id){
        Region region = RegionManagerProvider.getInstance().getRegionManager().getRegionById(id);
        if(region instanceof PrivateRegion) return (PrivateRegion) region;
        else return null;
    }

    /**
     * @return the Private Region that has claimed this chunk.
     * */
    public PrivateRegion fromChunk(Chunk chunk){
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(chunk);
        if(chunkInfo.isClaimed()){
            return fromId(chunkInfo.getPrivateRegionId());
        }else {
            return null;
        }
    }

    public Set<PrivateRegion> getAllPrivateRegion(){
        return RegionManagerProvider.getInstance().getRegionManager().getRegionMap().values().stream()
                .filter(region -> region instanceof PrivateRegion)
                .map(region -> (PrivateRegion) region)
                .collect(Collectors.toSet());
    }
}
