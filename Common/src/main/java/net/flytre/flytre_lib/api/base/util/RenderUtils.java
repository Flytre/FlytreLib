package net.flytre.flytre_lib.api.base.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.flytre.flytre_lib.api.base.math.Rectangle;
import net.flytre.flytre_lib.impl.base.RenderUtilsImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * Some stuff to make rendering easier, which is great cuz rendering SUCKS.
 */
public final class RenderUtils {


    private RenderUtils() {
    }

    /**
     * Get the color of a fluid
     *
     * @param world the world
     * @param pos   the pos
     * @param fluid the fluid
     * @return the int
     */

    public static int color(World world, BlockPos pos, Fluid fluid) {
        return RenderUtilsImpl.getColor(world, pos, fluid);
    }

    /**
     * Get the sprite for a fluid.
     *
     * @param world the world
     * @param pos   the pos
     * @param fluid the fluid
     * @return the sprite
     */

    public static Sprite textureName(World world, BlockPos pos, Fluid fluid) {
        return RenderUtilsImpl.getSprite(world, pos, fluid);
    }

    public static void renderFluidInGui(MatrixStack matrixStack, Fluid fluid, int drawHeight, int x, int y, int width, int height) {
        RenderUtilsImpl.renderFluidInGui(matrixStack, fluid, drawHeight, x, y, width, height);
    }


    /**
     * Border is included in dimensions
     */
    public static void drawBorderedRect(Rectangle rectangle, int hex, int borderHex, int borderThickness) {
        drawRectangle(rectangle.reducedBy(borderThickness), hex);
        drawHollowRect(rectangle, borderHex, borderThickness);
    }


    /**
     * Draws a border of thickness X AROUND rectangle X
     */
    public static void drawBorderAround(Rectangle rectangle, int hex, int thickness) {
        Rectangle modified = new Rectangle(rectangle.getLeft() - thickness, rectangle.getTop() - thickness, rectangle.getWidth() + thickness * 2, rectangle.getHeight() + thickness * 2);
        drawHollowRect(modified, hex, thickness);
    }

    /**
     * Draws a hollow rectangle, i.e. a border of a specified thickness
     */
    public static void drawHollowRect(Rectangle rectangle, int hex, int thickness) {
        Rectangle top = new Rectangle(rectangle.getLeft(), rectangle.getTop(), rectangle.getWidth(), thickness);
        Rectangle bottom = new Rectangle(rectangle.getLeft(), rectangle.getBottom() - thickness, rectangle.getWidth(), thickness);
        Rectangle left = new Rectangle(rectangle.getLeft(), rectangle.getTop() + thickness, thickness, rectangle.getHeight() - thickness * 2);
        Rectangle right = new Rectangle(rectangle.getRight() - thickness, rectangle.getTop() + thickness, thickness, rectangle.getHeight() - thickness * 2);

        drawRectangle(top, hex);
        drawRectangle(bottom, hex);
        drawRectangle(left, hex);
        drawRectangle(right, hex);
    }


    public static void drawRectangle(Rectangle rectangle, int hex) {
        final Color color = new Color(hex);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(rectangle.getLeft(), rectangle.getBottom(), 0.0D).color(color.red, color.green, color.blue, color.alpha).next();
        buffer.vertex(rectangle.getRight(), rectangle.getBottom(), 0.0D).color(color.red, color.green, color.blue, color.alpha).next();
        buffer.vertex(rectangle.getRight(), rectangle.getTop(), 0.0D).color(color.red, color.green, color.blue, color.alpha).next();
        buffer.vertex(rectangle.getLeft(), rectangle.getTop(), 0.0D).color(color.red, color.green, color.blue, color.alpha).next();
        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    /**
     * Draw a rectangle in a GUI
     */
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        drawRectangle(Rectangle.fromBounds(left, top, right, bottom), color);
    }

