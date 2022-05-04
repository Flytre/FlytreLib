package net.flytre.flytre_lib.loader;

import java.util.function.Supplier;

class CachedSupplier<T> implements Supplier<T> {

    private final Supplier<T> generator;
    private T value;


    private CachedSupplier(Supplier<T> generator) {
        this.generator = generator;
    }

    static <T> CachedSupplier<T> of(Supplier<T> supplier) {
        return new CachedSupplier<>(supplier);
    }

    @Override
    public T get() {
        if (value == null)
            value = generator.get();
        return value;
    }
}
