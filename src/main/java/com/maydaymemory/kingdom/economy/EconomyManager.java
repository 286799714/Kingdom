package com.maydaymemory.kingdom.economy;

import com.maydaymemory.kingdom.Reference;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EconomyManager{
    private List<Account> accounts;
    private List<Currency> currencies;
    private YamlConfiguration config;
    private EconomyManager(){
        initialize();
    }

    private void initialize(){
        accounts=new ArrayList<>();
        currencies=new ArrayList<>();
    }

    public Currency createCurrency(String name,String displayName){
        Currency currency=getCurrency(name);
        if (currency!=null){
            return currency;
        }
        currency=new Currency(name,displayName);
        currencies.add(currency);
        for (Account account:accounts){
            account.deposit(currency,0);
        }
        return currency;
    }

    public Currency getCurrency(String name){
        for (Currency c:currencies){
            if(c.getName().equalsIgnoreCase(name)){
                return c;
            }
        }
        return null;
    }

    public Account getAccount(Account.Type type,String name){
        for (Account account:accounts){
            if(account.getType()==type&&account.getName().equalsIgnoreCase(name)){
                return account;
            }
        }
        return null;
    }

    public Account createAccount(Account.Type type,String name){
        Account account=getAccount(type,name);
        if(account!=null){
            return account;
        }
        account=new Account(type,name);
        accounts.add(account);
        for(Currency currency:currencies){
            account.currencies.put(currency,0);
        }
        return account;
    }

    public void load() throws IOException{
        initialize();
        Plugin plugin= Bukkit.getPluginManager().getPlugin(Reference.PLUGIN_NAME);
        File file=new File(plugin.getDataFolder(),"economy.yml");
        if(!file.exists()){
            file.createNewFile();
        }
        this.config=YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section=config.getConfigurationSection("currencies");
        for (String c:section.getKeys(false)){
            createCurrency(c,section.getString(c));
        }
        for(String key:config.getKeys(false)){
            Account.Type type=null;
            if(key.startsWith("p_")){
                type= Account.Type.PLAYER;
            }else if(key.startsWith("r_")){
                type= Account.Type.REGION;
            }else if(key.equalsIgnoreCase("currencies")){
                continue;
            }
            Account account=new Account(type,key.replaceAll("r_","").replaceAll("p_",""));
            section=config.getConfigurationSection(key);
            if(section==null){
                continue;
            }
            for(String c:section.getKeys(false)){
                account.deposit(getCurrency(c),section.getInt(c));
            }
            accounts.add(account);
        }
    }

    public void save() throws IOException {
        for(Currency currency:currencies){
            config.set("currencies."+currency.getName(),currency.getDisplayName());
        }
        for(Account account:accounts){
            String key=(account.getType()== Account.Type.PLAYER?"p_":"r_")+account.getName();
            for(Map.Entry<Currency,Integer> entry:account.getCurrencyMap().entrySet()){
                config.set(key+"."+entry.getKey().getName(),entry.getValue());
            }
        }
        Plugin plugin= Bukkit.getPluginManager().getPlugin(Reference.PLUGIN_NAME);
        config.save(new File(plugin.getDataFolder(),"economy.yml"));
    }

    private static final EconomyManager manager=new EconomyManager();
    public static EconomyManager getManager(){
        return manager;
    }
}
