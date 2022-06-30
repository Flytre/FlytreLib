package net.flytre.flytre_lib.impl.base;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class SetupRenderUtils {

    private SetupRenderUtils() {
    }

    public static Sprite getSprite(World world, BlockPos pos, Fluid fluid) {
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        Sprite[] sprites = handler.getFluidSprites(world, pos, fluid.getDefaultState());
        return sprites[0];
    }

    public static int getColor(World world, BlockPos pos, Fluid fluid) {
        int c = FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidColor(world, pos, fluid.getDefaultState());
        if (fluid.isIn(FluidTags.WATER))
            c += 0xFF000000;
        return c;
    }

    public static void setup() {
        RenderUtilsImpl.setSpriteGetter(SetupRenderUtils::getSprite);
        RenderUtilsImpl.setColorGetter(SetupRenderUtils::getColor);
    }

}
