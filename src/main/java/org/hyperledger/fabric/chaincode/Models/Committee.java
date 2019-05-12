package org.hyperledger.fabric.chaincode.Models;

public class Committee {
    public String committeeId;
    public int votesNo;

    public Committee() {}

    public Committee(String committeeId, int votesNo) {
        this.committeeId = committeeId;
        this.votesNo = votesNo;
    }
}