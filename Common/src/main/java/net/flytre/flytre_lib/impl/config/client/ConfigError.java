package net.flytre.flytre_lib.impl.config.client;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
class ConfigError extends AssertionError {
    public ConfigError(String detailMessage) {
        super(detailMessage);
    }

    public ConfigError(Object detailMessage) {
        super(detailMessage);
    }
}
