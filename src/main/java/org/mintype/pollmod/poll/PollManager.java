package org.mintype.pollmod.poll;

import java.util.ArrayList;
import java.util.List;

public class PollManager {
    private final List<Poll> activePolls;

    public PollManager() {
        this.activePolls = new ArrayList<>();
    }

    /**
     * Create a new poll and add it to the active list.
     *
     * @param question Poll question
     * @param options  Poll options
     * @param durationSeconds  Poll duration
     * @return the created Poll
     */
    public Poll createPoll(String question, List<String> options, long durationSeconds) {
        Poll poll = new Poll(question, options, durationSeconds);
        activePolls.add(poll);
        return poll;
    }

    /**
     * End a poll, removing it from the active list.
     *
     * @param poll Poll to end
     */
    public void endPoll(Poll poll) {
        poll.end();
        activePolls.remove(poll);
    }

    public void addPollOption(Poll poll, String option) {
        poll.addOption(option);
    }

    public List<Poll> getActivePolls() {
        return activePolls;
    }
}
