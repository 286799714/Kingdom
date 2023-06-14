package com.maydaymemory.kingdom.core.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;

public class CommandRegistry {
    private static final CommandMap commandMap;

    static {
        try
        {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap)field.get(Bukkit.getServer());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
    public static void register(Command command) {
        commandMap.register(command.getName(), command);
    }
}
