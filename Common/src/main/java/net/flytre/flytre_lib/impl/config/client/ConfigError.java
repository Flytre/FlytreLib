package net.flytre.flytre_lib.impl.config.client;

public class ConfigError extends AssertionError {
    public ConfigError(String detailMessage) {
        super(detailMessage);
    }

    public ConfigError(Object detailMessage) {
        super(detailMessage);
    }
}
