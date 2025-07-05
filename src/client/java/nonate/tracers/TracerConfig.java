package nonate.tracers;

import net.minecraft.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class TracerConfig {
    // Mutable map to allow adding/removing entries via commands
    public static final Map<EntityType<?>, float[]> tracedMobs = new HashMap<>();
    public static boolean runTracer = true;
    public static final Map<String,float[]> colorMap = new HashMap<>();

    static {
        tracedMobs.put(EntityType.SHULKER, new float[]{0f, 1f, 0f});
        tracedMobs.put(EntityType.AXOLOTL, new float[]{0f, 1f, 1f});
    }
    static {
        colorMap.put("red",     new float[]{1f, 0f, 0f});
        colorMap.put("green",   new float[]{0f, 1f, 0f});
        colorMap.put("blue",    new float[]{0f, 0f, 1f});
        colorMap.put("yellow",  new float[]{1f, 1f, 0f});
        colorMap.put("cyan",    new float[]{0f, 1f, 1f});
        colorMap.put("magenta", new float[]{1f, 0f, 1f});
        colorMap.put("white",   new float[]{1f, 1f, 1f});
        colorMap.put("black",   new float[]{0f, 0f, 0f});
        colorMap.put("orange",  new float[]{1f, 0.5f, 0f});
        colorMap.put("purple",  new float[]{0.5f, 0f, 0.5f});
        colorMap.put("pink",    new float[]{1f, 0.75f, 0.8f});
        colorMap.put("gray",    new float[]{0.5f, 0.5f, 0.5f});
        colorMap.put("lime",    new float[]{0.5f, 1f, 0f});
        colorMap.put("brown",   new float[]{0.4f, 0.26f, 0.13f});
    }
}