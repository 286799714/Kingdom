package com.maydaymemory.kingdom.core.command;

import com.maydaymemory.kingdom.core.language.LanguageInject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ParameterManager {
    @LanguageInject

    private static final Map<String, Map<String, ParameterProvider<?>>> providerMap = new HashMap<>();

    public static void registerProvider(String namespace, ParameterProvider<?> provider){
        if(!providerMap.containsKey(namespace)) providerMap.put(namespace, new HashMap<>());
        String typeName = provider.getToken().getType().getTypeName();
        switch (typeName) {
            case "int":
                providerMap.get(namespace).put(Integer.class.getTypeName(), provider);
                break;
            case "double":
                providerMap.get(namespace).put(Double.class.getTypeName(), provider);
                break;
            case "float":
                providerMap.get(namespace).put(Float.class.getTypeName(), provider);
                break;
            case "boolean":
                providerMap.get(namespace).put(Boolean.class.getTypeName(), provider);
                break;
            default:
                providerMap.get(namespace).put(provider.getToken().getType().getTypeName(), provider);
        }
    }

    public static ParameterProvider<?> getProvider(String namespace, String typeName){
        Map<String, ParameterProvider<?>> map = providerMap.get(namespace);
        if(map == null) return null;
        switch (typeName) {
            case "int":
                return providerMap.get(namespace).get(Integer.class.getTypeName());
            case "double":
                return providerMap.get(namespace).get(Double.class.getTypeName());
            case "float":
                return providerMap.get(namespace).get(Float.class.getTypeName());
            case "boolean":
                return providerMap.get(namespace).get(Boolean.class.getTypeName());
            default:
                return providerMap.get(namespace).get(typeName);
        }
    }

    public static Map<String, ParameterProvider<?>> getProviders(String namespace){
        return providerMap.get(namespace);
    }

    public static ParameterProvider<?> getProvider(String namespace, Type type){
        return getProvider(namespace, type.getTypeName());
    }
}