package net.flytre.flytre_lib.api.event;


import net.flytre.flytre_lib.impl.event.EventImpl;
import net.minecraft.client.util.math.MatrixStack;

@FunctionalInterface
public interface HudRenderEvent {


    Event<HudRenderEvent> EVENT = EventImpl.create();

    void onRender(MatrixStack matrixStack, float tickDelta);

}
