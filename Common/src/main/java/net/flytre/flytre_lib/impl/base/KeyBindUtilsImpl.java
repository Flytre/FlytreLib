package net.flytre.flytre_lib.impl.base;

import com.google.common.collect.Lists;
import net.flytre.flytre_lib.mixin.base.KeyBindingAccessor;
import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class KeyBindUtilsImpl {

    private static final List<KeyBinding> BINDS = new ArrayList<>();

    private static Map<String, Integer> getCategoryMap() {
        return KeyBindingAccessor.getCategoryMap();
    }

    public static void addCategoryIfNecessary(String key) {
        Map<String, Integer> map = getCategoryMap();
        if (!map.containsKey(key)) {
            int value = map.values().stream().max(Integer::compareTo).orElse(0) + 1;
            map.put(key, value);
        }
    }

    public static KeyBinding register(KeyBinding binding) {
        addCategoryIfNecessary(binding.getCategory());
        BINDS.add(binding);
        return binding;
    }

    public static KeyBinding[] process(KeyBinding[] current) {
        List<KeyBinding> modified = Lists.newArrayList(current);
        modified.removeAll(BINDS);
        modified.addAll(BINDS);
        return modified.toArray(new KeyBinding[0]);
    }


}
