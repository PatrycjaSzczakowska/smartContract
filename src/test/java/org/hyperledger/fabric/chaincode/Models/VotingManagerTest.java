package org.hyperledger.fabric.chaincode.Models;

import org.junit.Assert;

import static org.junit.jupiter.api.Assertions.*;

class VotingManagerTest {

    @org.junit.jupiter.api.Test
    void vote() {
        VotingManager votingManager=new VotingManager();
        Assert.assertTrue(votingManager.vote(VotingCreator.createVoting(),"P2"));
    }

    @org.junit.jupiter.api.Test
    void voterExist() {
        VotingManager votingManager=new VotingManager();
        Assert.assertTrue(votingManager.voterExist(VotingCreator.createVoting(),"V10"));
    }

    @org.junit.jupiter.api.Test
    void candidateExist() {
        VotingManager votingManager=new VotingManager();
        Assert.assertTrue(votingManager.candidateExist(VotingCreator.createVoting(),"P1"));
    }

    @org.junit.jupiter.api.Test
    void getVotingName() {
        VotingManager votingManager=new VotingManager();
        Assert.assertEquals(votingManager.getVotingName(VotingCreator.createVoting()),  "Voting no 1");
    }

}