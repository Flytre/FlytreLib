package net.flytre.flytre_lib.api.event;

import java.util.List;

public interface Event<T> {

    void register(T listener);

    List<T> getListeners();
}
