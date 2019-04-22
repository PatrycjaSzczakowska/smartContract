package org.hyperledger.fabric.chaincode.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class VotingManager {

    public static boolean vote(Voting voting, String candidateId) {
        Optional<Candidate> candidate = getCandidate(voting, candidateId);
        if (candidate.isPresent()) {
            voting.addVote(candidate.get());
            return true;
        }
        return false;
    }

    public static boolean voterExist(Voting voting, String voterId) {
        return voting.getVoters().stream()
                .anyMatch(voter -> voter.getVoterId().equals(voterId));
    }

    public static boolean candidateExist(Voting voting, String candidateId) {
        return voting.getCandidates().stream()
                .anyMatch(candidate -> candidate.getCandidateId().equals(candidateId));
    }

    public static String getVotingName(Voting voting) {
        return voting.getName();
    }

    public static Map<Candidate, Integer> getVotingResults(Voting voting) {
        return voting.getVotingResults();
    }

    private static Optional<Candidate> getCandidate(Voting voting, String candidateId) {
        return voting.getCandidates().stream()
                .filter(candidate -> candidate.getCandidateId().equals(candidateId)).findFirst();
    }


}
