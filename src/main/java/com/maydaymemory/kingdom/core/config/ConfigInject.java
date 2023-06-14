package com.maydaymemory.kingdom.core.config;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigInject {
    String path() default "config.yml";
}
