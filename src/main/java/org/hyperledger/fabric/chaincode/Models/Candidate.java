package org.hyperledger.fabric.chaincode.Models;

public class Candidate {
    private String candidateId;
    private int votes;

    public Candidate() {}

    public Candidate(String candidateId) {
        this.candidateId = candidateId;
        votes = 0;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public int getVotes() {
        return votes;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void addVote() {
        votes++;
    }
}