package org.mintype.pollmod.poll;

import org.mintype.pollmod.storage.PollState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Poll {

    private final String question;                  // The poll question
    private final List<String> options;             // List of options for voting
    private final Map<String, Integer> votes;       // Option -> vote count
    private final Map<String, String> playerVotes;  // Player UUID -> option chosen
    private final Instant creationTime;             // When the poll was created
    private final Instant endTime;                  // When the poll ends
    private PollState state;

    /**
     * Create a poll.
     *
     * @param question        Poll question
     * @param options         List of options
     * @param durationSeconds Duration in seconds from creation
     */
    public Poll(String question, List<String> options, long durationSeconds) {
        this.question = question;
        this.options = new ArrayList<>(options);
        this.votes = new HashMap<>();
        this.playerVotes = new HashMap<>();

        // Initialize vote counts
        for (String option : options) {
            votes.put(option, 0);
        }

        this.creationTime = Instant.now();
        this.endTime = creationTime.plusSeconds(durationSeconds);

        this.state = PollState.ACTIVE;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public PollState getState() {
        return this.state;
    }

    /**
     * Check if the poll is still active (not expired).
     */
    public boolean isActive() {
        if(this.state == PollState.ACTIVE) this.state = Instant.now().isBefore(endTime) ? PollState.ACTIVE : PollState.ENDED;
        return this.state == PollState.ACTIVE;
    }

    public void addOption(String option) throws IllegalStateException {
        if(!isActive()) throw new IllegalStateException("Poll has expired");
        if (options.contains(option)) throw new IllegalArgumentException("Option already exists");
        options.add(option);
        votes.put(option, 0);
    }

    /**
     * Record a vote for a player. Each player can only vote once.
     *
     * @param playerId UUID or name of the player
     * @param option   Option chosen
     * @return true if vote was successful, throws exceptions if not successful
     */
    public boolean vote(String playerId, String option) {
        if (!options.contains(option)) throw new IllegalArgumentException("Option does not exist");
        if (playerVotes.containsKey(playerId)) throw new IllegalArgumentException("Player has already voted");
        if(!isActive()) throw new IllegalStateException("Poll has expired");

        playerVotes.put(playerId, option);
        votes.put(option, votes.get(option) + 1);
        return true;
    }

    public Map<String, Integer> getVoteCounts() {
        return votes;
    }

    public boolean hasPlayerVoted(String playerId) {
        return playerVotes.containsKey(playerId);
    }

    public int getTotalVotes() {
        return votes.values().stream().mapToInt(Integer::intValue).sum();
    }

    public List<String> getWinners() {
        int max = votes.values().stream().max(Integer::compare).orElse(0);
        return votes.entrySet().stream()
                .filter(e -> e.getValue() == max)
                .map(Map.Entry::getKey)
                .toList();
    }

    public void end() {
        this.state = PollState.ENDED;
    }
}
