package net.flytre.flytre_lib.api.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotate fields with this to enforce a numeric range for a certain parameter (Target: numbers only)
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {

    /**
     * @return the minimum value this numeric parameter can hold
     */
    double min();

    /**
     * @return the maximum value this numeric parameter can hold
     */
    double max();
}
