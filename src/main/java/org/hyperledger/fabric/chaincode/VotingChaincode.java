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
            return getCandidate(stub, params);
        else if (func.equals("vote"))
            return vote(stub, params);
        else if (func.equals("getResultsByCandidates"))
            return getVoteResults(stub, params);
        else if (func.equals("getResultsByParties"))
            return getCandidate(stub, params);
        else if (func.equals("beginVoting"))
            return getCandidate(stub, params);
        else if (func.equals("endVoting"))
            return getCandidate(stub, params);
        return newErrorResponse(responseError("Unsupported method", ""));
    }

    //{"Args":["createVoting"]}
    private Response createVoting(ChaincodeStub stub, List<String> args) {
        if (args.size()!=0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        Voting voting = VotingCreator.createVoting();
        try {
            if (checkString(stub.getStringState(voting.getName()))) {
                // return newErrorResponse(responseError("Voting already exits", ""));
            }

            //candidates
            JsonArray json;
            json = new JsonArray();
            for (Candidate candidate: voting.getCandidates()) {
                stub.putState(candidate.getCandidateId(), (new ObjectMapper()).writeValueAsBytes(candidate));
                json.add(candidate.getCandidateId());
            }

            stub.putState("candidatesList", (new ObjectMapper()).writeValueAsBytes(json.toString()));

            //committees
            json = new JsonArray();
            for (Committee committee: voting.getCommittees()) {
                JsonObject obj = new JsonObject();
                obj.add("committeeId", new JsonPrimitive(committee.getCommitteeId()));
                //toString
                obj.add("votesNo", new JsonPrimitive(committee.getVotesNo() + ""));
                json.add(obj);

            }
            stub.putState("committeesList", (new ObjectMapper()).writeValueAsBytes(json.toString()));


        } catch (JsonProcessingException e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
        return newSuccessResponse(responseSuccess("Voting added"));
    }

    //{"Args":["getCandidates","Voting1"]}
    private Response getCandidate(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        String candidateId = args.get(0);
        if (!checkString(candidateId))
            return newErrorResponse(responseError("Invalid argument", ""));
        try {
            String candidateString = stub.getStringState(candidateId);
            if (!checkString(candidateString))
                return newErrorResponse(responseError("Nonexistent voting", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            Candidate candidate = objectMapper.readValue(candidateString, Candidate.class);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(candidate))));
        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }


    //{"Args":["vote","Voting1","V10", "P2"]}
    private Response vote(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 2", ""));
        String voterId = args.get(0);
        String candidateId = args.get(1);

        try {
            String candidateString = stub.getStringState(candidateId);
            if (!checkString(candidateString))
                return newErrorResponse(responseError("Nonexistent candidate", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            Candidate candidate = objectMapper.readValue(candidateString, Candidate.class);

            String voterString = stub.getStringState(voterId);
            if (!checkString(voterString))
                return newErrorResponse(responseError("Nonexistent voter", ""));
            objectMapper = new ObjectMapper();

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
            for (String candidateId: candidatesList.getCandidateIds()) {
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