package org.hyperledger.fabric.chaincode.Models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Voting {
    private String name;
    private Map<Candidate, Integer> votingResults;
    private List<Candidate> candidates;
    private List<Voter> voters;

    public Voting() {

    }

    public Voting(String name, List<Candidate> candidates, List<Voter> voters) {
        this.name = name;
        this.candidates = candidates;
        this.voters = voters;
        setupVotingResultsMap();
    }

    private void setupVotingResultsMap() {
        votingResults = new HashMap<>();
        for (Candidate candidate : candidates) {
            votingResults.put(candidate, 0);
        }
    }

    public String getName() {
        return name;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public List<Voter> getVoters() {
        return voters;
    }

    public Map<Candidate, Integer> getVotingResults() {
        return votingResults;
    }

    public void addVote(Candidate candidate) {
        votingResults.put(candidate, votingResults.get(candidate) + 1);
    }
}