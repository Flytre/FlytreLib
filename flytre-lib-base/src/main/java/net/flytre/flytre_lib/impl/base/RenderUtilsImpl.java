package net.flytre.flytre_lib.impl.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;

@Environment(EnvType.CLIENT)
public class RenderUtilsImpl {

    public static boolean isFluidRenderingSupported() {
        return renderingSupported;
    }

    private static FluidRenderer renderer = ((matrixStack, fluid, drawHeight, x, y, width, height) -> {
        throw new AssertionError("Fabric API is required to use this method");
    });
    private static boolean renderingSupported = false;

    public static void setRenderer(FluidRenderer renderer) {
        RenderUtilsImpl.renderer = renderer;
        renderingSupported = true;
    }

    public static void renderFluidInGui(MatrixStack matrixStack, Fluid fluid, int drawHeight, int x, int y, int width, int height) {
        renderer.render(matrixStack, fluid, drawHeight, x, y, width, height);
    }

    @FunctionalInterface
    public interface FluidRenderer {
        void render(MatrixStack matrixStack, Fluid fluid, int drawHeight, int x, int y, int width, int height);
    }
}
