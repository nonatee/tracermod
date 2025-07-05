package nonate.tracers;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static nonate.tracers.TracerConfig.*;
import static nonate.tracers.TracerConfig.tracedMobs;

public class TracerCommandMob {
    public static LiteralArgumentBuilder<FabricClientCommandSource> mobCommand = LiteralArgumentBuilder.<FabricClientCommandSource>literal("mob")
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("add")
                    .then(argument("type", StringArgumentType.string())
                            .executes(ctx -> {
                                String input = StringArgumentType.getString(ctx, "type");
                                Identifier id = Identifier.tryParse(input);

                                if (id == null) {
                                    ctx.getSource().sendError(Text.literal("Invalid identifier format: " + input));
                                    return 0;
                                }

                                if (!Registries.ENTITY_TYPE.containsId(id)) {
                                    ctx.getSource().sendError(Text.literal("Unknown entity type: " + id));
                                    return 0;
                                }
                                EntityType<?> type = Registries.ENTITY_TYPE.get(id);
                                if(!tracedMobs.containsKey(type)) {
                                    tracedMobs.put(type, new float[]{1, 0, 0});
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
                            .suggests((context, builder) -> {
                                List<String> suggestions = tracedMobs.keySet().stream()
                                        .map(entityType -> {
                                            Identifier id = Registries.ENTITY_TYPE.getId(entityType);
                                            return id.getPath();
                                        })
                                        .toList();

                                return CommandSource.suggestMatching(suggestions, builder);
                            })
                            .executes(ctx -> {
                                String input = StringArgumentType.getString(ctx, "type");
                                Identifier id = Identifier.tryParse(input);

                                if (id == null) {
                                    ctx.getSource().sendError(Text.literal("Invalid identifier format: " + input));
                                    return 0;
                                }

                                if (!Registries.ENTITY_TYPE.containsId(id)) {
                                    ctx.getSource().sendError(Text.literal("Unknown entity type: " + id));
                                    return 0;
                                }
                                EntityType<?> type = Registries.ENTITY_TYPE.get(id);
                                if(tracedMobs.containsKey(type)) {
                                    tracedMobs.remove(type);
                                    ctx.getSource().sendFeedback(Text.literal("Removed tracer for: " + id));
                                }
                                else {
                                    ctx.getSource().sendFeedback(Text.literal("Not tracing " + id +"!"));
                                }

                                return 1;
                            }))
            )
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("enable")
                    .executes(context -> {
                        if(runTracerMobs) {
                            context.getSource().sendFeedback(Text.literal("Tracers are already active."));
                        }
                        else {
                            runTracerMobs = true;
                            context.getSource().sendFeedback(Text.literal("Tracers activated."));
                        }
                        return 1;
                    }))
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("disable")
                    .executes(context -> {
                        if(runTracerMobs) {
                            runTracerMobs = false;
                            context.getSource().sendFeedback(Text.literal("Tracers disabled."));
                        }
                        else {

                            context.getSource().sendFeedback(Text.literal("Tracers are already disabled."));
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
                                        EntityType<?> type = Registries.ENTITY_TYPE.get(id);
                                        String color = StringArgumentType.getString(context, "color");

                                        if(Registries.ENTITY_TYPE.containsId(id)) {
                                            if(tracedMobs.containsKey(type)) {
                                                tracedMobs.put(type, colorMap.get(color));
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
                                    }))))
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("list").executes(context -> {
                if (tracedMobs.isEmpty()) {
                    context.getSource().sendFeedback(Text.literal("No tracers currently set."));
                } else {
                    for (Map.Entry<EntityType<?>, float[]> entry : tracedMobs.entrySet()) {
                        EntityType<?> type = entry.getKey();
                        float[] rgb = entry.getValue();

                        // Get the entity type ID string for readability
                        Identifier id = Registries.ENTITY_TYPE.getId(type);
                        String idString = id != null ? id.toString() : "unknown";

                        String message = String.format("%s -> RGB(%.2f, %.2f, %.2f)", idString, rgb[0], rgb[1], rgb[2]);
                        context.getSource().sendFeedback(Text.literal(message));
                    }
                }
                return 1;
            }));
}
