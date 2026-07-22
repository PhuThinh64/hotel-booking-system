package com.example.hotel_booking.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAction {
    String module();
    String action();
    String targetId();
    Class<?> entityClass() default Void.class;
    boolean resolveParent() default false;
}