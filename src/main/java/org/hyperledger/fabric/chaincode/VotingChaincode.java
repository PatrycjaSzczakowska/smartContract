package org.hyperledger.fabric.chaincode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.hyperledger.fabric.chaincode.Models.*;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;


import static org.hyperledger.fabric.chaincode.ChaincodeResponse.*;
import static org.hyperledger.fabric.chaincode.Utils.checkString;

public class VotingChaincode extends ChaincodeBase {

    @Override
    public Response init(ChaincodeStub stub) {
        return newSuccessResponse(responseSuccess("Init"));
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        String func = stub.getFunction();
        List<String> params = stub.getParameters();
        if (func.equals("createVoting"))
            return createVoting(stub, params);
        else if (func.equals("getCandidates"))
            return getCandidates(stub, params);
        else if (func.equals("getCommittee"))
            return getCommittee(stub, params);
        else if (func.equals("vote"))
            return vote(stub, params);
        else if (func.equals("getResultsByCandidates"))
            return getVoteResults(stub, params);
        else if (func.equals("getResultsByParties"))
            return getCandidates(stub, params);
        else if (func.equals("beginVoting"))
            return getCandidates(stub, params);
        else if (func.equals("endVoting"))
            return getCandidates(stub, params);
        return newErrorResponse(responseError("Unsupported method", ""));
    }

    //{"Args":["createVoting"]}
    private Response createVoting(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        Voting voting = VotingCreator.createVoting();
        try {
            if (checkString(stub.getStringState(voting.getName()))) {
                // return newErrorResponse(responseError("Voting already exits", ""));
            }

            //candidates
            JsonArray candidatesJsonArray;
            candidatesJsonArray = new JsonArray();
            for (Candidate candidate : voting.getCandidates()) {
                //putting id to list
                candidatesJsonArray.add(candidate.getCandidateId());

                //putting single candidate object into blockchain
                stub.putState(candidate.getCandidateId(), (new ObjectMapper()).writeValueAsBytes(candidate));
            }
            stub.putState("candidatesList", (new ObjectMapper()).writeValueAsBytes(candidatesJsonArray.toString()));

            //committees
            JsonArray committeesJsonArray = new JsonArray();
            for (Committee committee : voting.getCommittees()) {
                //putting id to list
                committeesJsonArray.add(committee.getCommitteeId());

                //putting single committee object into blockchain
//                JsonObject committeeJsonObject = new JsonObject();
//                committeeJsonObject.add("committeeId", new JsonPrimitive(committee.getCommitteeId()));
//                committeeJsonObject.add("votesNo", new JsonPrimitive(committee.getVotesNo() + ""));
//
//                JsonArray tokensJsonArray = new JsonArray();
//                for (String tokenId : committee.getVotes()) {
//                    tokensJsonArray.add(tokenId);
//                }
//                committeeJsonObject.add("tokenIds", tokensJsonArray);

                stub.putState(committee.getCommitteeId(), (new ObjectMapper()).writeValueAsBytes(committee));
            }

            stub.putState("committeesList", (new ObjectMapper()).writeValueAsBytes(committeesJsonArray.toString()));

        } catch (JsonProcessingException e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
        return newSuccessResponse(responseSuccess("Voting added"));
    }

    //{"Args":["getCandidates"]}
    private Response getCandidates(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        try {
            String candidatesString = stub.getStringState("candidatesList");
            if (!checkString(candidatesString))
                return newErrorResponse(responseError("Nonexistent voting", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            String candidatesJsonString = objectMapper.readValue(candidatesString, String.class);
            // JsonArray json = new JsonArray(candidatesJsonString);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(candidatesJsonString))));
        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    //{"Args":["getCommittee"]}
    private Response getCommittee(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        try {
            String committeeString = stub.getStringState("committeesList");
            if (!checkString(committeeString))
                return newErrorResponse(responseError("Nonexistent voting", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            String committeesJsonString = objectMapper.readValue(committeeString, String.class);
            // JsonArray json = new JsonArray(candidatesJsonString);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(committeesJsonString))));
        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }



    //{"Args":["vote","Voting1","V10", "P2"]}
    private Response vote(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 2", ""));
        String voterId = args.get(0);
        String candidateId = args.get(1);
        String committeeId = args.get(2);

        try {
            String candidateString = stub.getStringState("");
            if (!checkString(candidateString))
                return newErrorResponse(responseError("Nonexistent candidate", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            Candidate candidate = objectMapper.readValue(candidateString, Candidate.class);

            String voterString = stub.getStringState(voterId);
            if (!checkString(voterString))
                return newErrorResponse(responseError("Nonexistent voter", ""));
            objectMapper = new ObjectMapper();

            stub.putState(candidate.getCandidateId(), (new ObjectMapper()).writeValueAsBytes(candidate));

//            if (voter.vote(candidateId)) {
//                candidate.addVote();
//                try {
//                    stub.putState(voter.getVoterId(), (new ObjectMapper()).writeValueAsBytes(voter));
//                    stub.putState(candidate.getCandidateId(), (new ObjectMapper()).writeValueAsBytes(candidate));
//                    return newSuccessResponse(responseSuccess("Voting went good"));
//                } catch (IOException e) {
//                    return newErrorResponse(responseError(e.getMessage(), ""));
//                }
//            }

            return newSuccessResponse(responseSuccess("Voter has already voted"));

        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }


    }

    //{"Args":["getVoteResults"]}
    private Response getVoteResults(ChaincodeStub stub, List<String> args) {
        try {
            String candidatesListString = stub.getStringState("candidatesIds");
            if (!checkString(candidatesListString))
                return newErrorResponse(responseError("Nonexistent candidatesIds", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            CandidatesList candidatesList = objectMapper.readValue(candidatesListString, CandidatesList.class);
            String response = "";
            for (String candidateId : candidatesList.getCandidateIds()) {
                String candidateString = stub.getStringState(candidateId);
                objectMapper = new ObjectMapper();
                Candidate candidate = objectMapper.readValue(candidateString, Candidate.class);
                response += (new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(candidate))) + "\n";
            }
            return newSuccessResponse(response);
        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    public static void main(String[] args) {
        new VotingChaincode().start(args);
    }
}