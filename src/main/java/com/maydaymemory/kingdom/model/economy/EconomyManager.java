package com.maydaymemory.kingdom.model.economy;

import com.maydaymemory.kingdom.PluginKingdom;
import com.maydaymemory.kingdom.core.config.ConfigInject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EconomyManager {
    @ConfigInject(path = "economy.yml")
    private static YamlConfiguration ecoConfig;
    private List<Account> accounts;
    private List<Currency> currencies;

    private EconomyManager() {
        initialize();
    }

    private void initialize() {
        accounts = new ArrayList<>();
        currencies = new ArrayList<>();
    }

    public Currency createCurrency(String id, String displayName) {
        Currency currency = getCurrency(id);
        if (currency != null) {
            return currency;
        }
        currency = new Currency(id, displayName);
        currencies.add(currency);
        for (Account account : accounts) {
            account.deposit(currency, 0);
        }
        return currency;
    }

    public Currency getCurrency(String id) {
        for (Currency c : currencies) {
            if (c.getName().equalsIgnoreCase(id)) {
                return c;
            }
        }
        return null;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public Account getAccount(Account.Type type, String name) {
        for (Account account : accounts) {
            if (account.getType() == type && account.getName().equalsIgnoreCase(name)) {
                return account;
            }
        }
        return null;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Account createAccount(Account.Type type, String name) {
        Account account = getAccount(type, name);
        if (account != null) {
            return account;
        }
        account = new Account(type, name);
        accounts.add(account);
        for (Currency currency : currencies) {
            account.currencies.put(currency, 0);
        }
        return account;
    }

    public void load() {
        initialize();
        ConfigurationSection section = ecoConfig.getConfigurationSection("currencies");
        if (section == null) return;
        for (String c : section.getKeys(false)) {
            createCurrency(c, section.getString(c));
        }
    }

    public void save() throws IOException {
        for (Currency currency : currencies) {
            ecoConfig.set("currencies." + currency.getName(), currency.getDisplayName());
        }
        ecoConfig.save(new File(PluginKingdom.getInstance().getDataFolder(), "economy.yml"));
    }

    private static final EconomyManager manager = new EconomyManager();

    public static EconomyManager getManager() {
        return manager;
    }
}
