package net.flytre.flytre_lib.api.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotate fields with this to attach a commented description to the json file and add hoverable text to the GUI.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

    /**
     * @return the description of the given field
     */
    String value();
}