    /**
     * Unpack a color into an int array [alpha,red,green,blue]
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


    public static void renderBlockSprite(VertexConsumer builder, MatrixStack stack, Sprite sprite, int light, int overlay, int[] color) {

        renderBlockSprite(builder, stack.peek().getPositionMatrix(), sprite, light, overlay, 0f, 1f, 0f, 1f, 0f, 1f, color);
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


    public static void renderBlockSprite(VertexConsumer builder, Matrix4f pos, Sprite sprite, int light, int overlay, float x1, float x2, float y1, float y2, float z1, float z2, int[] color) {
        renderSpriteSide(builder, pos, sprite, Direction.DOWN, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.UP, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.NORTH, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.SOUTH, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.WEST, light, overlay, x1, x2, y1, y2, z1, z2, color);
        renderSpriteSide(builder, pos, sprite, Direction.EAST, light, overlay, x1, x2, y1, y2, z1, z2, color);
    }


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

    //See InventoryScreen::drawEntity
    //Allows you to render all entities, not just living ones -> beware errors
    public static void renderSpinningEntity(int x, int y, int size, float mouseX, float mouseY, Entity entity) {
        float f = (float) Math.atan(mouseX / 40.0F);
        float g = (float) Math.atan(mouseY / 40.0F);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1050.0D);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0D, 0.0D, 1000.0D);
        matrixStack2.scale((float) size, (float) size, (float) size);

        float scalar = (float) (1f / entity.getBoundingBox().getYLength());
        if (scalar < 1)
            scalar = (float) (2f / entity.getBoundingBox().getYLength());
        matrixStack2.scale(scalar, scalar, scalar);

        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack2.multiply(quaternion);
        float h = entity instanceof LivingEntity ? ((LivingEntity) entity).bodyYaw : 0;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity instanceof LivingEntity ? ((LivingEntity) entity).prevHeadYaw : 0;
        float l = entity instanceof LivingEntity ? ((LivingEntity) entity).headYaw : 0;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).bodyYaw = 180.0F + f * 20.0F;
            ((LivingEntity) entity).headYaw = entity.getYaw();
            ((LivingEntity) entity).prevHeadYaw = entity.getYaw();
        }
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        entityRenderDispatcher.configure(FakeWorld.getInstance(), new Camera(), null);


        Quaternion rotation = Vec3f.POSITIVE_Y.getDegreesQuaternion((System.currentTimeMillis() % (360 * 10)) / 10.0f);
        matrixStack2.multiply(rotation);

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();


        RenderSystem.runAsFancy(() -> {
//            float scalar = (float) (1f / entity.getBoundingBox().getYLength());
//            matrixStack2.scale(scalar, scalar, scalar);

            entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack2, immediate, 15728880);
        });
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);

        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).bodyYaw = h;
            ((LivingEntity) entity).prevHeadYaw = k;
            ((LivingEntity) entity).headYaw = l;
        }
        entity.setYaw(i);
        entity.setPitch(j);
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    //See ItemRenderer::renderInGUI
    public static void renderSpinningItem(ItemStack stack, int x, int y, BakedModel model) {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client != null;
        TextureManager textureManager = client.getTextureManager();
        ItemRenderer renderer = client.getItemRenderer();

        textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 100.0F + renderer.zOffset);
        matrixStack.translate(8.0D, 8.0D, 0.0D);
        matrixStack.scale(1.0F, -1.0F, 1.0F);
        matrixStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();

        Quaternion rotation = Vec3f.POSITIVE_Y.getDegreesQuaternion((System.currentTimeMillis() % (360 * 10)) / 10.0f);
        matrixStack2.multiply(rotation);

        matrixStack2.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-30));
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        assert client.world != null;
        renderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }

    /**
     * Draws a string that wraps around to new lines when it exceeds the width parameter.
     * Returns the change in y from drawing the string
     */
    public static int drawWrappedString(MatrixStack matrices, String string, int x, int y, int wrapWidth, int maxLines, int color) {
        while (string != null && string.endsWith("\n")) {
            string = string.substring(0, string.length() - 1);
        }
        MinecraftClient client = MinecraftClient.getInstance();
        List<StringVisitable> strings = client.textRenderer.getTextHandler().wrapLines(new LiteralText(string), wrapWidth, Style.EMPTY);
        int i;
        for (i = 0; i < strings.size(); i++) {
            if (i >= maxLines) {
                break;
            }
            StringVisitable renderable = strings.get(i);
            if (i == maxLines - 1 && strings.size() > maxLines) {
                renderable = StringVisitable.concat(strings.get(i), StringVisitable.plain("..."));
            }
            OrderedText line = Language.getInstance().reorder(renderable);
            int x1 = x;
            if (client.textRenderer.isRightToLeft()) {
                int width = client.textRenderer.getWidth(line);
                x1 += (float) (wrapWidth - width);
            }
            client.textRenderer.draw(matrices, line, x1, y + i * client.textRenderer.fontHeight, color);
        }
        return i * client.textRenderer.fontHeight;
    }


