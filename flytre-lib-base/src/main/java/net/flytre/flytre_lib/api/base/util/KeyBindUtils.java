package net.flytre.flytre_lib.api.base.util;

import net.flytre.flytre_lib.impl.base.KeyBindUtilsImpl;
import net.minecraft.client.option.KeyBinding;

public class KeyBindUtils {


    public static KeyBinding register(KeyBinding keyBinding) {
        return KeyBindUtilsImpl.register(keyBinding);
    }
}
