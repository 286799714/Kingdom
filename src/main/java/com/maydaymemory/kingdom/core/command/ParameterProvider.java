package com.maydaymemory.kingdom.core.command;

import org.bukkit.command.CommandSender;

public interface ParameterProvider <T> {
    /**@return null if translate is not completed. If so, the command will not be executed.*/
    T translate(CommandSender sender, String[] args, int index, RootCommand command, SubCommand subCommand, CommandMeta meta);
    /**Reference usage:  return new ParameterToken<String>(){}; */
    ParameterToken<T> getToken();
}
