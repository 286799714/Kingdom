package com.maydaymemory.kingdom.core.command;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandHandler {
    boolean playerOnly() default false;
    String name();
    String permission() default "";
    String description() default "";
}
