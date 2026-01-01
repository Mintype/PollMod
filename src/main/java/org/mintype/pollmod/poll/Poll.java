package org.mintype.pollmod.poll;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Poll {

    private final int id;
    private final UUID creatorId;
    private final String question;
    private final Map<Integer, String> options; // optionId -> optionText
    private final Map<Integer, Integer> votes; // optionId -> voteCount
    private final Map<String, Integer> playerVotes; // playerId -> optionId
    private int nextOptionId;

    private final long durationSeconds; // -1 = infinite
    private final Instant creationTime;

    private Instant startTime;
    private Instant endTime;
    private Duration remainingDuration;

    private PollState state;

    /**
     * Create a poll.
     *
     * @param id                Poll id
     * @param creatorId         UUID of the poll creator
     * @param question          Poll question
     * @param options           List of options
     * @param durationSeconds   Duration in seconds from creation; -1 represents infinite duration
     */
    public Poll(int id, UUID creatorId, String question, List<String> options, long durationSeconds) {
        this.id = id;
        this.creatorId = Objects.requireNonNull(creatorId);
        if (options == null || options.size() < 2) {
            throw new IllegalArgumentException("Poll must have at least two options");
        }

        this.question = Objects.requireNonNull(question);
        this.options = new HashMap<>();
        this.votes = new HashMap<>();
        this.playerVotes = new HashMap<>();
        this.durationSeconds = durationSeconds;
        this.nextOptionId = 0;

        for (String option : options) {
            int optionId = nextOptionId++;
            this.options.put(optionId, option);
            votes.put(optionId, 0);
        }

        this.creationTime = Instant.now();
        this.state = PollState.UNSTARTED;
    }

    public void start() {
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

    public void pause() {
        if (state != PollState.ACTIVE) {
            return;
        }

        if (endTime != null) {
            remainingDuration = Duration.between(Instant.now(), endTime);
        }

        this.state = PollState.PAUSED;
    }

    public void end() {
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

    public void vote(String playerId, int optionId) {
        if (!isActive()) {
            throw new IllegalStateException("Poll is not active");
        }
        if (!options.containsKey(optionId)) {
            throw new IllegalArgumentException("Option does not exist");
        }
        if (playerVotes.containsKey(playerId)) {
            throw new IllegalArgumentException("Player has already voted");
        }

        playerVotes.put(playerId, optionId);
        votes.merge(optionId, 1, Integer::sum);
    }

    public boolean hasPlayerVoted(String playerId) {
        return playerVotes.containsKey(playerId);
    }

    public String getQuestion() {
        return question;
    }

    public Map<Integer, String> getOptions() {
        return Collections.unmodifiableMap(options);
    }

    public Map<Integer, Integer> getVoteCounts() {
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

    public List<Integer> getWinningOptionIds() {
        int maxVotes = votes.values().stream()
                .max(Integer::compareTo)
                .orElse(0);

        return votes.entrySet().stream()
                .filter(e -> e.getValue() == maxVotes)
                .map(Map.Entry::getKey)
                .toList();
    }

    public UUID getCreatorId() {
        return creatorId;
    }

    public String getOptionText(int optionId) {
        return options.get(optionId);
    }

    public int addOption(String option) {
        if(state == PollState.ENDED) {
            throw new IllegalStateException("Cannot add option to already ended poll");
        }
        if(options.containsValue(option)) {
            throw new IllegalArgumentException("Option already present in poll");
        }
        int optionId = nextOptionId++;
        options.put(optionId, option);
        votes.put(optionId, 0);
        return optionId;
    }

    public int getId() {
        return id;
    }
}
