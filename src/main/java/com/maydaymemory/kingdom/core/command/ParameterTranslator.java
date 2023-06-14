package com.maydaymemory.kingdom.core.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ParameterTranslator {
    protected String[] args;

    protected final ParameterProvider<?>[] providers;

    protected final List<Object> values = new ArrayList<>();

    public ParameterTranslator(ParameterProvider<?>... providers){
        this.providers = providers;
    }

    /**@return false if arguments length is lower than required, or ParameterProvider provides null*/
    public boolean translate(CommandSender sender, String[] args, RootCommand command, SubCommand subCommand, CommandMeta meta){
        if(args.length < providers.length) return false;
        this.args = args;
        int index = 0;
        boolean r = true;
        values.clear();
        for(ParameterProvider<?> provider : providers){
            Object o = provider.translate(sender, args, index, command, subCommand, meta);
            if(o == null) r = false;
            values.add(o);
            index++;
        }
        return r;
    }

    /**@return null if the index is out of value array's bound.*/
    public Object getValue(int index){
        if(index >= values.size()) return null;
        return values.get(index);
    }

    public String[] getArgs() {
        return args;
    }

    public int getParameterLength(){
        return providers.length;
    }

    public ParameterProvider<?> getProvider(int index){
        return providers[index];
    }
}
