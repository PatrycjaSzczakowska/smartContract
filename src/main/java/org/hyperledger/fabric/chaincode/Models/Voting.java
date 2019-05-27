package org.hyperledger.fabric.chaincode.Models;

import java.util.List;

public class Voting {
    private List<Candidate> candidates;
    private List<Committee> committees;

    public Voting(List<Candidate> candidates, List<Committee> committees) {
        this.candidates = candidates;
        this.committees = committees;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public List<Committee> getCommittees() {
        return committees;
    }

}