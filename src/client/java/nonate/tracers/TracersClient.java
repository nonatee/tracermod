package nonate.tracers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import static nonate.tracers.RenderTracer.drawTracers;

public class TracersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

			WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
				drawTracers(context.matrixStack(), context.consumers(), context.camera().getPos(), context);
			});

		TracerCommand.register();
	}
}