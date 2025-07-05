package nonate.tracers;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

import static nonate.tracers.TracerCamera.getCameraLookVector;
import static nonate.tracers.TracerConfig.*;

public class TracerToBlocks {
    public static void drawBlockTracers(MatrixStack matrices, VertexConsumerProvider consumers, Vec3d cameraPos, WorldRenderContext context) {
        if(!runTracerBlocks) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null || client.player == null) return;


        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());
        matrices.push();
        Camera camera = context.camera();

        for(Map.Entry<BlockPos, Block> entryBlock : blocksToRender.entrySet()) {
            Vec3d floatPos = Vec3d.ofCenter(entryBlock.getKey());
            Vec3d cameraLook = getCameraLookVector(camera); // or use camera.getRotation()
            Vec3d from = cameraLook.multiply(0.05); // small offset forward
            Vec3d to = floatPos;
            float[] colors = tracedBlocks.get(entryBlock.getValue());


            MatrixStack.Entry entry = matrices.peek();

            buffer.vertex(entry, from.toVector3f())
                    .color(colors[0],colors[1],colors[2], 1f)
                    .normal(0, 1, 0);


            buffer.vertex(entry.getPositionMatrix(), (float)(to.x - cameraPos.x), (float)(to.y - cameraPos.y), (float)(to.z - cameraPos.z))
                    .color(colors[0],colors[1],colors[2], 1f)
                    .normal(0, 1, 0);
        }
        matrices.pop();
    }
}
