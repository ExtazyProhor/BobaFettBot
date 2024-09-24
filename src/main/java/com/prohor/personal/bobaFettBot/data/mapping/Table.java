package com.prohor.personal.bobaFettBot.data.mapping;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Table {
    String name();
}
