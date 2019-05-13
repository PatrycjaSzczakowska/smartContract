package org.hyperledger.fabric.chaincode.Models;

import java.util.ArrayList;
import java.util.List;

public class Committee {
    private String committeeId;
    private int votesNo;
    private List<String> votes; //tokenIds, should be here??

    public Committee() {
    }

    public Committee(String committeeId, int votesNo) {
        this.committeeId = committeeId;
        this.votesNo = votesNo;
        this.votes=new ArrayList<>();
    }

    public Committee(String committeeId, int votesNo, List<String> votes) {
        this.committeeId = committeeId;
        this.votesNo = votesNo;
        this.votes = votes;
    }

    public String getCommitteeId() {
        return committeeId;
    }

    public void setCommitteeId(String committeeId) {
        this.committeeId = committeeId;
    }

    public int getVotesNo() {
        return votesNo;
    }

    public void setVotesNo(int votesNo) {
        this.votesNo = votesNo;
    }

    public List<String> getVotes() {
        return votes;
    }

    public void setVotes(List<String> votes) {
        this.votes = votes;
    }

    public void addVote(String tokenId){
        votes.add(tokenId);
    }
}