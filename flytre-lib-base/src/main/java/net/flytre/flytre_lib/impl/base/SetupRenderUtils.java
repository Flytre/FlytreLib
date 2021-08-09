package net.flytre.flytre_lib.impl.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SetupRenderUtils {

    public static Sprite getSprite(World world, BlockPos pos, Fluid fluid) {
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        Sprite[] sprites = handler.getFluidSprites(world, pos, fluid.getDefaultState());
        return sprites[0];
    }

    public static int getColor(World world, BlockPos pos,Fluid fluid) {
        int c = FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidColor(world, pos, fluid.getDefaultState());
        if (fluid.isIn(FluidTags.WATER))
            c += 0xFF000000;
        return c;
    }

    public static void renderFluidInGui(MatrixStack matrixStack, Fluid fluid, int drawHeight, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        y += height;

        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);

        if (handler == null)
            return;

        final Sprite sprite = handler.getFluidSprites(MinecraftClient.getInstance().world, BlockPos.ORIGIN, fluid.getDefaultState())[0];
        int color = FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidColor(MinecraftClient.getInstance().world, BlockPos.ORIGIN, fluid.getDefaultState());

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


    public static void setup() {
        RenderUtilsImpl.setRenderer(SetupRenderUtils::renderFluidInGui);
        RenderUtilsImpl.setSpriteGetter(SetupRenderUtils::getSprite);
        RenderUtilsImpl.setColorGetter(SetupRenderUtils::getColor);

    }

}
