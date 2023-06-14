package com.maydaymemory.kingdom.model.region;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class RegionTypeToken<T extends AdministrativeRegion>{
    private final Type type;

    public RegionTypeToken() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if(genericSuperclass instanceof Class){
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        type = typeArguments[0];
    }

    public Type getType() {
        return type;
    }
}
