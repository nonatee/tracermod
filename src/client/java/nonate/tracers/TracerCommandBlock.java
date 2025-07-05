package nonate.tracers;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static nonate.tracers.TracerConfig.*;
import static nonate.tracers.TracerConfig.tracedMobs;

public class TracerCommandBlock {
    public static LiteralArgumentBuilder<FabricClientCommandSource> blockCommand = LiteralArgumentBuilder.<FabricClientCommandSource>literal("block")
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("add")
                    .then(argument("type", StringArgumentType.string())
                            .executes(ctx -> {
                                String input = StringArgumentType.getString(ctx, "type");
                                Identifier id = Identifier.tryParse(input);

                                if (id == null) {
                                    ctx.getSource().sendError(Text.literal("Invalid identifier format: " + input));
                                    return 0;
                                }

                                if (!Registries.BLOCK.containsId(id)) {
                                    ctx.getSource().sendError(Text.literal("Unknown block type: " + id));
                                    return 0;
                                }
                                Block type = Registries.BLOCK.get(id);
                                if(!tracedBlocks.containsKey(type)) {
                                    tracedBlocks.put(type, new float[]{1, 0, 0});
                                    ctx.getSource().sendFeedback(Text.literal("Added tracer for: " + id));
                                }
                                else {
                                    ctx.getSource().sendFeedback(Text.literal("Already tracing " + id +"!"));
                                }

                                return 1;
                            }))
            )
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("remove")
                    .then(argument("type", StringArgumentType.string())
                            .executes(ctx -> {
                                String input = StringArgumentType.getString(ctx, "type");
                                Identifier id = Identifier.tryParse(input);

                                if (id == null) {
                                    ctx.getSource().sendError(Text.literal("Invalid identifier format: " + input));
                                    return 0;
                                }

                                if (!Registries.BLOCK.containsId(id)) {
                                    ctx.getSource().sendError(Text.literal("Unknown block type: " + id));
                                    return 0;
                                }
                                Block type = Registries.BLOCK.get(id);
                                if(tracedBlocks.containsKey(type)) {
                                    tracedBlocks.remove(type, new float[]{1, 0, 0});
                                    ctx.getSource().sendFeedback(Text.literal("Removed tracer for: " + id));
                                }
                                else {
                                    ctx.getSource().sendFeedback(Text.literal("Not tracing " + id +"!"));
                                }

                                return 1;
                            }))
            )
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("radius")
                    .then(argument("radius", StringArgumentType.string())
                            .executes(context -> {
                                String input = StringArgumentType.getString(context, "radius");
                                try {
                                    radius = Integer.parseInt(input);
                                    context.getSource().sendFeedback(Text.literal("Radius changed to " + radius + "."));
                                } catch (NumberFormatException e) {
                                    context.getSource().sendError(Text.literal("Not an integer."));
                                }
                                return 1;
                            })))
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("enable")
                    .executes(context -> {
                        if(runTracerBlocks) {
                            context.getSource().sendFeedback(Text.literal("Tracers are already active."));
                        }
                        else {
                            runTracerBlocks = true;
                            context.getSource().sendFeedback(Text.literal("Tracers activated."));
                        }
                        return 1;
                    }))
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("disable")
                    .executes(context -> {
                        if(runTracerBlocks) {
                            runTracerBlocks = false;
                            context.getSource().sendFeedback(Text.literal("Tracers disabled."));
                        }
                        else {

                            context.getSource().sendFeedback(Text.literal("Tracers are already disabled."));
                        }
                        return 1;
                    }))
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("list").executes(context -> {
                if (tracedBlocks.isEmpty()) {
                    context.getSource().sendFeedback(Text.literal("No tracers currently set."));
                } else {
                    for (Map.Entry<Block, float[]> entry : tracedBlocks.entrySet()) {
                        Block type = entry.getKey();
                        float[] rgb = entry.getValue();

                        // Get the entity type ID string for readability
                        Identifier id = Registries.BLOCK.getId(type);
                        String idString = id != null ? id.toString() : "unknown";

                        String message = String.format("%s -> RGB(%.2f, %.2f, %.2f)", idString, rgb[0], rgb[1], rgb[2]);
                        context.getSource().sendFeedback(Text.literal(message));
                    }
                }
                return 1;
            }))
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("color")
                    .then(argument("type", StringArgumentType.string())
                            .then(argument("color", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        return CommandSource.suggestMatching(
                                                colorMap.keySet(),
                                                builder
                                        );
                                    })
                                    .executes(context -> {
                                        String input = StringArgumentType.getString(context,"type");
                                        Identifier id = Identifier.tryParse(input);
                                        Block type = Registries.BLOCK.get(id);
                                        String color = StringArgumentType.getString(context, "color");

                                        if(Registries.BLOCK.containsId(id)) {
                                            if(tracedBlocks.containsKey(type)) {
                                                tracedBlocks.put(type, colorMap.get(color));
                                                context.getSource().sendFeedback(Text.literal("Successfully changed color of " + id + " to " + color + "."));
                                            }
                                            else {
                                                context.getSource().sendError(Text.literal("Mob is not being traced."));
                                            }
                                        }
                                        else {
                                            context.getSource().sendError(Text.literal("Invalid identifier."));
                                        }
                                        return 1;
                                    }))));

}
