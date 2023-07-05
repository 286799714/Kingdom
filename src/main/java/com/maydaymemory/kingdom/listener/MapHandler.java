package com.maydaymemory.kingdom.listener;

import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.model.player.PlayerInfo;
import com.maydaymemory.kingdom.model.player.PlayerInfoManager;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.annotation.Nonnull;
import java.awt.*;

public class MapHandler implements Listener {
    @ConfigInject
    private static Configuration config;

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event){
        event.getMap().addRenderer(new MyMapRenderer());
        Bukkit.getLogger().info("test");
    }

    private static class MyMapRenderer extends MapRenderer {

        @Override
        public void render(@Nonnull MapView mapView, @Nonnull MapCanvas mapCanvas, @Nonnull Player player) {
            PlayerInfo info = PlayerInfoManager.getInstance().getOrCreate(player.getUniqueId());
            if(!info.getPrivateRegions().isEmpty()){
                PrivateRegion region = info.getPrivateRegions().stream().findFirst().orElseThrow(()->new NullPointerException(""));
                region.getResidentialChunks().stream()
                        .map(pair -> {
                            World world = Bukkit.getWorld(pair.getFormer());
                            if(world == null) return null;
                            return world.getChunkAt(pair.getLatter().getFormer(), pair.getLatter().getLatter());
                        })
                        .forEach(chunk->{
                            if(chunk !=null) renderChunk(mapView, mapCanvas, player, chunk, Color.red);
                        });
            }
        }

        private void renderChunk(@Nonnull MapView mapView, @Nonnull MapCanvas mapCanvas, @Nonnull Player player, Chunk chunk, Color color){
            int fromX = chunk.getX() * 16 - mapView.getCenterX() + 63;
            int fromZ = chunk.getZ() * 16 - mapView.getCenterZ() + 63;
            for(int x = fromX; x < fromX + 16; x+=2){
                mapCanvas.setPixelColor(x, fromZ, color);
                mapCanvas.setPixelColor(x, fromZ + 16, color);
            }
            for(int z = fromZ; z < fromZ + 16; z+=2){
                mapCanvas.setPixelColor(fromX, z, color);
                mapCanvas.setPixelColor(fromX + 16, z, color);
            }
        }
    }
}
