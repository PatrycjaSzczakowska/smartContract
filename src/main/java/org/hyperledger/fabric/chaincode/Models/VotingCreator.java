package org.hyperledger.fabric.chaincode.Models;

import java.util.ArrayList;
import java.util.List;

public class VotingCreator {
    //List<> candidates, List <Voter> voters
    public static Voting createVoting() {
        String name = "Voting1";
        List<Candidate> candidates = createCandidates();
        List<Committee> committees = createCommittees();

        return new Voting(name, candidates, committees);
    }

    private static List<Candidate> createCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(new Candidate("P1", "x", "x", "x", 1, 2, 3));
        candidates.add(new Candidate("P2", "x", "x", "x", 1, 2, 3));
        return candidates;
    }

    private static List<Committee> createCommittees() {
        List<Committee> committees = new ArrayList<>();
        committees.add(new Committee("COM1", 20));
        committees.add(new Committee("COM2", 20));
        return committees;
    }
}
