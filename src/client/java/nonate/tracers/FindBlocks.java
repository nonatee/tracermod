package nonate.tracers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static nonate.tracers.TracerConfig.*;

public class FindBlocks {
    private static int clientTicks = 0;

    public static void findBlocks(MinecraftClient client) {
        World world = client.world;
        List<BlockPos> foundBlocks = new ArrayList<>();
        BlockPos center = client.player.getBlockPos();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        blocksToRender.clear();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    pos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    BlockState state = world.getBlockState(pos);
                    if (tracedBlocks.containsKey(state.getBlock())) {
                        blocksToRender.put(pos.toImmutable(),state.getBlock());
                    }
                }
            }
        }

    }
    public static void registerTickHandler() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                clientTicks++;

                if(clientTicks % 100 == 0) {
                    findBlocks(client);
                }
            }
        });
    }
}
