package org.hyperledger.fabric.chaincode.Models;

public class Voting {
    private Map<Candidate, Integer> votingResults;

    private Voting(List<Candidate> candidates) {
        votingResults = new Map<Candidate, Integer>();
        for (Candidate candidate: candidates) {
            votingResults.put(candidate, 0);
        }
    }

    public String getVotingResults() {
        return userId;
    }

}