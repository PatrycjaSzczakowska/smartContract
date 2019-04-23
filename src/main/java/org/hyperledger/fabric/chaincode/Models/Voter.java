package org.hyperledger.fabric.chaincode.Models;

public class Voter {
    private String voterId;
    private String candidateIdVote;

    public Voter() {}

    public Voter(String voterId) {
        this.voterId = voterId;
        candidateIdVote = null;
    }

    public String getVoterId() {
        return voterId;
    }

    public String getCandidateIdVote() {
        return candidateIdVote;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public void setCandidateIdVote(String candidateIdVote) {
        this.candidateIdVote = candidateIdVote;
    }

    public boolean vote(String candidateIdVote) {
        if (this.candidateIdVote != null) return false;

        this.candidateIdVote = candidateIdVote;
        return true;
    }
}