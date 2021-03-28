package net.flytre.flytre_lib.common.inventory;

import net.minecraft.util.math.Direction;

import java.util.Map;

public interface IOTypeProvider {
    Map<Direction, IOType> getIOType();
}
