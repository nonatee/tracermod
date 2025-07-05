package nonate.tracers;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static nonate.tracers.TracerCommandBlock.blockCommand;
import static nonate.tracers.TracerCommandMob.mobCommand;



public class TracerCommand {
    public static void register() {

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("tracermod")
                    .then(mobCommand)
                    .then(blockCommand)
            );
        });
    }
}