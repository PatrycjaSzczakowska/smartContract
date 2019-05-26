package org.hyperledger.fabric.chaincode;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
        else if (func.equals("getVotes"))
            return getVotes(stub, params);
        else if (func.equals("getResultsByCandidates"))
            return getResultsByCandidates(stub, params);
        else if (func.equals("getResultsByParties"))
            return getResultsByParties(stub, params);
//        else if (func.equals("beginVoting"))
//            return getCandidates(stub, params);
//        else if (func.equals("endVoting"))
//            return getCandidates(stub, params);
        return newErrorResponse(responseError("Unsupported method"));
    }

    //{"Args":["createVoting"]}
    private Response createVoting(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0"));
        Voting voting = VotingCreator.createVoting();
        try {
            //candidates
            JsonArray candidatesJsonArray = new JsonArray();
            int i = 0;
            for (Candidate candidate : voting.getCandidates()) {
                //putting single candidate object
                stub.putState(candidate.getCandidateId(), (new ObjectMapper()).writeValueAsBytes(candidate));

                //putting id to list
                JsonObject candidateJsonObject = new JsonObject();
                candidateJsonObject.addProperty("id", "" + i);
                candidateJsonObject.addProperty("cid", "" + candidate.getCandidateId());
                candidatesJsonArray.add(candidateJsonObject);
                i++;
            }
            stub.putState("candidatesList", (new ObjectMapper()).writeValueAsBytes(candidatesJsonArray.toString()));

            //committees
            JsonArray committeesJsonArray = new JsonArray();
            i = 0;
            for (Committee committee : voting.getCommittees()) {
                //putting id to list
                JsonObject committeeJsonObject = new JsonObject();
                committeeJsonObject.addProperty("id", "" + i);
                committeeJsonObject.addProperty("cid", "" + committee.getCommitteeId());
                committeesJsonArray.add(committeeJsonObject);
                i++;

                //putting single committee object
                stub.putState(committee.getCommitteeId(), (new ObjectMapper()).writeValueAsBytes(committee));
            }
            stub.putState("committeesList", (new ObjectMapper()).writeValueAsBytes(committeesJsonArray.toString()));

            //votes empty list
            JsonArray tokensArray = new JsonArray();
            stub.putState("tokensList", (new ObjectMapper()).writeValueAsBytes(tokensArray.toString()));


        } catch (JsonProcessingException e) {
            return newErrorResponse(responseError("Error during creating voting"));
        }
        return newSuccessResponse(responseSuccess("Voting added"));
    }

    //{"Args":["getCandidates"]}
    private Response getCandidates(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0"));
        try {
            String candidatesString = stub.getStringState("candidatesList");
            if (!checkString(candidatesString))
                return newErrorResponse(responseError("Nonexistent candidates list"));
            ObjectMapper objectMapper = new ObjectMapper();
            String candidatesJsonString = objectMapper.readValue(candidatesString, String.class);
            JsonArray json = new JsonParser().parse(candidatesJsonString).getAsJsonArray();
            JsonArray jarray = new JsonArray();
            for (int i = 0; i < json.size(); i++) {
                String candidateId = json.get(i).getAsJsonObject().get("cid").getAsString();
                String candidateString = stub.getStringState(candidateId);
                Candidate candidate = objectMapper.readValue(candidateString, Candidate.class);
                JsonObject obj = new JsonObject();
                obj.addProperty("candidateId", candidate.candidateId);
                obj.addProperty("firstName", candidate.firstName);
                obj.addProperty("lastName", candidate.lastName);
                obj.addProperty("party", candidate.party);
                obj.addProperty("partyNo", candidate.partyNo);
                obj.addProperty("listNo", candidate.listNo);
                obj.addProperty("age", candidate.age);
                jarray.add(obj);
            }
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(jarray.toString()))));
        } catch (Throwable e) {
            return newErrorResponse(responseError("Error during candidates mapping"));
        }
    }

    //{"Args":["getCommittees"]}
    private Response getCommittees(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0"));
        try {
            String committeeString = stub.getStringState("committeesList");
            if (!checkString(committeeString))
                return newErrorResponse(responseError("Nonexistent committees list"));
            ObjectMapper objectMapper = new ObjectMapper();
            String committeesJsonString = objectMapper.readValue(committeeString, String.class);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(
                    (new ObjectMapper()).writeValueAsString(committeesJsonString))));
        } catch (Throwable e){
            return newErrorResponse(responseError("Error during committees mapping"));
        }
    }

    //{"Args":["getCommittee","COM1"]}
    private Response getCommittee(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1"));
        try {
            String committeeString = stub.getStringState(args.get(0));
            if (!checkString(committeeString))
                return newErrorResponse(responseError("Nonexistent committee"));
            ObjectMapper objectMapper = new ObjectMapper();
            Committee committee = objectMapper.readValue(committeeString, Committee.class);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(committee))));
        } catch (Throwable e) {
            return newErrorResponse(responseError("Error during committee mapping"));
        }
    }

    //{"Args":["vote","P1", "V10", "123"]}
    private Response vote(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 3"));

        String candidateId = args.get(0);
        String committeeId = args.get(1);
        String tokenId = args.get(2);

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            //is committee valid?
            String committeeString = stub.getStringState(committeeId);
            if (!checkString(committeeString))
                return newErrorResponse(responseError("Nonexistent committee"));
            Committee committee = objectMapper.readValue(committeeString, Committee.class);
            if (committee.getVotes().size() + 1 > committee.getVotesNo())
                return newErrorResponse(responseError("More votes cannot be added in this committee"));

            //is candidate valid?
            String candidateString = stub.getStringState(candidateId);
            if (!checkString(candidateString))
                return newErrorResponse(responseError("Nonexistent candidate"));

            //is tokenId valid?
            String tokenString = stub.getStringState(tokenId);
            if (checkString(tokenString))
                return newErrorResponse(responseError("Vote with given tokenId already exists"));

            //creating vote object
            Vote vote = new Vote(tokenId, candidateId);
            stub.putState(vote.getTokenId(), (new ObjectMapper()).writeValueAsBytes(vote));

            //adding tokenId to tokensList
            String tokensString = stub.getStringState("tokensList");
            if (!checkString(tokensString))
                return newErrorResponse(responseError("Nonexistent tokensList"));

            String tokensJsonString = objectMapper.readValue(tokensString, String.class);
            JsonArray tokensArray = new JsonParser().parse(tokensJsonString).getAsJsonArray();

            JsonObject tokenJsonObject = new JsonObject();
            tokenJsonObject.addProperty("id", "" + tokensArray.size() + 1);
            tokenJsonObject.addProperty("tid", "" + vote.getTokenId());
            tokensArray.add(tokenJsonObject);

            stub.putState("tokensList", (new ObjectMapper()).writeValueAsBytes(tokensArray.toString()));

            //adding tokenId to committee
            committee.addVote(tokenId);
            stub.putState(committee.getCommitteeId(), (new ObjectMapper()).writeValueAsBytes(committee));

            return newSuccessResponse(responseSuccess("Voted successfully\n"));

        } catch (Throwable e) {
            return newErrorResponse(responseError("Error during object mapping"));
        }
    }

    //{"Args":["getVote","123"]}
    private Response getVoteByTokenId(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1"));
        try {
            String voteString = stub.getStringState(args.get(0));
            if (!checkString(voteString))
                return newErrorResponse(responseError("Nonexistent vote"));
            ObjectMapper objectMapper = new ObjectMapper();
            Vote vote = objectMapper.readValue(voteString, Vote.class);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(vote))));
        } catch (Throwable e) {
            return newErrorResponse(responseError("Error during vote mapping"));
        }
    }

    //{"Args":["getCommittees"]}
    private Response getVotes(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0"));
        try {
            String votesString = stub.getStringState("tokensList");
            if (!checkString(votesString))
                return newErrorResponse(responseError("Nonexistent votes list"));
            ObjectMapper objectMapper = new ObjectMapper();
            String votesJsonString = objectMapper.readValue(votesString, String.class);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(
                    (new ObjectMapper()).writeValueAsString(votesJsonString))));
        } catch (Throwable e) {
            return newErrorResponse(responseError("Error during votes mapping"));
        }
    }

    //{"Args":["getResultsByCandidates"]}
    private Response getResultsByCandidates(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0"));
        try {
            Map<String, Integer> resultsByCandidates = new HashMap<>();

            ObjectMapper objectMapper = new ObjectMapper();

            String candidatesString = stub.getStringState("candidatesList");
            if (!checkString(candidatesString))
                return newErrorResponse(responseError("Nonexistent candidates list"));
            String candidatesJsonString = objectMapper.readValue(candidatesString, String.class);
            JsonArray candidatesJsonArray = new JsonParser().parse(candidatesJsonString).getAsJsonArray();
            for (int i = 0; i < candidatesJsonArray.size(); i++) {
                String candidateId = candidatesJsonArray.get(i).getAsJsonObject().get("cid").getAsString();
                resultsByCandidates.put(candidateId, 0);
            }

            String votesString = stub.getStringState("tokensList");
            if (!checkString(votesString))
                return newErrorResponse(responseError("Nonexistent votes list"));
            String votesJsonString = objectMapper.readValue(votesString, String.class);
            JsonArray votesJsonArray = new JsonParser().parse(votesJsonString).getAsJsonArray();
            for (int i = 0; i < votesJsonArray.size(); i++) {
                String tokenId = votesJsonArray.get(i).getAsJsonObject().get("tid").getAsString();
                String voteString = stub.getStringState(tokenId);
                Vote vote = objectMapper.readValue(voteString, Vote.class);

                resultsByCandidates.put(vote.getCandidateId(), resultsByCandidates.get(vote.getCandidateId()) + 1);
            }

            JsonArray resultsJsonArray = new JsonArray();
            for (String candidateId : resultsByCandidates.keySet()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("candidateId", candidateId);
                obj.addProperty("votes", resultsByCandidates.get(candidateId));
                resultsJsonArray.add(obj);
            }
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(resultsJsonArray.toString()))));

        } catch (Throwable e) {
            return newErrorResponse(responseError("Error during object mapping"));
        }
    }

    //{"Args":["getResultsByParties"]}
    private Response getResultsByParties(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 0"));
        try {
            Map<String, Integer> resultsByParties = new HashMap<>();
            List<Candidate> candidates = new ArrayList<>();

            ObjectMapper objectMapper = new ObjectMapper();

            String candidatesString = stub.getStringState("candidatesList");
            if (!checkString(candidatesString))
                return newErrorResponse(responseError("Nonexistent candidates list"));
            String candidatesJsonString = objectMapper.readValue(candidatesString, String.class);
            JsonArray candidatesJsonArray = new JsonParser().parse(candidatesJsonString).getAsJsonArray();
            for (int i = 0; i < candidatesJsonArray.size(); i++) {
                String candidateId = candidatesJsonArray.get(i).getAsJsonObject().get("cid").getAsString();
                String candidateString = stub.getStringState(candidateId);
                Candidate candidate = objectMapper.readValue(candidateString, Candidate.class);
                if (!resultsByParties.containsKey(candidate.getParty()))
                    resultsByParties.put(candidate.getParty(), 0);

                candidates.add(candidate);
            }

            String votesString = stub.getStringState("tokensList");
            if (!checkString(votesString))
                return newErrorResponse(responseError("Nonexistent votes list"));
            String votesJsonString = objectMapper.readValue(votesString, String.class);
            JsonArray votesJsonArray = new JsonParser().parse(votesJsonString).getAsJsonArray();
            for (int i = 0; i < votesJsonArray.size(); i++) {
                String tokenId = votesJsonArray.get(i).getAsJsonObject().get("tid").getAsString();
                String voteString = stub.getStringState(tokenId);
                Vote vote = objectMapper.readValue(voteString, Vote.class);
                String partyId = candidates.stream()
                        .filter(candidate -> candidate.getCandidateId().equals(vote.getCandidateId()))
                        .findFirst()
                        .get().getParty();
                resultsByParties.put(partyId, resultsByParties.get(partyId) + 1);
            }

            JsonArray resultsJsonArray = new JsonArray();
            for (String candidateId : resultsByParties.keySet()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("partyId", candidateId);
                obj.addProperty("votes", resultsByParties.get(candidateId));
                resultsJsonArray.add(obj);
            }
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(resultsJsonArray.toString()))));


        } catch (Throwable e) {
            return newErrorResponse(responseError("Error during object mapping"));
        }
    }

    public static void main(String[] args) {
        new VotingChaincode().start(args);
    }
}