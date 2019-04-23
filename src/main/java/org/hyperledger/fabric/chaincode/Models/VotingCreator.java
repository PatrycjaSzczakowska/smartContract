package org.hyperledger.fabric.chaincode.Models;

import java.util.ArrayList;
import java.util.List;

public class VotingCreator {
    //List<> candidates, List <Voter> voters, Date startTime, int durarionInHours
    public static Voting createVoting() {
        String name = "Voting1";
        List<Candidate> candidates = createCandidates();
        List<Voter> voters = createVoters();

        return new Voting(name, candidates, voters);
    }

    private static List<Candidate> createCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(new Candidate("P1"));
        candidates.add(new Candidate("P2"));
        return candidates;
    }

    private static List<Voter> createVoters() {
        List<Voter> voters = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            voters.add(new Voter("V".concat(String.valueOf(i))));
        }
        return voters;
    }
}
