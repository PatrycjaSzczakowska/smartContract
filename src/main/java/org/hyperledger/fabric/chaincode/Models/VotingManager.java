package org.hyperledger.fabric.chaincode.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class VotingManager {
    private Voting voting;

    public boolean vote(String candidateId) {
        Optional<Candidate> candidate = getCandidate(candidateId);
        if (candidate.isPresent()) {
            voting.addVote(candidate.get());
            return true;
        }
        return false;
    }

    public boolean voterExist(String voterId) {
        return voting.getVoters().stream()
                .anyMatch(voter -> voter.getVoterId().equals(voterId));
    }

    public boolean candidateExist(String candidateId) {
        return voting.getCandidates().stream()
                .anyMatch(candidate -> candidate.getCandidateId().equals(candidateId));
    }

    public String getVotingName() {
        return voting.getName();
    }

    public Map<Candidate, Integer> getVotingResults() {
        return voting.getVotingResults();
    }

    private Optional<Candidate> getCandidate(String candidateId) {
        return voting.getCandidates().stream()
                .filter(candidate -> candidate.getCandidateId().equals(candidateId)).findFirst();
    }


}
