package org.mintype.pollmod.poll;

public enum PollState {
    UNSTARTED,     // Poll has not started yet
    ACTIVE,     // Poll is ongoing and players can vote
    PAUSED,     // Poll is ongoing and players can vote
    ENDED,      // Poll has finished
}
