package net.flytre.flytre_lib.api.base.util;

import net.flytre.flytre_lib.impl.base.KeyBindUtilsImpl;
import net.minecraft.client.option.KeyBinding;

/**
 * Used to register key binds on the client
 */
public final class KeyBindUtils {

    private KeyBindUtils() {
        throw new AssertionError();
    }

    public static KeyBinding register(KeyBinding keyBinding) {
        return KeyBindUtilsImpl.register(keyBinding);
    }
}
