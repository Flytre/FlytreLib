package net.flytre.flytre_lib.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;

/**
 * Some stuff to make rendering easier, which is great cuz rendering SUCKS.
 */
public class RenderUtils {

    /**
     * Get the color of a fluid, except doesn't actually work very well but hey it might just so why not try it!
     * NOT STABLE AT ALL ~ DO NOT USE
     *
     * @param world the world
     * @param pos   the pos
     * @param fluid the fluid
     * @return the int
     */

    public static int color(World world, BlockPos pos, Fluid fluid) {
        int c = FluidRenderHandlerRegistry.INSTANCE.get(fluid).getFluidColor(world, pos, fluid.getDefaultState());
        if (fluid.isIn(FluidTags.WATER))
            c += 0xFF000000;
        return c;
    }

    /**
     * Get the sprite for a fluid.
     *
     * @param world the world
     * @param pos   the pos
     * @param fluid the fluid
     * @return the sprite
     */
    @Environment(EnvType.CLIENT)
    public static Sprite textureName(World world, BlockPos pos, Fluid fluid) {
        FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        Sprite[] sprites = handler.getFluidSprites(world, pos, fluid.getDefaultState());
        return sprites[0];
    }

    /**
     * Unpack a color into a an int array [alpha,red,green,blue]
     *
     * @param color the color
     * @return the unpacked int
     */
    public static int[] unpackColor(int color) {

        final int[] colors = new int[4];
        colors[0] = color >> 24 & 0xff; // alpha
        colors[1] = color >> 16 & 0xff; // red
        colors[2] = color >> 8 & 0xff; // green
        colors[3] = color & 0xff; // blue
        return colors;
    }

    /**
     * Same as other renderBlockSprite but for a full block instead of a specified box.
     *
     * @param builder the builder
     * @param stack   the stack
     * @param sprite  the sprite
     * @param light   the light
     * @param overlay the overlay
     * @param color   the color
     */

    @Environment(EnvType.CLIENT)
    public static void renderBlockSprite(VertexConsumer builder, MatrixStack stack, Sprite sprite, int light, int overlay, int[] color) {

        renderBlockSprite(builder, stack.peek().getModel(), sprite, light, overlay, 0f, 1f, 0f, 1f, 0f, 1f, color);
    }

    /**
     * Basically used to render fluids like in the fluid tank. (Draws a box with the fluid in it)
     *
     * @param builder the vertex consumer
     * @param pos     the pos
     * @param sprite  the sprite
     * @param light   the light
     * @param overlay the overlay
     * @param x1      x1
     * @param x2      x2
     * @param y1      y1
     * @param y2      y2
     * @param z1      z1
     * @param z2      z2
     * @param color   unpacked color
     */

    @Environment(EnvType.CLIENT)
    public static void renderBlockSprite(VertexConsumer builder, Matrix4f pos, Sprite sprite, int light, int overlay, float x1, float x2, float y1, float y2, float z1, float z2, int[] color) {
        renderSpriteSide(builder, pos, sprite, Direction.DOWN, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.UP, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.NORTH, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.SOUTH, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.WEST, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.EAST, light, overlay, x1, x2, y1, y2, z1, z2, color);
    }

