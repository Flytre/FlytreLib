package net.flytre.flytre_lib.api.config.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to add a custom button to a map or list with the given function and name
 *
 * The runnable class must have a no-arg constructor
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Button {

    Class<? extends Runnable> function();

    String translationKey();

}
