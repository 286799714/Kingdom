package com.maydaymemory.kingdom.core.command;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParameterSign {
    String name();
    String hover() default "";
}
