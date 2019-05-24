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
import com.google.gson.JsonParser;
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
        else if (func.equals("getCommittees"))
            return getCommittees(stub, params);
        else if (func.equals("getCommittee"))
            return getCommittee(stub, params);
        else if (func.equals("vote"))
            return vote(stub, params);
        else if (func.equals("getVote"))
            return getVoteByTokenId(stub, params);
//        else if (func.equals("getResultsByCandidates"))
//            return getVoteResults(stub, params);
//        else if (func.equals("getResultsByParties"))
//            return getCandidates(stub, params);
//        else if (func.equals("beginVoting"))
//            return getCandidates(stub, params);
//        else if (func.equals("endVoting"))
//            return getCandidates(stub, params);
        return newErrorResponse(responseError("Unsupported method", ""));
    }

    //{"Args":["createVoting"]}
    private Response createVoting(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0", ""));
        Voting voting = VotingCreator.createVoting();
        try {
            //candidates
            JsonArray json;
            json = new JsonArray();
            int i = 0;
            for (Candidate candidate: voting.getCandidates()) {
                stub.putState(candidate.getCandidateId(), (new ObjectMapper()).writeValueAsBytes(candidate));
                JsonObject obj = new JsonObject();
                obj.addProperty("id", "" + i);
                obj.addProperty("cid", "" + candidate.getCandidateId());
                json.add(obj);
                i++;
            }
            stub.putState("candidatesList", (new ObjectMapper()).writeValueAsBytes(json.toString()));

            //committees
            JsonArray committeesJsonArray = new JsonArray();
            for (Committee committee : voting.getCommittees()) {
                //putting id to list
                committeesJsonArray.add(committee.getCommitteeId());

                //putting single committee object into blockchain
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
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0", ""));
        try {
            String candidatesString = stub.getStringState("candidatesList");
            if (!checkString(candidatesString))
                return newErrorResponse(responseError("Nonexistent candidates list", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            String candidatesJsonString = objectMapper.readValue(candidatesString, String.class);
            JsonArray json = new JsonParser().parse(candidatesJsonString).getAsJsonArray();
            JsonArray jarray = new JsonArray();
            for (int i = 0; i < json.size(); i++)
            {
                String c = json.get(i).getAsJsonObject().get("cid").getAsString();
                String cdString = stub.getStringState(c);
                objectMapper = new ObjectMapper();
                Candidate cd = objectMapper.readValue(cdString, Candidate.class);
                JsonObject obj = new JsonObject();
                obj.addProperty("candidateId", cd.candidateId);
                obj.addProperty("firstName", cd.firstName);
                obj.addProperty("lastName", cd.lastName);
                obj.addProperty("party", cd.party);
                obj.addProperty("partyNo", cd.partyNo);
                obj.addProperty("listNo", cd.listNo);
                obj.addProperty("age", cd.age);
                jarray.add(obj);
            }
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(jarray.toString()))));
        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    //{"Args":["getCommittees"]}
    private Response getCommittees(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0", ""));
        try {
            String committeeString = stub.getStringState("committeesList");
            if (!checkString(committeeString))
                return newErrorResponse(responseError("Nonexistent committees list", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            String committeesJsonString = objectMapper.readValue(committeeString, String.class);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(
                    (new ObjectMapper()).writeValueAsString(committeesJsonString))));
        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }



    //{"Args":["getCommittee","COM1"]}
    private Response getCommittee(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        try {
            String committeeString = stub.getStringState(args.get(0));
            if (!checkString(committeeString))
                return newErrorResponse(responseError("Nonexistent voting", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            Committee committee = objectMapper.readValue(committeeString, Committee.class);
            // JsonArray json = new JsonArray(candidatesJsonString);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(committee))));
        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    //{"Args":["vote","P1", "V10", "123"]}
    private Response vote(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 3", ""));

        String candidateId = args.get(0);
        String committeeId = args.get(1);
        String tokenId = args.get(2);

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            //is committee valid?
            String committeeString = stub.getStringState(committeeId);
            if (!checkString(committeeString))
                return newErrorResponse(responseError("Nonexistent committee", ""));
            Committee committee = objectMapper.readValue(committeeString, Committee.class);
            if (committee.getVotes().size() + 1 > committee.getVotesNo())
                return newErrorResponse(responseError("You can't add more votes to this committee", ""));

            //is candidate valid?
            String candidateString = stub.getStringState(candidateId);
            if (!checkString(candidateString))
                return newErrorResponse(responseError("Nonexistent candidate", ""));

            //is tokenId valid?
            String tokenString = stub.getStringState(tokenId);
            if (checkString(tokenString))
                return newErrorResponse(responseError(tokenString, ""));

            //vote
            Vote vote = new Vote(tokenId, candidateId);
            stub.putState(vote.getTokenId(), (new ObjectMapper()).writeValueAsBytes(vote));

            //adding tokenId to committee
            committee.addVote(tokenId);
            stub.putState(committee.getCommitteeId(), (new ObjectMapper()).writeValueAsBytes(committee));

            return newSuccessResponse(responseSuccess("Voted successfully\n"));

        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }


    //{"Args":["getVote","123"]}
    private Response getVoteByTokenId(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        try {
            String voteString = stub.getStringState(args.get(0));
            if (!checkString(voteString))
                return newErrorResponse(responseError("Nonexistent vote", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            Vote vote = objectMapper.readValue(voteString, Vote.class);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(vote))));
        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    public static void main(String[] args) {
        new VotingChaincode().start(args);
    }
}