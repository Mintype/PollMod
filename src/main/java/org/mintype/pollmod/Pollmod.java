package org.mintype.pollmod;

import net.fabricmc.api.ModInitializer;
import org.mintype.pollmod.command.CreatePollCommand;
import org.mintype.pollmod.command.ListPollCommand;
import org.mintype.pollmod.command.PollCommand;
import org.mintype.pollmod.poll.PollManager;

public class Pollmod implements ModInitializer {

    private final String MODID = "pollmod";
    public static final PollManager POLL_MANAGER = new PollManager();

    @Override
    public void onInitialize() {
        System.out.println("[" + MODID + "] initializing...");

        PollCommand.register();
        CreatePollCommand.register();
        ListPollCommand.register();

        System.out.println("[" + MODID + "] initialized");
    }
}
