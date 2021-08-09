package net.flytre.flytre_lib.impl.storage.fluid.gui;


import net.flytre.flytre_lib.api.storage.fluid.core.FluidStack;
import net.flytre.flytre_lib.api.storage.fluid.gui.FluidHandler;

public interface FluidHandlerListener {

    void onSlotUpdate(FluidHandler handler, int slotId, FluidStack stack);
}
