package net.flytre.flytre_lib.impl.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.IFluidTypeRenderProperties;
import net.minecraftforge.client.RenderProperties;

public class SetupRenderUtils {

    public static Sprite getSprite(World world, BlockPos pos, Fluid fluid) {
        IFluidTypeRenderProperties properties = RenderProperties.get(fluid);
        Identifier texture = properties.getStillTexture();
        return MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(texture);
    }

    public static int getColor(World world, BlockPos pos, Fluid fluid) {
        IFluidTypeRenderProperties properties = RenderProperties.get(fluid);
        return properties.getColorTint();
    }

    public static void setup() {
        RenderUtilsImpl.setSpriteGetter(SetupRenderUtils::getSprite);
        RenderUtilsImpl.setColorGetter(SetupRenderUtils::getColor);
    }
}
