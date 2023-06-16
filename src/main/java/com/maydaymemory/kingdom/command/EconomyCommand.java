package com.maydaymemory.kingdom.command;

import com.maydaymemory.kingdom.api.EconomyAPI;
import com.maydaymemory.kingdom.core.command.CommandHandler;
import com.maydaymemory.kingdom.core.command.ParameterSign;
import com.maydaymemory.kingdom.core.command.SubCommand;
import com.maydaymemory.kingdom.model.economy.Account;
import com.maydaymemory.kingdom.model.economy.Currency;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class EconomyCommand extends BaseCommand{
    public EconomyCommand() {
        super("kingdomeconomy");
        this.setAliases(Collections.singletonList("ke"));
    }
    @CommandHandler(
            name = "create",
            permission = "kingdom.economy.create",
            description = "cmd-inf.eco-create.description")
    public SubCommand create=new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.eco-create.parameter.name",
                hover = "cmd-inf.eco-create.parameter.name-hover"
        )
        private String id;
        @ParameterSign(
                name = "cmd-inf.eco-create.parameter.display",
                hover = "cmd-inf.eco-create.parameter.display-hover"
        )
        private String display;
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Currency currency=EconomyAPI.getInstance().getCurrency(id);
            if(currency!=null){
                sender.sendMessage(processMessage("cmd-inf.eco-create.exist"));
                return;
            }
            currency= EconomyAPI.getInstance().createCurrency(id, display.replaceAll("&", "§"));
            sender.sendMessage(processMessage("cmd-inf.eco-create.success",currency));
        }
    };

    @CommandHandler(
            name = "withdraw",
            permission = "kingdom.economy.withdraw",
            description = "cmd-inf.eco-withdraw.description")
    public SubCommand withdraw=new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.eco-withdraw.parameter.name",
                hover = "cmd-inf.eco-withdraw.parameter.name-hover"
        )
        private String name;
        @ParameterSign(
                name = "cmd-inf.eco-withdraw.parameter.type",
                hover = "cmd-inf.eco-withdraw.parameter.type-hover"
        )
        private String type;
        @ParameterSign(
                name = "cmd-inf.eco-withdraw.parameter.account",
                hover = "cmd-inf.eco-withdraw.parameter.account-hover"
        )
        private String account;
        @ParameterSign(
                name = "cmd-inf.eco-withdraw.parameter.amount",
                hover = "cmd-inf.eco-withdraw.parameter.amount-hover"
        )
        private int amount;
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Currency currency=EconomyAPI.getInstance().getCurrency(name);
            if(currency==null){
                sender.sendMessage(processMessage("cmd-inf.eco-withdraw.name-error"));
                return;
            }
            Account.Type type=null;
            try{
                type=Account.Type.valueOf(this.type);
            }catch (Exception e){
                sender.sendMessage(processMessage("cmd-inf.eco-withdraw.type-error"));
                return;
            }
            Account account=EconomyAPI.getInstance().getAccount(type,this.account);
            if(account==null){
                sender.sendMessage(processMessage("cmd-inf.eco-withdraw.account-error"));
                return;
            }
            if(!EconomyAPI.getInstance().withdraw(currency, account, amount)){
                sender.sendMessage(processMessage("cmd-inf.eco-withdraw.amount-error"));
                return;
            }
            sender.sendMessage(processMessage("cmd-inf.eco-withdraw.success",currency,account,Math.abs(amount)));
        }

        @Override
        public List<String> tabComplete(CommandSender sender, int index) {
            if(index==0){
                return EconomyAPI.getInstance().getAllCurrencies().stream().map(Currency::getName).collect(Collectors.toList());
            }
            if(index==1){
                return Arrays.asList("PLAYER","REGION");
            }
            if(index==2){
                return EconomyAPI.getInstance().getAllAccounts().stream().map(Account::getName).collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
    };

    @CommandHandler(
            name = "deposit",
            permission = "kingdom.economy.deposit",
            description = "cmd-inf.eco-deposit.description")
    public SubCommand deposit=new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.eco-deposit.parameter.name",
                hover = "cmd-inf.eco-deposit.parameter.name-hover"
        )
        private String name;
        @ParameterSign(
                name = "cmd-inf.eco-deposit.parameter.type",
                hover = "cmd-inf.eco-deposit.parameter.type-hover"
        )
        private String type;
        @ParameterSign(
                name = "cmd-inf.eco-deposit.parameter.account",
                hover = "cmd-inf.eco-deposit.parameter.account-hover"
        )
        private String account;
        @ParameterSign(
                name = "cmd-inf.eco-deposit.parameter.amount",
                hover = "cmd-inf.eco-deposit.parameter.amount-hover"
        )
        private int amount;
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Currency currency=EconomyAPI.getInstance().getCurrency(name);
            if(currency==null){
                sender.sendMessage(processMessage("cmd-inf.eco-deposit.name-error"));
                return;
            }
            Account.Type type=null;
            try{
                type=Account.Type.valueOf(this.type);
            }catch (Exception e){
                sender.sendMessage(processMessage("cmd-inf.eco-deposit.type-error"));
                return;
            }
            Account account=EconomyAPI.getInstance().getAccount(type,this.account);
            if(account==null){
                sender.sendMessage(processMessage("cmd-inf.eco-deposit.account-error"));
                return;
            }
            EconomyAPI.getInstance().deposit(currency, account, amount);
            sender.sendMessage(processMessage("cmd-inf.eco-deposit.success",currency,account,Math.abs(amount)));
        }

        @Override
        public List<String> tabComplete(CommandSender sender, int index) {
            if(index==0){
                return EconomyAPI.getInstance().getAllCurrencies().stream().map(Currency::getName).collect(Collectors.toList());
            }
            if(index==1){
                return Arrays.asList("PLAYER","REGION");
            }
            if(index==2){
                return EconomyAPI.getInstance().getAllAccounts().stream().map(Account::getName).collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
    };

    @CommandHandler(
            name = "query",
            permission = "kingdom.economy.query",
            description = "cmd-inf.eco-query.description")
    public SubCommand query=new SubCommand() {
        @ParameterSign(
                name = "cmd-inf.eco-query.parameter.type",
                hover = "cmd-inf.eco-query.parameter.type-hover"
        )
        private String type;
        @ParameterSign(
                name = "cmd-inf.eco-query.parameter.account",
                hover = "cmd-inf.eco-query.parameter.account-hover"
        )
        private String account;
        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            Account.Type type=null;
            try{
                type=Account.Type.valueOf(this.type);
            }catch (Exception e){
                sender.sendMessage(processMessage("cmd-inf.eco-query.type-error"));
                return;
            }
            Account account = EconomyAPI.getInstance().getAccount(type,this.account);
            if(account==null){
                sender.sendMessage(processMessage("cmd-inf.eco-query.account-error"));
                return;
            }
            sender.sendMessage(processMessage("cmd-inf.eco-query.success-1").replaceAll("%account%",account.getName()));
            for(Map.Entry<Currency,Integer> entry:account.getCurrencyMap().entrySet()){
                sender.sendMessage(processMessage("cmd-inf.eco-query.success-2").replaceAll("%display%",entry.getKey().getDisplayName()).replaceAll("%amount%",entry.getValue().toString()));
            }
        }

        @Override
        public List<String> tabComplete(CommandSender sender, int index) {
            if(index==0){
                return Arrays.asList("PLAYER","REGION");
            }
            if(index==1){
                return EconomyAPI.getInstance().getAllAccounts().stream().map(Account::getName).collect(Collectors.toList());
            }
            return new ArrayList<>();
        }
    };


    public String processMessage(String message,Currency currency){
        return processMessage(message).replaceAll("%display%",currency.getDisplayName());
    }

    public String processMessage(String message, Currency currency,Account account){
        return processMessage(processMessage(message),currency)
                .replaceAll("%account%",account.getName())
                .replaceAll("%amount%",account.query(currency)+"");
    }

    public String processMessage(String message, Currency currency,Account account,int change){
        return processMessage(processMessage(message),currency,account)
                .replaceAll("%change%",change+"");
    }

}
