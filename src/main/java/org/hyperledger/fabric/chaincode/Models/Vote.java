package org.hyperledger.fabric.chaincode.Models;

public class Vote {
    private String tokenId; //id
    private String candidateId; //vote for...
    private String committeId; // neccessary?

    public Vote() {
    }

    public Vote(String tokenId, String candidateId, String committeId) {
        this.tokenId = tokenId;
        this.candidateId = candidateId;
        this.committeId = committeId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getCommitteId() {
        return committeId;
    }

    public void setCommitteId(String committeId) {
        this.committeId = committeId;
    }
}
