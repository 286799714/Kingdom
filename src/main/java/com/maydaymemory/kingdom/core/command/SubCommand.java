package com.maydaymemory.kingdom.core.command;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {
    public abstract void execute(CommandSender sender, String label, String[] args);

    /**If you want to customize tabComplete, just override this method and simply provide all possible values.*/
    public List<String> tabComplete(CommandSender sender, int index){
        return new ArrayList<>();
    }

    public ParameterTranslator getTranslator(String namespace){
        Class<? extends SubCommand> c = this.getClass();
        Field[] fields =  c.getDeclaredFields();
        List<ParameterProvider<?>> providers = new ArrayList<>();
        for(Field field : fields){
            if(!field.isAnnotationPresent(ParameterSign.class)) continue;
            ParameterProvider<?> provider = ParameterManager.getProvider(namespace, field.getType().getTypeName());
            if(provider == null) throw new NullPointerException("The provider of ("+field.getType().getTypeName()+") is not in the strategy.");
            providers.add(provider);
        }
        return new ParameterTranslator(providers.toArray(new ParameterProvider<?>[0]));
    }

    public ParameterMeta[] getParameterMeta(){
        Class<? extends SubCommand> c = this.getClass();
        Field[] fields =  c.getDeclaredFields();
        List<ParameterMeta> meta = new ArrayList<>();
        for(Field field : fields) {
            if (!field.isAnnotationPresent(ParameterSign.class)) continue;
            ParameterSign anno = field.getAnnotation(ParameterSign.class);
            ParameterMeta parameterMeta = new ParameterMeta(anno.name(), anno.hover());
            meta.add(parameterMeta);
        }
        return meta.toArray(new ParameterMeta[0]);
    }

    public void injectParameter(ParameterTranslator translator){
        Class<? extends SubCommand> c = this.getClass();
        Field[] fields =  c.getDeclaredFields();
        int index = 0;
        for(Field field : fields) {
            if (!field.isAnnotationPresent(ParameterSign.class)) continue;
            field.setAccessible(true);
            try {
                field.set(this, translator.getValue(index));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            index++;
        }
    }
}
