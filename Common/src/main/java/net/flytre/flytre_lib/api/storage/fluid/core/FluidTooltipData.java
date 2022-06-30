package net.flytre.flytre_lib.api.storage.fluid.core;

import net.minecraft.text.Text;

import java.util.List;

/**
 * If a fluid implements this, then when the fluid's tooltips are rendered this function will be called so
 * the tooltip can be modified. Example would be an experience type fluid, which has custom units.
 */
public interface FluidTooltipData {

    void addTooltipInfo(FluidStack stack, List<Text> tooltip);
}
