package org.mintype.pollmod.permission;

public final class ModPermissions {

    /**
     * Allows access to the base /poll command.
     * Without this permission, the player cannot use any poll-related commands.
     */
    public static final String USE = "pollmod.poll.use";

    /**
     * Allows creation of new polls.
     */
    public static final String CREATE = "pollmod.poll.create";

    /**
     * Allows voting in active polls.
     * Intended for regular players.
     */
    public static final String VOTE = "pollmod.poll.vote";

    /**
     * Allows ending or closing an active poll.
     */
    public static final String END = "pollmod.poll.end";

    /**
     * Grants full administrative access to all poll functionality.
     * Recommended for administrators only.
     */
    public static final String ADMIN = "pollmod.poll.admin";

    private ModPermissions() {}
}
