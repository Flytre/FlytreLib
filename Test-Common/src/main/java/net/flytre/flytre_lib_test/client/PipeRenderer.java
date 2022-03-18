package net.flytre.flytre_lib_test.client;

import net.flytre.flytre_lib.base.util.InventoryUtils;
import net.flytre.flytre_lib_test.PipeBlock;
import net.flytre.flytre_lib_test.PipeEntity;
import net.flytre.flytre_lib_test.TimedPipeResult;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;

import java.util.LinkedList;

public class PipeRenderer implements BlockEntityRenderer<PipeEntity> {

    public PipeRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(PipeEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {


        int ticksPerOperation = entity.getTicksPerOperation();

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);

        for (TimedPipeResult timed : entity.getQueuedItems()) {

            if (timed.getTime() < -1)
                continue;

            matrices.push();
            LinkedList<BlockPos> path = timed.getPipeResult().getPath();
            BlockPos current = path.size() > 0 ? path.get(0) : null;
            if (current == null)
                current = entity.getPos();
            BlockPos next = path.size() <= 1 ? timed.getPipeResult().getDestination() : path.get(1);

            float mult = ticksPerOperation - timed.getTime() + tickDelta;

            if (entity.getWorld() != null) {
                Block nextBlock = entity.getWorld().getBlockState(next).getBlock();
                boolean inv = InventoryUtils.getInventoryAt(entity.getWorld(), next) != null;
                if (!(nextBlock instanceof PipeBlock) && !inv) {
                    mult = 0;
                }
            }
            float dx = (next.getX() - current.getX()) / (float) ticksPerOperation * (mult);
            float dy = (next.getY() - current.getY()) / (float) ticksPerOperation * (mult);
            float dz = (next.getZ() - current.getZ()) / (float) ticksPerOperation * (mult);

            if (timed.getPipeResult().getAnim() != null) {
                if (mult < 0f) {
                    BlockPos pos = current.offset(timed.getPipeResult().getAnim());
                    dx = (pos.getX() - current.getX()) / (float) ticksPerOperation * (-mult);
                    dy = (pos.getY() - current.getY()) / (float) ticksPerOperation * (-mult);
                    dz = (pos.getZ() - current.getZ()) / (float) ticksPerOperation * (-mult);
                }
            }
            if (!timed.isStuck())
                matrices.translate(dx, dy, dz);

            matrices.translate(0, -0.125, 0);

            if (timed.getPipeResult().getAnim() != null && mult < 0f) {
                float scale = 0.5f + (mult + (float) (ticksPerOperation / 2)) / (float) (ticksPerOperation / 2) * 0.3f;
                matrices.scale(scale, scale, scale);
            } else
                matrices.scale(0.8f, 0.8f, 0.8f);
            if (entity.getWorld() != null)
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) * 4 * (20.0f / ticksPerOperation)));
            MinecraftClient.getInstance().getItemRenderer().renderItem(timed.getPipeResult().getStack(), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0);
            matrices.pop();
        }

        matrices.pop();
    }
}
