package nonate.tracers;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import java.util.OptionalDouble;

public class TracerLine {


    public class CustomRenderLayers {
        public static final RenderLayer TRACER_LINE = RenderLayer.of(
                "tracer_line",
                256,
                false,
                true,
                RenderPipelines.DEBUG_QUADS,
                RenderLayer.MultiPhaseParameters.builder()

                        .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(2)))
                        .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                        .target(RenderPhase.MAIN_TARGET)
                        .lightmap(RenderPhase.DISABLE_LIGHTMAP)
                        .texture(RenderPhase.NO_TEXTURE)
                        .overlay(RenderPhase.DISABLE_OVERLAY_COLOR)
                        .texturing(RenderPhase.DEFAULT_TEXTURING)
                        .build(true)
        );
    }
}
