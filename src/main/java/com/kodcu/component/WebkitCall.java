package com.kodcu.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by usta on 31.05.2015.
 * This annotation only helps developers which methods called from JS in JavaFx Embedded Webkit
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebkitCall {
}
