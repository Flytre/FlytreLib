package net.flytre.flytre_lib.api.storage.inventory.filter;

public interface FilterSettings {
    int getFilterType();

    void setFilterType(int filterType);

    boolean isMatchNbt();

    void setMatchNbt(boolean matchNbt);

    boolean isMatchMod();

    void setMatchMod(boolean matchMod);

    default void toggleNbtMatch() {
        setMatchNbt(!isMatchNbt());
    }

    default void toggleModMatch() {
        setMatchMod(!isMatchMod());
    }

    default void toggleFilterType() {
        setFilterType(getFilterType() == 1 ? 0 : 1);
    }
}
