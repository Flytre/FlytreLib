package net.flytre.flytre_lib.api.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate an enum method with this to provide translation keys for the enum's values
 * Could alternatively use @DisplayName.
 *
 * Only 1 such method is allowed per class.
 * The method should take in no arguments and return a string.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MemberLocalizationFunction {
}
