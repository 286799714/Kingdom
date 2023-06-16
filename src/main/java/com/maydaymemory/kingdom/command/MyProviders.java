package com.maydaymemory.kingdom.command;

import com.maydaymemory.kingdom.Reference;
import com.maydaymemory.kingdom.core.command.*;
import com.maydaymemory.kingdom.core.language.LanguageInject;
import com.maydaymemory.kingdom.model.economy.Account;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class MyProviders {
    @LanguageInject
    private static Configuration lang;

    public static void register() {
        ParameterManager.registerProvider(Reference.PLUGIN_NAME, stringProvider);
        ParameterManager.registerProvider(Reference.PLUGIN_NAME, floatProvider);
        ParameterManager.registerProvider(Reference.PLUGIN_NAME, doubleProvider);
        ParameterManager.registerProvider(Reference.PLUGIN_NAME, booleanProvider);
        ParameterManager.registerProvider(Reference.PLUGIN_NAME, intProvider);
        ParameterManager.registerProvider(Reference.PLUGIN_NAME, playerProvider);
        ParameterManager.registerProvider(Reference.PLUGIN_NAME, privateRegionProvider);

    }

    public static final ParameterProvider<String> stringProvider =  new ParameterProvider<String>() {
        @Override
        public String translate(CommandSender sender, String[] args, int index, RootCommand command, SubCommand subCommand, CommandMeta meta) {
            return args[index];
        }

        @Override
        public ParameterToken<String> getToken() {
            return new ParameterToken<String>() {};
        }
    };

    public static final ParameterProvider<Integer> intProvider = new ParameterProvider<Integer>() {
        @Override
        public Integer translate(CommandSender sender, String[] args, int index, RootCommand command, SubCommand subCommand, CommandMeta meta) {
            try{
                return Integer.parseInt(args[index]);
            }catch (NumberFormatException e){
                String msg = lang.getString("cmd.integer-format-wrong","cmd.integer-format-wrong");
                StringBuilder builder = new StringBuilder();
                builder.append("/");
                builder.append(command.getName());
                builder.append(" ");
                builder.append(meta.getSubCommandName());
                for(int i = 0; i < args.length; i++){
                    builder.append(" ");
                    if(i == index) builder.append("§c");
                    builder.append(args[i]);
                    if(i == index) builder.append("(x)§f");
                }
                sender.sendMessage(msg.replaceAll("%format%", builder.toString()));
            }
            return null;
        }

        @Override
        public ParameterToken<Integer> getToken() {
            return new ParameterToken<Integer>() {};
        }
    };

    public static final ParameterProvider<Float> floatProvider = new ParameterProvider<Float>() {
        @Override
        public Float translate(CommandSender sender, String[] args, int index, RootCommand command, SubCommand subCommand, CommandMeta meta) {
            try{
                return (float)Double.parseDouble(args[index]);
            }catch (NumberFormatException e){
                String msg = lang.getString("cmd.float-format-wrong","cmd.float-format-wrong");
                StringBuilder builder = new StringBuilder();
                builder.append("/");
                builder.append(command.getName());
                builder.append(" ");
                builder.append(meta.getSubCommandName());
                for(int i = 0; i < args.length; i++){
                    builder.append(" ");
                    if(i == index) builder.append("§c");
                    builder.append(args[i]);
                    if(i == index) builder.append("(x)§f");
                }
                sender.sendMessage(msg.replaceAll("%format%", builder.toString()));
            }
            return null;
        }

        @Override
        public ParameterToken<Float> getToken() {
            return new ParameterToken<Float>() {};
        }
    };

    public static final ParameterProvider<Double> doubleProvider = new ParameterProvider<Double>() {
        @Override
        public Double translate(CommandSender sender, String[] args, int index, RootCommand command, SubCommand subCommand, CommandMeta meta) {
            try{
                return Double.parseDouble(args[index]);
            }catch (NumberFormatException e){
                String msg = lang.getString("cmd.double-format-wrong", "cmd.double-format-wrong");
                StringBuilder builder = new StringBuilder();
                builder.append("/");
                builder.append(command.getName());
                builder.append(" ");
                builder.append(meta.getSubCommandName());
                for(int i = 0; i < args.length; i++){
                    builder.append(" ");
                    if(i == index) builder.append("§c");
                    builder.append(args[i]);
                    if(i == index) builder.append("(x)§f");
                }
                sender.sendMessage(msg.replaceAll("%format%", builder.toString()));
            }
            return null;
        }

        @Override
        public ParameterToken<Double> getToken() {
            return new ParameterToken<Double>() {};
        }
    };

    public static final ParameterProvider<Boolean> booleanProvider = new ParameterProvider<Boolean>() {
        @Override
        public Boolean translate(CommandSender sender, String[] args, int index, RootCommand command, SubCommand subCommand, CommandMeta meta) {
            try{
                return Boolean.parseBoolean(args[index]);
            }catch (NumberFormatException e){
                String msg = lang.getString("cmd.bool-format-wrong", "cmd.bool-format-wrong");
                StringBuilder builder = new StringBuilder();
                builder.append("/");
                builder.append(command.getName());
                builder.append(" ");
                builder.append(meta.getSubCommandName());
                for(int i = 0; i < args.length; i++){
                    builder.append(" ");
                    if(i == index) builder.append("§c");
                    builder.append(args[i]);
                    if(i == index) builder.append("(x)§f");
                }
                sender.sendMessage(msg.replaceAll("%format%", builder.toString()));
            }
            return null;
        }

        @Override
        public ParameterToken<Boolean> getToken() {
            return new ParameterToken<Boolean>() {};
        }
    };

    public static final ParameterProvider<Player> playerProvider = new ParameterProvider<Player>() {
        @Override
        public Player translate(CommandSender sender, String[] args, int index, RootCommand command, SubCommand subCommand, CommandMeta meta) {
            Player p = Bukkit.getPlayer(args[index]);
            if(p == null) {
                sender.sendMessage(lang.getString("cmd.player-not-found", "cmd.player-not-found").replaceAll("%player%", args[index]));
            }
            return p;
        }

        @Override
        public ParameterToken<Player> getToken() {
            return new ParameterToken<Player>() {};
        }
    };

    public static final ParameterProvider<PrivateRegion> privateRegionProvider = new ParameterProvider<PrivateRegion>() {
        @Override
        public PrivateRegion translate(CommandSender sender, String[] args, int index, RootCommand command, SubCommand subCommand, CommandMeta meta) {
            PrivateRegion region = PrivateRegion.fromName(args[index]);
            if(region == null) {
                sender.sendMessage(lang.getString("cmd.pr-not-found", "cmd.pr-not-found").replaceAll("%region%", args[index]));
            }
            return region;
        }

        @Override
        public ParameterToken<PrivateRegion> getToken() {
            return new ParameterToken<PrivateRegion>() {};
        }
    };
}
