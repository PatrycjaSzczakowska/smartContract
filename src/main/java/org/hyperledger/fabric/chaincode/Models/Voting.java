package org.hyperledger.fabric.chaincode.Models;

import java.util.List;

public class Voting {
    private String name;
    private List<Candidate> candidates;
    private List<Committee> committees;

    public Voting() {

    }

    public Voting(String name, List<Candidate> candidates, List<Committee> committees) {
        this.name = name;
        this.candidates = candidates;
        this.committees = committees;
    }

    public String getName() {
        return name;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public List<Committee> getCommittees() {
        return committees;
    }

}