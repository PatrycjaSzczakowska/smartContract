package org.hyperledger.fabric.chaincode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.hyperledger.fabric.chaincode.ChaincodeExceptions.NoObjectInStubException;
import org.hyperledger.fabric.chaincode.ChaincodeExceptions.ObjectInStubException;
import org.hyperledger.fabric.chaincode.Models.*;
import org.hyperledger.fabric.shim.ChaincodeStub;
import static org.hyperledger.fabric.chaincode.Utils.checkString;

public class VotingHelper {
    public static VotingStatusEnum getStatus(ChaincodeStub stub) throws Throwable {
        String votingStatusString = stub.getStringState(VotingObjectsEnum.STATUS.getId());
        if (!checkString(votingStatusString))
            throw new NoObjectInStubException("Voting");

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(votingStatusString, VotingStatusEnum.class);
    }


    public static boolean canCreateVoting (ChaincodeStub stub) {
        String votingStatusString = stub.getStringState(VotingObjectsEnum.STATUS.getId());
        return !checkString(votingStatusString);
    }

    public static Committee getCommittee(ChaincodeStub stub, String id) throws Throwable {
        String committeeString = stub.getStringState(id);
        if (!checkString(committeeString))
            throw new NoObjectInStubException("Committee");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(committeeString, Committee.class);
    }

    public static Candidate getCandidate(ChaincodeStub stub, String id) throws Throwable {
        String candidateString = stub.getStringState(id);
        if (!checkString(candidateString))
            throw new NoObjectInStubException("Candidate");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(candidateString, Candidate.class);
    }

    public static JsonArray getCandidatesJsonArray(ChaincodeStub stub) throws Throwable {
        String candidatesString = stub.getStringState(VotingObjectsEnum.CANDIDATES.getId());
        if (!checkString(candidatesString))
            throw new NoObjectInStubException("candidatesList");
        ObjectMapper objectMapper = new ObjectMapper();
        String candidatesJsonString = objectMapper.readValue(candidatesString, String.class);
        return new JsonParser().parse(candidatesJsonString).getAsJsonArray();
    }

    public static JsonArray getCommitteesJsonArray(ChaincodeStub stub) throws Throwable {
        String committeesString = stub.getStringState(VotingObjectsEnum.COMMITTEES.getId());
        if (!checkString(committeesString))
            throw new NoObjectInStubException("committeesList");
        ObjectMapper objectMapper = new ObjectMapper();
        String candidatesJsonString = objectMapper.readValue(committeesString, String.class);
        return new JsonParser().parse(candidatesJsonString).getAsJsonArray();
    }

    public static JsonArray getVotesJsonArray(ChaincodeStub stub) throws Throwable {
        String votesString = stub.getStringState(VotingObjectsEnum.TOKENS.getId());
        if (!checkString(votesString))
            throw new NoObjectInStubException("tokenIdsList");
        ObjectMapper objectMapper = new ObjectMapper();
        String candidatesJsonString = objectMapper.readValue(votesString, String.class);
        return new JsonParser().parse(candidatesJsonString).getAsJsonArray();
    }

    public static Vote getVote(ChaincodeStub stub, String id) throws Throwable {
        String voteString = stub.getStringState(id);
        if (!checkString(voteString))
            throw new NoObjectInStubException("Vote");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(voteString, Vote.class);
    }

    public static void candidateNotExists (ChaincodeStub stub, String id) throws NoObjectInStubException {
        String candidateString = stub.getStringState(id);
        if (!checkString(candidateString))
            throw new NoObjectInStubException("Candidate");
    }

    public static void tokenExists (ChaincodeStub stub, String id) throws ObjectInStubException {
        String tokenString = stub.getStringState(id);
        if (checkString(tokenString))
            throw new ObjectInStubException("Token");
    }
}
