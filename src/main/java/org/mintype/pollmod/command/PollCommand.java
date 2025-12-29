package org.mintype.pollmod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import org.mintype.pollmod.permission.ModPermissions;
import me.lucko.fabric.api.permissions.v0.Permissions;


public class PollCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher);
        });
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("poll")
                        .requires(source -> Permissions.check(source, ModPermissions.USE, 0))
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(
                                    () -> net.minecraft.text.Text.literal("Poll command works."),
                                    false
                            );
                            return 1;
                        })
        );
    }
}
