package org.hyperledger.fabric.chaincode.Models;

import java.util.ArrayList;
import java.util.List;

public class VotingCreator {
    //List<> candidates, List <Voter> voters
    public static Voting createVoting() {
        List<Candidate> candidates = createCandidates();
        List<Committee> committees = createCommittees();

        return new Voting(candidates, committees);
    }

    private static List<Candidate> createCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(new Candidate("C1", "Tomasz", "Jasiak", "zieloni", 1, 1, 35));
        candidates.add(new Candidate("C2", "Donald", "Kowalski", "czerwoni", 2, 1, 43));
        candidates.add(new Candidate("C3", "Maria", "Nowacka", "zieloni", 1, 2, 30));

        return candidates;
    }

    private static List<Committee> createCommittees() {
        List<Committee> committees = new ArrayList<>();
        committees.add(new Committee("COM1", 10));
        committees.add(new Committee("COM2", 5));
        return committees;
    }
}
