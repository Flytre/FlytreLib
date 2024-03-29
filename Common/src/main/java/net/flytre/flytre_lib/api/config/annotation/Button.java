package net.flytre.flytre_lib.api.config.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to add a custom button to a map or list with the given function and name
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Button {

    /**
     * @return A class with a no-arg constructor and runnable function that executes the
     * button's on press code
     */
    Class<? extends Runnable> function();

    /**
     * @return the translation key for the button's text
     */
    String translationKey();

}
