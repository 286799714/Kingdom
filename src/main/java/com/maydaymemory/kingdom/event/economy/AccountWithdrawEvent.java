package com.maydaymemory.kingdom.event.economy;

import com.maydaymemory.kingdom.model.economy.Account;
import com.maydaymemory.kingdom.model.economy.Currency;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class AccountWithdrawEvent extends AccountEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;

    private Currency currency;
    private int amount;

    public AccountWithdrawEvent(Account account, Currency currency, int amount) {
        super(account);
        this.currency = currency;
        this.amount = amount;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
