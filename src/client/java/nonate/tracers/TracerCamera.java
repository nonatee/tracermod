package nonate.tracers;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TracerCamera {
    public static Vec3d getCameraLookVector(Camera camera) {
        Quaternionf rotation = camera.getRotation();
        // Forward direction in OpenGL (negative Z)
        Vector3f forward = new Vector3f(0, 0, -1);
        forward.rotate(rotation);  // Rotate the forward vector by the quaternion
        return new Vec3d(forward.x(), forward.y(), forward.z());
    }
}
