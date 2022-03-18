package net.flytre.flytre_lib.impl.config.client;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
class ObjectWrapper<K> {
    public final K value;

    public ObjectWrapper(K value) {
        this.value = value;
    }
}
