package org.hyperledger.fabric.chaincode.Models;

public class Candidate {
    private String candidateId;
    private String name;

    public Candidate(String walletId, String name) {
        this.candidateId = candidateId;
        this.name = name;
    }

    private Candidate() {}

    public String getCandidateId() {
        return candidateId;
    }

    public Double getName() {
        return name;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public void setName(String name) {
        this.name = name;
    }
}