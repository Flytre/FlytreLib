package net.flytre.flytre_lib.impl.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.api.base.util.RenderUtils;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class RenderUtilsImpl {


    public static boolean isFluidRenderingSupported() {
        return renderingSupported;
    }

    private static FluidRenderer renderer = ((matrixStack, fluid, drawHeight, x, y, width, height) -> {
        throw new AssertionError("Fabric API is required to use this method");
    });

    private static SpriteGetter spriteGetter = ((world, pos, fluid) -> {
        throw new AssertionError("Fabric API is required to use this method");
    });

    private static ColorGetter colorGetter = ((world, pos, fluid) -> {
        throw new AssertionError("Fabric API is required to use this method");
    });

    private static boolean renderingSupported = false;

    public static void setSpriteGetter(SpriteGetter spriteGetter) {
        RenderUtilsImpl.spriteGetter = spriteGetter;
    }

    public static void setColorGetter(ColorGetter colorGetter) {
        RenderUtilsImpl.colorGetter = colorGetter;
    }

    public static void setRenderer(FluidRenderer renderer) {
        RenderUtilsImpl.renderer = renderer;
        renderingSupported = true;
    }

    public static Sprite getSprite(World world, BlockPos pos, Fluid fluid) {
        return spriteGetter.get(world, pos, fluid);
    }

    public static int getColor(World world, BlockPos pos, Fluid fluid) {
        return colorGetter.get(world, pos, fluid);
    }

    public static void renderFluidInGui(MatrixStack matrixStack, Fluid fluid, int drawHeight, int x, int y, int width, int height) {
        renderer.render(matrixStack, fluid, drawHeight, x, y, width, height);
    }

    @FunctionalInterface
    public interface FluidRenderer {
        void render(MatrixStack matrixStack, Fluid fluid, int drawHeight, int x, int y, int width, int height);
    }

    @FunctionalInterface
    public interface SpriteGetter {
        Sprite get(World world, BlockPos pos, Fluid fluid);
    }

    @FunctionalInterface
    public interface ColorGetter {
        int get(World world, BlockPos pos, Fluid fluid);
    }
}
