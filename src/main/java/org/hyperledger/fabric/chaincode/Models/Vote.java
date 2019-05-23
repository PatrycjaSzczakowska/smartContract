package org.hyperledger.fabric.chaincode.Models;

public class Vote {
    private String tokenId; //id
    private String candidateId; //vote for...

    public Vote() {
    }

    public Vote(String tokenId, String candidateId) {
        this.tokenId = tokenId;
        this.candidateId = candidateId;
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
}
