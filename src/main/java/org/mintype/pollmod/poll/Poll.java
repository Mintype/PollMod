package org.mintype.pollmod.poll;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Poll {

    private final String question;
    private final List<String> options;
    private final Map<String, Integer> votes;
    private final Map<String, String> playerVotes;

    private final long durationSeconds; // -1 = infinite
    private final Instant creationTime;

    private Instant startTime;
    private Instant endTime;
    private Duration remainingDuration;

    private PollState state;

    /**
     * Create a poll.
     *
     * @param question          Poll question
     * @param options           List of options
     * @param durationSeconds   Duration in seconds from creation; -1 represents infinite duration
     */
    public Poll(String question, List<String> options, long durationSeconds) {
        if (options == null || options.size() < 2) {
            throw new IllegalArgumentException("Poll must have at least two options");
        }

        this.question = Objects.requireNonNull(question);
        this.options = new ArrayList<>(options);
        this.votes = new HashMap<>();
        this.playerVotes = new HashMap<>();
        this.durationSeconds = durationSeconds;

        for (String option : options) {
            votes.put(option, 0);
        }

        this.creationTime = Instant.now();
        this.state = PollState.UNSTARTED;
    }

    public void startPoll() {
        if (state == PollState.ENDED) {
            throw new IllegalStateException("Poll has already ended");
        }

        if (state == PollState.ACTIVE) {
            return;
        }

        this.startTime = Instant.now();

        if (durationSeconds != -1) {
            if (remainingDuration != null) {
                this.endTime = startTime.plus(remainingDuration);
                remainingDuration = null;
            } else {
                this.endTime = startTime.plusSeconds(durationSeconds);
            }
        }

        this.state = PollState.ACTIVE;
    }

    public void pausePoll() {
        if (state != PollState.ACTIVE) {
            return;
        }

        if (endTime != null) {
            remainingDuration = Duration.between(Instant.now(), endTime);
        }

        this.state = PollState.PAUSED;
    }

    public void endPoll() {
        this.endTime = Instant.now();
        this.state = PollState.ENDED;
    }

    public PollState getState() {
        if (state == PollState.ACTIVE && endTime != null && Instant.now().isAfter(endTime)) {
            state = PollState.ENDED;
        }
        return state;
    }

    public boolean isActive() {
        return getState() == PollState.ACTIVE;
    }

    public void vote(String playerId, String option) {
        if (!isActive()) {
            throw new IllegalStateException("Poll is not active");
        }
        if (!votes.containsKey(option)) {
            throw new IllegalArgumentException("Option does not exist");
        }
        if (playerVotes.containsKey(playerId)) {
            throw new IllegalArgumentException("Player has already voted");
        }

        playerVotes.put(playerId, option);
        votes.merge(option, 1, Integer::sum);
    }

    public boolean hasPlayerVoted(String playerId) {
        return playerVotes.containsKey(playerId);
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public Map<String, Integer> getVoteCounts() {
        return Collections.unmodifiableMap(votes);
    }

    public int getTotalVotes() {
        return votes.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public List<String> getWinners() {
        int maxVotes = votes.values().stream()
                .max(Integer::compareTo)
                .orElse(0);

        return votes.entrySet().stream()
                .filter(e -> e.getValue() == maxVotes)
                .map(Map.Entry::getKey)
                .toList();
    }

    public void addOption(String option) {
        if(state == PollState.ENDED) {
            throw new IllegalStateException("Cannot add option to already ended poll");
        }
        if(options.contains(option)) {
            throw new IllegalArgumentException("Option already present in poll");
        }
        options.add(option);
    }
}
