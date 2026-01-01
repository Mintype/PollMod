package org.mintype.pollmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import org.mintype.pollmod.permission.ModPermissions;
import me.lucko.fabric.api.permissions.v0.Permissions;
import org.mintype.pollmod.poll.Poll;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static org.mintype.pollmod.Pollmod.POLL_MANAGER;

public class EditPollCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher);
        });
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("poll")
                        .then(literal("edit")
                                .requires(source -> Permissions.check(source, ModPermissions.CREATE, 0))
                                .then(argument("id", IntegerArgumentType.integer(10, 3600))
                                        // /poll edit <mode>
                                        .then(argument("mode", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    builder.suggest("addOption");
                                                    builder.suggest("delOption");
                                                    builder.suggest("setQuestion");
                                                    return builder.buildFuture();
                                                })
                                                .then(argument("arg", StringArgumentType.greedyString())
                                                    .executes(ctx ->
                                                            edit(ctx, ctx.getSource(), StringArgumentType.getString(ctx, "mode"))
                                                    )
                                                )
                                        )

                                )
                        )
        );
    }

    private static int edit(CommandContext<ServerCommandSource> ctx, ServerCommandSource source, String mode) {
        int id = IntegerArgumentType.getInteger(ctx, "id");
        String arg = StringArgumentType.getString(ctx, "arg");

        Poll poll = POLL_MANAGER.getPoll(id);

        /*
        TODO:
            1. Make sure all methods work
            2. Add error handling & message returned back to source
         */

        switch (mode.toLowerCase()) {
            case "addoption" -> {
                poll.addOption(arg);
            }
            case "deloption" -> {
                poll.deleteOption(arg);
            }
            case "setquestion" -> {
                poll.setQuestion(arg);
            }
        }

        return 1;
    }
}
