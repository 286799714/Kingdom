package com.maydaymemory.kingdom.api;

import com.maydaymemory.kingdom.event.economy.AccountDepositEvent;
import com.maydaymemory.kingdom.event.economy.AccountWithdrawEvent;
import com.maydaymemory.kingdom.model.economy.Account;
import com.maydaymemory.kingdom.model.economy.Currency;
import com.maydaymemory.kingdom.model.economy.EconomyManager;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.List;

public class EconomyAPI {
    private EconomyAPI(){}

    private static final class InstanceHolder{
        static final EconomyAPI instance = new EconomyAPI();
    }

    public static EconomyAPI getInstance(){
        return EconomyAPI.InstanceHolder.instance;
    }

    public Currency createCurrency(String id, String display){
        Currency currency= EconomyManager.getManager().getCurrency(id);
        if(currency!=null)
            return null;
        return EconomyManager.getManager().createCurrency(id, display);
    }

    public @Nonnull Account createAccount(Account.Type type, String name){
        return EconomyManager.getManager().createAccount(type, name);
    }

    public Currency getCurrency(String id){
        return EconomyManager.getManager().getCurrency(id);
    }

    public Account getAccount(Account.Type type, String name){
        return EconomyManager.getManager().getAccount(type, name);
    }

    public List<Currency> getAllCurrencies(){
        return EconomyManager.getManager().getCurrencies();
    }

    public List<Account> getAllAccounts(){
        return EconomyManager.getManager().getAccounts();
    }

    public boolean withdraw(Currency currency, Account account, int amount){
        AccountWithdrawEvent event = new AccountWithdrawEvent(account, currency, amount);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return false;
        return account.withdraw(currency,Math.abs(amount));
    }

    public void deposit(Currency currency, Account account, int amount){
        AccountDepositEvent event = new AccountDepositEvent(account, currency, amount);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        account.deposit(currency,Math.abs(amount));
    }
}
