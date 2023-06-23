package com.maydaymemory.kingdom.api;

import com.maydaymemory.kingdom.PluginKingdom;
import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.core.language.LanguageInject;
import com.maydaymemory.kingdom.core.util.Pair;
import com.maydaymemory.kingdom.event.teleport.TeleportPrivateRegionAcceptedEvent;
import com.maydaymemory.kingdom.event.teleport.TeleportPrivateRegionRejectedEvent;
import com.maydaymemory.kingdom.event.teleport.TeleportPrivateRegionRequestEvent;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.chunk.ChunkInfoManager;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TeleportAPI {
    private static final class InstanceHolder {
        static final TeleportAPI instance = new TeleportAPI();
    }

    public static TeleportAPI getInstance() {
        return TeleportAPI.InstanceHolder.instance;
    }

    @ConfigInject
    private static Configuration config;

    @LanguageInject
    private static Configuration lang;

    protected ItemStack teleportItem;
    protected int per;
    protected int cost;
    protected int world;
    private final Set<Pair<UUID, String>> teleportQuests = new HashSet<>();
    private final Set<Pair<UUID, String>> requests = new HashSet<>();
    private final Map<Pair<UUID, String>, Integer> costs = new HashMap<>();

    private int getItem(Player p){
        AtomicInteger count= new AtomicInteger();
        p.getInventory().forEach(i->{
            if(getTeleportItem().equals(i)){
                count.addAndGet(i.getAmount());
            }
        });
        return count.get();
    }

    private void consume(Player p,int cost){
        ItemStack item = TeleportAPI.getInstance().getTeleportItem().clone();
        p.getInventory().remove(TeleportAPI.getInstance().getTeleportItem());
        item.setAmount(Math.max(getItem(p)-cost, 0));
        p.getInventory().addItem(item);
    }

    private void startQuest(Player player, PrivateRegion target, int cost){
        Pair<UUID, String> pair = new Pair<>(player.getUniqueId(), target.getId());
        consume(player, cost);
        teleportQuests.add(pair);
        costs.put(pair, cost);
    }

    private void stopQuest(Player player, PrivateRegion target){
        Pair<UUID, String> pair = new Pair<>(player.getUniqueId(), target.getId());
        ItemStack item = TeleportAPI.getInstance().getTeleportItem().clone();
        item.setAmount(costs.getOrDefault(pair, 0));
        player.getInventory().addItem(item);
        teleportQuests.remove(pair);
        costs.remove(pair);
    }

    public int calculateCost(PrivateRegion start, PrivateRegion to) {
        int result = calculateDistance(start, to);
        result = (result - (result % per)) / per * cost;
        if (start.getMainChunk().getWorld() != to.getMainChunk().getWorld()) {
            result += world;
        }
        return result;
    }

    /**
     * Try to teleport between private regions, the starting point is the private region where the player is located.<br/>
     * Teleport will not execute immediately, a request will be sent to target region's owner first.<br/>
     * Will calculate required items (config.yml 'teleport.item'),
     * if insufficient, teleport request will not be sent.<br/>
     * When the owner of the destination private region is offline, the request will not be sent. <br/>
     * If the destination is the player's own private region, teleport will start immediately.
     * @param player player who is going to send a teleport request.
     * @param target destination private region.
     * @return false if the request is not sent.
     * */
    public boolean startTeleport(Player player, PrivateRegion target){
        PrivateRegion start = PrivateRegionAPI.getInstance().fromChunk(player.getLocation().getChunk());
        if(start == null || target == null) return false;
        int cost = calculateCost(start, target);
        ItemStack item = TeleportAPI.getInstance().getTeleportItem().clone();
        if(!player.getInventory().containsAtLeast(item, cost)) return false;
        OfflinePlayer owner1 = target.getOwner();
        if(owner1 == null) return false;
        Player owner = owner1.getPlayer();
        if(owner == null) return false;
        TeleportPrivateRegionRequestEvent event = new TeleportPrivateRegionRequestEvent(player, target);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        if(owner.getUniqueId().equals(player.getUniqueId())){
            startQuest(player, target, cost);
        }else {
            requests.add(new Pair<>(player.getUniqueId(), target.getId()));
            owner.sendMessage(lang.getString("teleport.request","teleport.request")
                    .replaceAll("%player%", player.getName()).replaceAll("%region%", target.getName()));
            owner.spigot().sendMessage(new ComponentBuilder(lang.getString("teleport.request-agree", "agree"))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/rt yes "+player.getName())).getCurrentComponent());
            owner.spigot().sendMessage(new ComponentBuilder(lang.getString("teleport.request-refuse", "refuse"))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/rt no "+player.getName())).getCurrentComponent());
            new BukkitRunnable(){
                @Override
                public void run() {
                    requests.remove(new Pair<>(player.getUniqueId(), target.getId()));
                }
            }.runTaskLater(PluginKingdom.getInstance(), config.getInt("teleport.request.timeout", 600));
        }
        return true;
    }

    /**
     * @return Returns non-null only when the teleport delay countdown is in progress
     * */
    public PrivateRegion getDestination(Player player){
        Optional<Pair<UUID, String>> optional = teleportQuests.stream()
                .filter(pair -> pair.getFormer().equals(player.getUniqueId()))
                .filter(pair -> PrivateRegionAPI.getInstance().fromId(pair.getLatter()) != null)
                .findFirst();
        return optional
                .map(pair -> PrivateRegionAPI.getInstance().fromId(pair.getLatter()))
                .orElse(null);
    }

    /**
     * @return Returns to its destination when the player has a teleportation request in progress
     * */
    public PrivateRegion getRequestDestination(Player player){
        Optional<Pair<UUID, String>> optional = requests.stream()
                .filter(pair -> pair.getFormer().equals(player.getUniqueId()))
                .filter(pair -> PrivateRegionAPI.getInstance().fromId(pair.getLatter()) != null)
                .findFirst();
        return optional
                .map(pair -> PrivateRegionAPI.getInstance().fromId(pair.getLatter()))
                .orElse(null);
    }

    /**
     * @return true if the request exists and is accepted successfully.
     * */
    public boolean acceptRegionTeleportRequest(Player requester, Player recipient){
        PrivateRegion destination = getRequestDestination(requester);
        if(destination == null) return false;
        if(!destination.getOwner().getUniqueId().equals(recipient.getUniqueId())) return false;
        TeleportPrivateRegionAcceptedEvent event = new TeleportPrivateRegionAcceptedEvent(requester, destination);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        teleportAfterDelay(requester, destination);
        return true;
    }

    /**
     * @return true if the request exists and is rejected successfully.
     * */
    public boolean rejectRegionTeleportRequest(Player requester, Player recipient){
        PrivateRegion destination = getRequestDestination(requester);
        if(destination == null) return false;
        if(!destination.getOwner().getUniqueId().equals(recipient.getUniqueId())) return false;
        TeleportPrivateRegionRejectedEvent event = new TeleportPrivateRegionRejectedEvent(requester, destination);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        requests.remove(new Pair<>(requester.getUniqueId(), destination.getId()));
        return true;
    }

    public void teleportAfterDelay(Player player, PrivateRegion target){
        if(target.getMainChunk() == null) return;
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(target.getMainChunk());
        if(!chunkInfo.isMainChunk()) return;
        PrivateRegion start = PrivateRegionAPI.getInstance().fromChunk(player.getLocation().getChunk());
        if(start == null) return;
        int cost = calculateCost(start, target);
        ItemStack item = TeleportAPI.getInstance().getTeleportItem().clone();
        if(!player.getInventory().containsAtLeast(item, cost)) return;
        startQuest(player, target, cost);
        new BukkitRunnable(){
            @Override
            public void run() {
                if(teleportQuests.contains(new Pair<>(player.getUniqueId(), target.getId()))) {
                    teleport(player, target);
                    teleportQuests.remove(new Pair<>(player.getUniqueId(), target.getId()));
                }
            }
        }.runTaskLater(PluginKingdom.getInstance(), config.getInt("teleport.delay", 100));
    }

    public void teleport(Player player, PrivateRegion target){
        if(target == null) return;
        if(target.getMainChunk() == null) return;
        ChunkInfo chunkInfo = ChunkInfoManager.getInstance().getOrCreate(target.getMainChunk());
        if(!chunkInfo.isMainChunk()) return;
        World world1 = Bukkit.getWorld(chunkInfo.getWorld());
        if(world1 == null) return;
        player.teleport(new Location(world1, chunkInfo.getCoreX(), chunkInfo.getCoreY()+1, chunkInfo.getCoreZ()));
    }

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
