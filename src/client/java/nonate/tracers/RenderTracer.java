package nonate.tracers;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import static nonate.tracers.TracerCamera.getCameraLookVector;
import static nonate.tracers.TracerConfig.*;
import static nonate.tracers.TracerLine.CustomRenderLayers.TRACER_LINE;

public class RenderTracer {
    public static void drawTracers(MatrixStack matrices, VertexConsumerProvider consumers, Vec3d cameraPos, WorldRenderContext context) {
        if(!runTracerMobs) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null || client.player == null) return;


        VertexConsumer buffer = consumers.getBuffer(TRACER_LINE);
        matrices.push();
        Camera camera = context.camera();

        for (MobEntity mob : client.world.getEntitiesByClass(
                MobEntity.class,
                client.player.getBoundingBox().expand(500),
                e -> tracedMobs.containsKey(e.getType())
        )) {

            Vec3d mobPos = mob.getPos().add(0, mob.getHeight() / 2.0, 0); // mob eye center
            Vec3d cameraLook = getCameraLookVector(camera); // or use camera.getRotation()
            Vec3d from = cameraLook.multiply(1); // small offset forward
            Vec3d to = mobPos;
            Vector3f perp = (to.subtract(cameraPos)).crossProduct(from).normalize().multiply(0.002).toVector3f();
            Vector3f from1 = from.toVector3f().add(perp);
            Vector3f from2 = from.toVector3f().sub(perp);

            Vector3f to1 = to.toVector3f().add(perp.mul((float) to.subtract(cameraPos).length()));
            Vector3f to2 = to.toVector3f().sub(perp);
            float[] colors = tracedMobs.get(mob.getType());


            MatrixStack.Entry entry = matrices.peek();

            buffer.vertex(entry, from1)
                    .color(colors[0],colors[1],colors[2], 1f)
                    .normal(0, 1, 0);
            buffer.vertex(entry, from2)
                    .color(colors[0],colors[1],colors[2], 1f)
                    .normal(0, 1, 0);


            buffer.vertex(entry.getPositionMatrix(), (float)(to1.x - cameraPos.x), (float)(to1.y - cameraPos.y), (float)(to1.z - cameraPos.z))
                    .color(colors[0],colors[1],colors[2], 1f)
                    .normal(0, 1, 0);



            buffer.vertex(entry.getPositionMatrix(), (float)(to2.x - cameraPos.x), (float)(to2.y - cameraPos.y), (float)(to2.z - cameraPos.z))
                    .color(colors[0],colors[1],colors[2], 1f)
                    .normal(0, 1, 0);
        }
        matrices.pop();
    }
}
