package net.flytre.flytre_lib.mixin.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    @Accessor("categoryOrderMap")
    static Map<String, Integer> getCategoryMap() {
        throw new AssertionError();
    }
}
