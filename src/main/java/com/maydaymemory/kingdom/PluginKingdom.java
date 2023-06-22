package com.maydaymemory.kingdom;

import com.maydaymemory.kingdom.command.*;
import com.maydaymemory.kingdom.core.command.CommandRegistry;
import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.core.config.ConfigUtil;
import com.maydaymemory.kingdom.core.language.LanguageUtil;
import com.maydaymemory.kingdom.listener.*;
import com.maydaymemory.kingdom.model.economy.EconomyManager;
import com.maydaymemory.kingdom.model.region.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginKingdom extends JavaPlugin {
    private static PluginKingdom instance;

    @ConfigInject
    private static Configuration config;

    @Override
    public void onLoad(){
        instance = this;
        loadPlugin();
    }

    @Override
    public void onEnable(){
        //Command register
        MyProviders.register();
        CommandRegistry.register(new OperatorCommand());
        CommandRegistry.register(new PrivateRegionCommand());
        CommandRegistry.register(new EconomyCommand());
        CommandRegistry.register(new TeleportCommand());
        //Region Factory register
        MyRegionFactory regionFactory = new MyRegionFactory();
        RegionManagerProvider.getInstance().getRegionManager().matchFactory(new RegionTypeToken<PrivateRegion>(){}, regionFactory);
        //Listener register
        Bukkit.getPluginManager().registerEvents(new PrivateRegionBuildingHandler(), this);
        Bukkit.getPluginManager().registerEvents(new PrivateRegionCoreInteractHandler(), this);
        Bukkit.getPluginManager().registerEvents(new PrivateRegionRedstoneHandler(), this);
        Bukkit.getPluginManager().registerEvents(new EconomyHandler(), this);
        Bukkit.getPluginManager().registerEvents(new GUIHandler(), this);
    }

    @Override
    public void onDisable() {
        try {
            EconomyManager.getManager().save();
        }catch (Exception e) {
            Bukkit.getLogger().info(ChatColor.RED + "Fail to save Economy System:");
            e.printStackTrace();
        }
    }

    public void loadPlugin(){
        ConfigUtil.saveDefault(this, "config.yml");
        ConfigUtil.saveDefault(this, "economy.yml");
        ConfigUtil.load(this, "anvilgui");
        LanguageUtil.saveDefault(this);
        String language = config.getString("language");
        if(!LanguageUtil.load(this, language, "anvilgui")){
            Bukkit.getLogger().info(ChatColor.RED + "Error to load language configuration: " + language + ".yml");
        }
        EconomyManager.getManager().load();
    }

    public static PluginKingdom getInstance(){
        return instance;
    }
}