    /**
     * Gets the height of a wrapped string (based on # of lines) without actually drawing the String
     */
    public static int getWrappedHeight(String string, int wrapWidth, int maxLines) {
        while (string != null && string.endsWith("\n")) {
            string = string.substring(0, string.length() - 1);
        }
        MinecraftClient client = MinecraftClient.getInstance();
        List<StringVisitable> strings = client.textRenderer.getTextHandler().wrapLines(new LiteralText(string), wrapWidth, Style.EMPTY);
        return Math.min(strings.size(), maxLines) * client.textRenderer.fontHeight;
    }

    public static void drawColorWheel(int centerX, int centerY, double radius, float brightness, float alpha) {

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();

        ColoredCoordinate center = new ColoredCoordinate(centerX, centerY, new java.awt.Color(java.awt.Color.HSBtoRGB(0, 0.0f, brightness)));
        Queue<ColoredCoordinate> vertices = new LinkedList<>();
        for (int degrees = 360; degrees >= 0; degrees -= 10) {
            double radians = degrees * Math.PI / 180;
            double x = radius * Math.cos(radians);
            double y = radius * Math.sin(radians);
            vertices.add(new ColoredCoordinate(x + center.x, y + center.y, new java.awt.Color(java.awt.Color.HSBtoRGB((float) degrees / 360f, 1.0f, brightness))));
        }

        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        buffer.vertex(center.x, center.y, 0.0D).color(center.color.getRed(), center.color.getGreen(), center.color.getBlue(), (int) (alpha * 255)).next();
        while (!vertices.isEmpty()) {
            ColoredCoordinate coord = vertices.poll();
            buffer.vertex(coord.x, coord.y, 0.0D).color(coord.color.getRed(), coord.color.getGreen(), coord.color.getBlue(), (int) (alpha * 255)).next();
        }
        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    /**
     * USE MOUSE RAW COORDS, NOT SCALED COORDS (Mouse::getX)
     */
    public static int getPixelColor(int x, int y) {

        //See ScreenshotRenderer.java, code copied from there

        MinecraftClient client = MinecraftClient.getInstance();
        Framebuffer framebuffer = client.getFramebuffer();


        int width = framebuffer.textureWidth;
        int height = framebuffer.textureHeight;
        int channels = 4;
        long sizeBytes = (long) width * (long) height * (long) channels;
        long pointer = MemoryUtil.nmemAlloc(sizeBytes);

        RenderSystem.bindTexture(framebuffer.getColorAttachment());

        RenderSystem.assertOnRenderThread();
        GlStateManager._pixelStore(3333, 4);

        int pixelDataFormat = 6408;
        int level = 0;
        GlStateManager._getTexImage(3553, level, pixelDataFormat, 5121, pointer);

        MemoryStack memoryStack = MemoryStack.stackPush();

        try {
            int j = width * channels;
            long l = memoryStack.nmalloc(j);

            for (int k = 0; k < height / 2; ++k) {
                int m = k * width * channels;
                int n = (height - 1 - k) * width * channels;
                MemoryUtil.memCopy(pointer + (long) m, l, j);
                MemoryUtil.memCopy(pointer + (long) n, pointer + (long) m, j);
                MemoryUtil.memCopy(l, pointer + (long) n, j);
            }
        } catch (Throwable var10) {
            try {
                memoryStack.close();
            } catch (Throwable var9) {
                var10.addSuppressed(var9);
            }

            throw var10;
        }

        memoryStack.close();

        long l = ((long) x + (long) y * (long) width) * 4L;
        int color = MemoryUtil.memGetInt(pointer + l);
        int r = (color >> 16 & 255);
        int g = (color >> 8 & 255);
        int b = (color & 255);
        return b * (256 * 256) + g * (256) + r;
    }

    private record Color(float red, float green, float blue, float alpha) {
        public Color(int color) {
            this((float) (color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, (float) (color >> 24 & 255) / 255.0F);
        }
    }

    private record ColoredCoordinate(double x, double y, java.awt.Color color) {
    }


}
