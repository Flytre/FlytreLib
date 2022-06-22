package net.flytre.flytre_lib.api.storage.inventory.filter;

public interface ResourceFilter<T> {

    boolean passFilterTest(T resource);

    boolean isEmpty();
}
