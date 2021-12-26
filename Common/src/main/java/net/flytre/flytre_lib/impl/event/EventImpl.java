package net.flytre.flytre_lib.impl.event;


import net.flytre.flytre_lib.api.event.Event;

import java.util.ArrayList;
import java.util.List;

public final class EventImpl<T> implements Event<T> {
    private final List<T> listeners;

    private EventImpl() {
        this.listeners = new ArrayList<>();
    }

    public void register(T listener) {
        this.listeners.add(listener);
    }

    public List<T> getListeners() {
        return listeners;
    }

    public static <T> EventImpl<T> create() {
        return new EventImpl<>();
    }
}