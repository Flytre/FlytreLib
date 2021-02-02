package net.flytre.flytre_lib.common.inventory;

import net.minecraft.util.math.Direction;

import java.util.HashMap;

public interface IOMode {
    HashMap<Direction,Boolean> getIO();
}
