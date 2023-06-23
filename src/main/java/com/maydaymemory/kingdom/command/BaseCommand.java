package com.maydaymemory.kingdom.command;

import com.maydaymemory.kingdom.Reference;
import com.maydaymemory.kingdom.core.command.RootCommand;
import com.maydaymemory.kingdom.core.command.SubCommand;
import com.maydaymemory.kingdom.core.language.LanguageInject;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;

import javax.annotation.Nonnull;

public abstract class BaseCommand extends RootCommand {
    @LanguageInject
    private static Configuration lang;

    public BaseCommand(String name) {
        super(name);
    }

    @Override
    protected void executionRejectCallback(@Nonnull RejectCause cause, @Nonnull CommandSender commandSender, @Nonnull String[] args, SubCommand subCommand) {
        switch (cause){
            case PLAYER_ONLY:
                commandSender.sendMessage(processMessage("cmd.player-only"));
                break;
            case NO_PERMISSION:
                commandSender.sendMessage(processMessage("cmd.no-permission"));
                break;
            case MISSING_ARGUMENT:
                commandSender.sendMessage(processMessage("cmd.missing-argument")
                        .replaceAll("%help%", getUsage(args[0])));
                break;
            case COMMAND_NOT_FOUND:
                commandSender.sendMessage(processMessage("cmd.command-not-found")
                        .replaceAll("%command%", args[0]));
                break;
        }
    }

    @Override
    protected String processMessage(String key) {
        return (lang == null ? key : lang.getString(key, key));
    }

    @Override
    protected String getProviderNamespace() {
        return Reference.PLUGIN_NAME;
    }
}
