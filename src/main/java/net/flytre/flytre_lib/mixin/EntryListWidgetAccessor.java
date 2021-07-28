package net.flytre.flytre_lib.mixin;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntryListWidget.class)
public interface EntryListWidgetAccessor<E extends EntryListWidget.Entry<E>> {

    @Accessor("scrolling")
    boolean getScrolling();


    @Accessor("hoveredEntry")
    void setHoveredEntry(E hoveredEntry);
}
