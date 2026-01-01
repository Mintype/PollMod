package org.mintype.pollmod.poll;

import java.util.*;

public class PollManager {
    private final Map<Integer, Poll> polls;
    private int nextId = 1;

    public PollManager() {
        this.polls = new HashMap<>();
    }

    /**
     * Create a new poll and add it to the active list.
     *
     * @param creatorId UUID of the poll creator
     * @param question Poll question
     * @param options  Poll options
     * @param durationSeconds  Poll duration
     * @return the created Poll
     */
    public Poll createPoll(UUID creatorId, String question, List<String> options, long durationSeconds) {
        int id = nextId++;
        Poll poll = new Poll(id, creatorId, question, options, durationSeconds);
        polls.put(id, poll);
        return poll;
    }

    /**
     * End a poll, removing it from the active list.
     *
     * @param id id of a poll to end
     */
    public void endPoll(int id) {
        Poll poll = getPoll(id);
        if (poll != null) {
            poll.end();
        }
    }

    public int addPollOption(Poll poll, String option) {
        return poll.addOption(option);
    }

    public Poll getPoll(int id) {
        return polls.get(id);
    }

    public void pausePoll(int id) {
        polls.get(id).pause();
    }

    public Map<Integer, Poll> getAllPolls() {
        return polls;
    }

    public Map<Integer, Poll> getPollsByState(PollState pollState) {
        return polls.entrySet().stream()
                .filter(e -> e.getValue().getState() == pollState)
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
    }
}
