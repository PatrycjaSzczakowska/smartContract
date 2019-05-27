package org.hyperledger.fabric.chaincode.Models;

public enum VotingObjectsEnum {
    STATUS("votingStatus"),
    CANDIDATES("candidatesList"),
    COMMITTEES("committeesList"),
    TOKENS("tokensList");

    private String id;

    VotingObjectsEnum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