    @Environment(EnvType.CLIENT)
    public static void renderSpriteSide(VertexConsumer builder, Matrix4f pos, Sprite sprite, Direction side, int light, int overlay, float x1, float x2, float y1, float y2, float z1, float z2, int[] color) {
        // Convert block size to pixel size
        final double px1 = x1 * 16;
        final double px2 = x2 * 16;
        final double py1 = y1 * 16;
        final double py2 = y2 * 16;
        final double pz1 = z1 * 16;
        final double pz2 = z2 * 16;

        if (side == Direction.DOWN) {
            final float u1 = sprite.getFrameU(px1);
            final float u2 = sprite.getFrameU(px2);
            final float v1 = sprite.getFrameV(pz1);
            final float v2 = sprite.getFrameV(pz2);
            builder.vertex(pos, x1, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(0f, -1f, 0f).next();
            builder.vertex(pos, x1, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(0f, -1f, 0f).next();
            builder.vertex(pos, x2, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(0f, -1f, 0f).next();
            builder.vertex(pos, x2, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(0f, -1f, 0f).next();
        }

        if (side == Direction.UP) {
            final float u1 = sprite.getFrameU(px1);
            final float u2 = sprite.getFrameU(px2);
            final float v1 = sprite.getFrameV(pz1);
            final float v2 = sprite.getFrameV(pz2);
            builder.vertex(pos, x1, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(0f, 1f, 0f).next();
            builder.vertex(pos, x2, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(0f, 1f, 0f).next();
            builder.vertex(pos, x2, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(0f, 1f, 0f).next();
            builder.vertex(pos, x1, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(0f, 1f, 0f).next();
        }

        if (side == Direction.NORTH) {
            final float u1 = sprite.getFrameU(px1);
            final float u2 = sprite.getFrameU(px2);
            final float v1 = sprite.getFrameV(py1);
            final float v2 = sprite.getFrameV(py2);
            builder.vertex(pos, x1, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(0f, 0f, -1f).next();
            builder.vertex(pos, x1, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(0f, 0f, -1f).next();
            builder.vertex(pos, x2, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(0f, 0f, -1f).next();
            builder.vertex(pos, x2, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(0f, 0f, -1f).next();
        }

        if (side == Direction.SOUTH) {
            final float u1 = sprite.getFrameU(px1);
            final float u2 = sprite.getFrameU(px2);
            final float v1 = sprite.getFrameV(py1);
            final float v2 = sprite.getFrameV(py2);
            builder.vertex(pos, x2, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(0f, 0f, 1f).next();
            builder.vertex(pos, x2, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(0f, 0f, 1f).next();
            builder.vertex(pos, x1, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(0f, 0f, 1f).next();
            builder.vertex(pos, x1, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(0f, 0f, 1f).next();
        }

        if (side == Direction.WEST) {
            final float u1 = sprite.getFrameU(py1);
            final float u2 = sprite.getFrameU(py2);
            final float v1 = sprite.getFrameV(pz1);
            final float v2 = sprite.getFrameV(pz2);
            builder.vertex(pos, x1, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(-1f, 0f, 0f).next();
            builder.vertex(pos, x1, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(-1f, 0f, 0f).next();
            builder.vertex(pos, x1, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(-1f, 0f, 0f).next();
            builder.vertex(pos, x1, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(-1f, 0f, 0f).next();
        }

        if (side == Direction.EAST) {
            final float u1 = sprite.getFrameU(py1);
            final float u2 = sprite.getFrameU(py2);
            final float v1 = sprite.getFrameV(pz1);
            final float v2 = sprite.getFrameV(pz2);
            builder.vertex(pos, x2, y1, z1).color(color[1], color[2], color[3], color[0]).texture(u1, v1).overlay(overlay).light(light).normal(1f, 0f, 0f).next();
            builder.vertex(pos, x2, y2, z1).color(color[1], color[2], color[3], color[0]).texture(u2, v1).overlay(overlay).light(light).normal(1f, 0f, 0f).next();
            builder.vertex(pos, x2, y2, z2).color(color[1], color[2], color[3], color[0]).texture(u2, v2).overlay(overlay).light(light).normal(1f, 0f, 0f).next();
            builder.vertex(pos, x2, y1, z2).color(color[1], color[2], color[3], color[0]).texture(u1, v2).overlay(overlay).light(light).normal(1f, 0f, 0f).next();
        }
    }


    /**
     * Overlay a texture on a block
     *
     * @param id              the texture path
     * @param matrices        the matrix stack
     * @param vertexConsumers the vertex consumer
     * @param light           the light
     * @param overlay         the overlay
     */
    @Environment(EnvType.CLIENT)
    public static void render(Identifier id, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getBlockBreaking(id));
        List<ModelPart.Cuboid> cuboids = List.of(new ModelPart.Cuboid(0, 0, -0.01F, -0.01F, -0.01F, 16.02F, 16.02F, 16.02F, 0.0F, 0.0F, 0.0F, false, 16, 16));
        ModelPart model = new ModelPart(cuboids, new HashMap<>());
        model.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }


    /**
     * Overlay a texture on the specified side of a block
     *
     * @param id              the texture path
     * @param matrices        the matrix stack
     * @param vertexConsumers the vertex consumer
     * @param light           the light
     * @param overlay         the overlay
     * @param dir             the side
     */
    @Environment(EnvType.CLIENT)
    public static void renderSide(Identifier id, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction dir) {
        matrices.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getBlockBreaking(id));
        List<ModelPart.Cuboid> cuboids = switch (dir) {
            case WEST -> List.of(new ModelPart.Cuboid(0, 0, -0.01F, -0.01F, -0.01F, 0.00F, 16.02F, 16.02F, 0.0F, 0.0F, 0.0F, false, 16, 16));
            case EAST -> List.of(new ModelPart.Cuboid(0, 0, 16.01F, -0.01F, -0.01F, 0.00F, 16.02F, 16.02F, 0.0F, 0.0F, 0.0F, false, 16, 16));
            case NORTH -> List.of(new ModelPart.Cuboid(0, 0, -0.01F, -0.01F, -0.01F, 16.02F, 16.02F, 0.00F, 0.0F, 0.0F, 0.0F, false, 16, 16));
            case SOUTH -> List.of(new ModelPart.Cuboid(0, 0, -0.01F, -0.01F, 16.01F, 16.02F, 16.02F, 0.00F, 0.0F, 0.0F, 0.0F, false, 16, 16));
            case UP -> List.of(new ModelPart.Cuboid(0, 0, -0.01F, 16.01F, -0.01F, 16.02F, 0.00F, 16.02F, 0.0F, 0.0F, 0.0F, false, 16, 16));
            case DOWN -> List.of(new ModelPart.Cuboid(0, 0, -0.01F, -0.01F, -0.01F, 16.02F, 0.00F, 16.02F, 0.0F, 0.0F, 0.0F, false, 16, 16));
        };
        ModelPart model = new ModelPart(cuboids, new HashMap<>());
        model.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }


    /**
     * Draw a rectangle in a GUI
     */
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int temp = left;
            left = right;
            right = temp;
        }

        if (top < bottom) {
            int temp = top;
            top = bottom;
            bottom = temp;
        }

        final float red = (float) (color >> 16 & 255) / 255.0F;
        final float green = (float) (color >> 8 & 255) / 255.0F;
        final float blue = (float) (color & 255) / 255.0F;
        final float alpha = (float) (color >> 24 & 255) / 255.0F;

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(left, bottom, 0.0D).color(red,green,blue,alpha).next();
        buffer.vertex(right, bottom, 0.0D).color(red,green,blue,alpha).next();
        buffer.vertex(right, top, 0.0D).color(red,green,blue,alpha).next();
        buffer.vertex(left, top, 0.0D).color(red,green,blue,alpha).next();
        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
