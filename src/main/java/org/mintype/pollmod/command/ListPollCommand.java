package org.mintype.pollmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.mintype.pollmod.permission.ModPermissions;
import org.mintype.pollmod.poll.Poll;
import org.mintype.pollmod.poll.PollState;

import java.util.Collection;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static org.mintype.pollmod.Pollmod.POLL_MANAGER;
import me.lucko.fabric.api.permissions.v0.Permissions;

public class ListPollCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                register(dispatcher)
        );
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("poll")
                        .then(literal("list")
                                .requires(src ->
                                        Permissions.check(src, ModPermissions.USE, 0)
                                )
                                // /poll list
                                .executes(ctx ->
                                        list(ctx.getSource(), "active")
                                )
                                // /poll list <mode>
                                .then(argument("mode", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            builder.suggest("active");
                                            builder.suggest("ended");
                                            builder.suggest("paused");
                                            builder.suggest("all");
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx ->
                                                list(ctx.getSource(), StringArgumentType.getString(ctx, "mode"))
                                        )
                                )
                        )
        );
    }

    private static int list(ServerCommandSource source, String mode) {
        Map<Integer, Poll> polls;

        switch (mode.toLowerCase()) {
            case "all" -> {
                if (!Permissions.check(source, ModPermissions.ADMIN, 0)) {
                    source.sendError(Text.literal("You do not have permission to view all polls."));
                    return 0;
                }
                polls = POLL_MANAGER.getAllPolls();
            }
            case "ended" -> polls = POLL_MANAGER.getPollsByState(PollState.ENDED);
            case "paused" -> polls = POLL_MANAGER.getPollsByState(PollState.PAUSED);
            case "active" -> polls = POLL_MANAGER.getPollsByState(PollState.ACTIVE);
            default -> {
                source.sendError(Text.literal("Invalid mode. Use: active, ended, paused, all"));
                return 0;
            }
        }

        if (polls.isEmpty()) {
            source.sendFeedback(
                    () -> Text.literal("No polls found (" + mode + ")."),
                    false
            );
            return 1;
        }

        source.sendFeedback(
                () -> Text.literal("Polls (" + mode + "):"),
                false
        );


        for (Map.Entry<Integer, Poll> pollEntry : polls.entrySet()) {
            Integer id = pollEntry.getKey();
            Poll poll = pollEntry.getValue();
            source.sendFeedback(
                    () -> Text.literal(
                            "#" + id + " | "
                                    + poll.getQuestion()
                                    + " [" + poll.getState() + "]"
                    ),
                    false
            );
        }


//        for (int i = 0; i < pol) {
//            source.sendFeedback(
//                    () -> Text.literal(
//                            "#" + poll.getId() + " | "
//                                    + poll.getQuestion()
//                                    + " [" + poll.getState() + "]"
//                    ),
//                    false
//            );
//        }

        return polls.size();
    }
}
