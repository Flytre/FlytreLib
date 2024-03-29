package net.flytre.flytre_lib.api.config.annotation;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Used to populate a **map** with values dynamically when the config editor is loaded - For example, populating a list
 * with the names of the players on a server known to the client
 * <p>
 * The populating class must have a no-arg constructor
 * <p>
 * Only called when both the world and player are non-null - So the player is in a world
 * <p>
 * Also remember the returned map has wildcard type arguments - It is up to you, as the coder,
 * to make sure the map returned is the same type as the config map if you don't want crashes to occur
 * <p>
 * Lastly, your Map will need some sort of default value, because the populator triggers only when the GUI is opened, not constantly
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Populator {

    /**
     * @return a class with a no-arg constructor; implements the specified function
     * that takes in the world and player, and populates values to a map with the same
     * type as the map this is annotated to
     */
    Class<? extends BiFunction<ClientWorld, ClientPlayerEntity, Map<?, ?>>> value();

    /**
     * Whether to replace existing entries in the map or not
     */
    boolean replace() default false;
}
