package org.hyperledger.fabric.chaincode.Models;

import java.util.List;

public class CandidatesList {
    private List<String> candidateIds;

    public CandidatesList() {}

    public CandidatesList(List<String> candidateIds) {
        this.candidateIds = candidateIds;
    }

    public List<String> getCandidateIds() {
        return candidateIds;
    }

    public void setCandidateIds(List<String> candidateIds) {
        this.candidateIds = candidateIds;
    }

}