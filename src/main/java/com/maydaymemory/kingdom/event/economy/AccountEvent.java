package com.maydaymemory.kingdom.event.economy;

import com.maydaymemory.kingdom.model.economy.Account;
import org.bukkit.event.Event;

public abstract class AccountEvent extends Event {
    protected Account account;

    public AccountEvent(Account account){
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
