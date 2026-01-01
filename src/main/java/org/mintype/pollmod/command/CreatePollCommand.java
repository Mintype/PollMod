package org.mintype.pollmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import org.mintype.pollmod.permission.ModPermissions;
import org.mintype.pollmod.poll.Poll;
import org.mintype.pollmod.poll.PollManager;
import me.lucko.fabric.api.permissions.v0.Permissions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static org.mintype.pollmod.Pollmod.POLL_MANAGER;

public class CreatePollCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher);
        });
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("poll")
                        .then(literal("create")
                                .requires(source -> Permissions.check(source, ModPermissions.CREATE, 0))
                                .then(argument("question", StringArgumentType.string())
                                        .then(argument("duration", IntegerArgumentType.integer(10, 3600))
                                                .then(argument("options", StringArgumentType.greedyString())
                                                        .executes(ctx -> {

                                                            String question = StringArgumentType.getString(ctx, "question");
                                                            int duration = IntegerArgumentType.getInteger(ctx, "duration");

                                                            String rawOptions = StringArgumentType.getString(ctx, "options");
                                                            List<String> options = Arrays.stream(rawOptions.split(","))
                                                                    .map(String::trim)
                                                                    .collect(Collectors.toList());

                                                            Poll poll = POLL_MANAGER.createPoll(ctx.getSource().getPlayer().getUuid(), question, options, duration);

                                                            // for debugging purposes
                                                            System.out.println(poll.getQuestion());
                                                            System.out.println(poll.getOptions());
                                                            System.out.println(poll.getState());

                                                            ctx.getSource().sendFeedback(
                                                                    () -> net.minecraft.text.Text.literal("Poll created: " + question),
                                                                    false
                                                            );

                                                            return 1;
                                                        })
                                                )
                                        )
                                )

                        )
        );
    }
}
