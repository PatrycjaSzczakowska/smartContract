package org.hyperledger.fabric.chaincode.Models;

import org.junit.Assert;

import static org.junit.jupiter.api.Assertions.*;

class VotingManagerTest {

    @org.junit.jupiter.api.Test
    void vote() {
        VotingManager votingManager=new VotingManager(VotingCreator.createVoting());
        Assert.assertTrue(votingManager.vote("P2"));
    }

    @org.junit.jupiter.api.Test
    void voterExist() {
        VotingManager votingManager=new VotingManager(VotingCreator.createVoting());
        Assert.assertTrue(votingManager.voterExist("V10"));
    }

    @org.junit.jupiter.api.Test
    void candidateExist() {
        VotingManager votingManager=new VotingManager(VotingCreator.createVoting());
        Assert.assertTrue(votingManager.candidateExist("P1"));
    }

    @org.junit.jupiter.api.Test
    void getVotingName() {
        VotingManager votingManager=new VotingManager(VotingCreator.createVoting());
        Assert.assertEquals(votingManager.getVotingName(),  "Voting no 1");
    }

}