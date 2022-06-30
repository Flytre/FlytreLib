package net.flytre.flytre_lib.impl.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class RenderUtilsImpl {

    private static SpriteGetter spriteGetter = ((world, pos, fluid) -> {
        throw new AssertionError("Fabric API or Forge is required to use this method");
    });
    private static ColorGetter colorGetter = ((world, pos, fluid) -> {
        throw new AssertionError("Fabric API or Forge is required to use this method");
    });
    private static boolean renderingSupported = false;

    private RenderUtilsImpl() {
    }

    public static boolean isFluidRenderingSupported() {
        return renderingSupported;
    }

    static void setSpriteGetter(SpriteGetter spriteGetter) {
        RenderUtilsImpl.spriteGetter = spriteGetter;
        renderingSupported = true;
    }

    static void setColorGetter(ColorGetter colorGetter) {
        RenderUtilsImpl.colorGetter = colorGetter;
    }

    public static Sprite getSprite(World world, BlockPos pos, Fluid fluid) {
        return spriteGetter.get(world, pos, fluid);
    }

    public static int getColor(World world, BlockPos pos, Fluid fluid) {
        return colorGetter.get(world, pos, fluid);
    }

    public static void renderFluidInGui(MatrixStack matrixStack, Fluid fluid, int drawHeight, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        y += height;

        final Sprite sprite = getSprite(MinecraftClient.getInstance().world, BlockPos.ORIGIN, fluid);
        int color = getColor(MinecraftClient.getInstance().world, BlockPos.ORIGIN, fluid);

        final int iconHeight = sprite.getHeight();
        int offsetHeight = drawHeight;

        RenderSystem.setShaderColor((color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, 1.0F);

        int iteration = 0;
        while (offsetHeight != 0) {
            final int curHeight = Math.min(offsetHeight, iconHeight);

            DrawableHelper.drawSprite(matrixStack, x, y - offsetHeight, 0, width, curHeight, sprite);
            offsetHeight -= curHeight;
            iteration++;
            if (iteration > 50) {
                break;
            }
        }
    }

    @FunctionalInterface
    interface SpriteGetter {
        Sprite get(World world, BlockPos pos, Fluid fluid);
    }

    @FunctionalInterface
    interface ColorGetter {
        int get(World world, BlockPos pos, Fluid fluid);
    }
}
