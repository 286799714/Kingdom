package com.maydaymemory.kingdom;

import com.maydaymemory.kingdom.command.*;
import com.maydaymemory.kingdom.core.command.CommandRegistry;
import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.core.config.ConfigUtil;
import com.maydaymemory.kingdom.core.language.LanguageUtil;
import com.maydaymemory.kingdom.data.DataManager;
import com.maydaymemory.kingdom.data.SQLDataLoader;
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
        RegionManagerProvider.getInstance().getRegionManager().matchFactory(PrivateRegion.class, regionFactory);
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
        DataManager.getInstance().savePrivateRegions();
        DataManager.getInstance().saveAllPlayerInfo();
        DataManager.getInstance().saveAllChunkInfo();
        DataManager.getInstance().getLoader().discard();
    }

    public void loadPlugin(){
        //load configs
        ConfigUtil.saveDefault(this, "config.yml");
        ConfigUtil.saveDefault(this, "economy.yml");
        ConfigUtil.load(this, Reference.GROUP_ID);
        //load language
        LanguageUtil.saveDefault(this);
        String language = config.getString("language");
        if(!LanguageUtil.load(this, language, Reference.GROUP_ID)){
            Bukkit.getLogger().info(ChatColor.RED + "Error to load language configuration: " + language + ".yml");
        }
        //load data
        switch (config.getString("data.type", "SQLite")){
            case "MySQL": {
                SQLDataLoader loader = new SQLDataLoader();
                loader.initial(SQLDataLoader.SQLType.MYSQL);
                DataManager.getInstance().setLoader(loader);
                break;
            }
            case "SQLite":
            default: {
                SQLDataLoader loader = new SQLDataLoader();
                loader.initial(SQLDataLoader.SQLType.SQLITE);
                DataManager.getInstance().setLoader(loader);
                break;
            }
        }
        DataManager.getInstance().loadPrivateRegions();
        DataManager.getInstance().loadAllPlayerInfo();
        DataManager.getInstance().loadAllChunkInfo();
        EconomyManager.getManager().load();
    }


    public static PluginKingdom getInstance(){
        return instance;
    }
}
