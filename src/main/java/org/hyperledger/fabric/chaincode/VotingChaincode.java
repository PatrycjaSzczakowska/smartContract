package org.hyperledger.fabric.chaincode;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hyperledger.fabric.chaincode.ChaincodeExceptions.NoObjectInStubException;
import org.hyperledger.fabric.chaincode.ChaincodeExceptions.ObjectInStubException;
import org.hyperledger.fabric.chaincode.Models.*;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;


import static org.hyperledger.fabric.chaincode.ChaincodeResponse.*;
import static org.hyperledger.fabric.chaincode.Utils.checkString;

public class VotingChaincode extends ChaincodeBase {

    @Override
    public Response init(ChaincodeStub stub) {
        return newSuccessResponse(responseSuccess(SuccessResponseEnum.INIT.getText()));
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        String func = stub.getFunction();
        List<String> params = stub.getParameters();
        switch (func) {
            case "createVoting":
                return createVoting(stub, params);
            case "getCandidates":
                return getCandidates(stub, params);
            case "getCommittees":
                return getCommittees(stub, params);
            case "getCommittee":
                return getCommittee(stub, params);
            case "vote":
                return vote(stub, params);
            case "getVote":
                return getVoteByTokenId(stub, params);
            case "getVotes":
                return getVotes(stub, params);
            case "getVotingStatus":
                return getVotingStatus(stub, params);
            case "getResultsByCandidates":
                return getResultsByCandidates(stub, params);
            case "getResultsByParties":
                return getResultsByParties(stub, params);
            case "beginVoting":
                return beginVoting(stub, params);
            case "endVoting":
                return endVoting(stub, params);
        }
        return newErrorResponse(responseError(ErrorResponseEnum.UNSUPPORTED_METHOD.getText()));
    }

    private Response beginVoting(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_0.getText()));
        try {
            VotingStatusEnum votingStatus = VotingHelper.getStatus(stub);

            if (VotingStatusEnum.CREATED.equals(votingStatus)) {
                votingStatus = VotingStatusEnum.STARTED;
                stub.putState(VotingObjectsEnum.STATUS.getId(), (new ObjectMapper()).writeValueAsBytes(votingStatus));
                return newSuccessResponse(responseSuccess(SuccessResponseEnum.VOTING_STARTED.getText()));
            } else if (VotingStatusEnum.STARTED.equals(votingStatus)) {
                return newErrorResponse(responseError(ErrorResponseEnum.VOTING_ALREADY_STARTED.getText()));
            } else if (VotingStatusEnum.ENDED.equals(votingStatus)) {
                return newErrorResponse(responseError(ErrorResponseEnum.VOTING_ALREADY_ENDED.getText()));
            } else {
                return newErrorResponse(responseError(ErrorResponseEnum.STATUS_ERROR.getText()));
            }
        } catch (NoObjectInStubException e) {
            return newErrorResponse(responseError(ErrorResponseEnum.VOTING_NOT_CREATED.getText()));
        } catch (Throwable e) {
            return newErrorResponse(responseError(ErrorResponseEnum.MAPPING_ERROR.getText()));
        }
    }

    //{"Args":["endVoting","123"]}
    private Response endVoting(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_0.getText()));
        try {
            VotingStatusEnum votingStatus = VotingHelper.getStatus(stub);

            if (VotingStatusEnum.STARTED.equals(votingStatus)) {
                votingStatus = VotingStatusEnum.ENDED;
                stub.putState(VotingObjectsEnum.STATUS.getId(), (new ObjectMapper()).writeValueAsBytes(votingStatus));
                return newSuccessResponse(responseSuccess(SuccessResponseEnum.VOTING_ENDED.getText()));
            } else if (VotingStatusEnum.CREATED.equals(votingStatus)) {
                return newErrorResponse(responseError(ErrorResponseEnum.VOTING_NOT_STARTED.getText()));
            } else if (VotingStatusEnum.ENDED.equals(votingStatus)) {
                return newErrorResponse(responseError(ErrorResponseEnum.VOTING_ALREADY_ENDED.getText()));
            } else {
                return newErrorResponse(responseError(ErrorResponseEnum.STATUS_ERROR.getText()));
            }
        } catch (NoObjectInStubException e) {
            return newErrorResponse(responseError(e.getObjectName() + ErrorResponseEnum.NO_OBJECT_ERROR.getText()));
        } catch (Throwable e) {
            return newErrorResponse(responseError(ErrorResponseEnum.MAPPING_ERROR.getText()));
        }
    }

    //{"Args":["createVoting"]}
    private Response createVoting(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_0.getText()));
        if (VotingHelper.canCreateVoting(stub)) {
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
                stub.putState(VotingObjectsEnum.CANDIDATES.getId(), (new ObjectMapper()).writeValueAsBytes(candidatesJsonArray.toString()));

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
                stub.putState(VotingObjectsEnum.COMMITTEES.getId(), (new ObjectMapper()).writeValueAsBytes(committeesJsonArray.toString()));

                //votes empty list
                JsonArray tokensArray = new JsonArray();
                stub.putState(VotingObjectsEnum.TOKENS.getId(), (new ObjectMapper()).writeValueAsBytes(tokensArray.toString()));

                //voting status ->  created
                stub.putState(VotingObjectsEnum.STATUS.getId(), (new ObjectMapper()).writeValueAsBytes(VotingStatusEnum.CREATED));

            } catch (JsonProcessingException e) {
                return newErrorResponse(responseError(ErrorResponseEnum.CREATE_VOTING_ERROR.getText()));
            }
            return newSuccessResponse(responseSuccess(SuccessResponseEnum.VOTING_CREATED.getText()));
        }
        return newErrorResponse(responseError(ErrorResponseEnum.VOTING_ALREADY_CREATED.getText()));
    }

    //{"Args":["getCandidates"]}
    private Response getCandidates(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_0.getText()));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonArray json = VotingHelper.getCandidatesJsonArray(stub);
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
            return newSuccessResponse(responseSuccess(jarray.toString()));
        } catch (NoObjectInStubException e) {
            return newErrorResponse(responseError(e.getObjectName() + ErrorResponseEnum.NO_OBJECT_ERROR.getText()));
        } catch (Throwable e) {
            return newErrorResponse(responseError(ErrorResponseEnum.MAPPING_ERROR.getText()));
        }
    }

    //{"Args":["getCommittees"]}
    private Response getCommittees(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_0.getText()));
        try {
            String committeeString = stub.getStringState(VotingObjectsEnum.COMMITTEES.getId());
            if (!checkString(committeeString))
                return newErrorResponse(responseError("committeesList" + ErrorResponseEnum.NO_OBJECT_ERROR.getText()));
            ObjectMapper objectMapper = new ObjectMapper();
            String committeesJsonString = objectMapper.readValue(committeeString, String.class);
            return newSuccessResponse(responseSuccess(committeesJsonString));
        } catch (Throwable e) {
            return newErrorResponse(responseError(ErrorResponseEnum.MAPPING_ERROR.getText()));
        }
    }

    //{"Args":["getCommittee","COM1"]}
    private Response getCommittee(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_1.getText()));
        try {
            Committee committee = VotingHelper.getCommittee(stub, args.get(0));
            return newSuccessResponse(responseSuccess((new ObjectMapper()).writeValueAsString(committee)));
        } catch (NoObjectInStubException e) {
            return newErrorResponse(responseError(e.getObjectName() + ErrorResponseEnum.NO_OBJECT_ERROR.getText()));
        } catch (Throwable e) {
            return newErrorResponse(responseError(ErrorResponseEnum.MAPPING_ERROR.getText()));
        }
    }

    //{"Args":["vote","P1", "V10", "123"]}
    private Response vote(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_3.getText()));

        String candidateId = args.get(0);
        String committeeId = args.get(1);
        String tokenId = args.get(2);

        try {
            VotingStatusEnum votingStatusEnum = VotingHelper.getStatus(stub);

            if (VotingStatusEnum.STARTED.equals(votingStatusEnum)) {

                Committee committee = VotingHelper.getCommittee(stub, committeeId);
                if (committee.getVotes().size() + 1 > committee.getVotesNo())
                    return newErrorResponse(responseError(ErrorResponseEnum.COMMITTEE_LIMIT.getText()));

                //is candidate valid?
                VotingHelper.candidateNotExists(stub, candidateId);

                //is tokenId valid?
                VotingHelper.tokenExists(stub, tokenId);

                //creating vote object
                Vote vote = new Vote(tokenId, candidateId);
                stub.putState(vote.getTokenId(), (new ObjectMapper()).writeValueAsBytes(vote));

                //adding tokenId to tokensList
                JsonArray tokensArray = VotingHelper.getVotesJsonArray(stub);

                JsonObject tokenJsonObject = new JsonObject();
                tokenJsonObject.addProperty("id", "" + tokensArray.size() + 1);
                tokenJsonObject.addProperty("tid", "" + vote.getTokenId());
                tokensArray.add(tokenJsonObject);

                stub.putState(VotingObjectsEnum.TOKENS.getId(), (new ObjectMapper()).writeValueAsBytes(tokensArray.toString()));

                //adding tokenId to committee
                committee.addVote(tokenId);
                stub.putState(committee.getCommitteeId(), (new ObjectMapper()).writeValueAsBytes(committee));

                return newSuccessResponse(responseSuccess(SuccessResponseEnum.VOTED_SUCCESS.getText()));
            } else {
                return newErrorResponse(responseError(ErrorResponseEnum.VOTING_NOT_STARTED.getText()));
            }
        } catch (ObjectInStubException e) {
            return newErrorResponse(responseError(e.getObjectName() + ErrorResponseEnum.OBJECT_ERROR.getText()));
        } catch (NoObjectInStubException e) {
            return newErrorResponse(responseError(e.getObjectName() + ErrorResponseEnum.NO_OBJECT_ERROR.getText()));
        } catch (Throwable e) {
            return newErrorResponse(responseError(ErrorResponseEnum.MAPPING_ERROR.getText()));
        }

    }

    //{"Args":["getVote","123"]}
    private Response getVoteByTokenId(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_1.getText()));
        try {
            Vote vote = VotingHelper.getVote(stub, args.get(0));
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccess((new ObjectMapper()).writeValueAsString(vote))));
        } catch (NoObjectInStubException e) {
            return newErrorResponse(responseError(e.getObjectName() + ErrorResponseEnum.NO_OBJECT_ERROR.getText()));
        } catch (Throwable e) {
            return newErrorResponse(responseError(ErrorResponseEnum.MAPPING_ERROR.getText()));
        }
    }

    //{"Args":["getVotingStatus","123"]}
    private Response getVotingStatus(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_0.getText()));
        try {
            VotingStatusEnum votingStatusEnum = VotingHelper.getStatus(stub);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccess((new ObjectMapper()).writeValueAsString(votingStatusEnum))));
        } catch (NoObjectInStubException e) {
            return newErrorResponse(responseError(e.getObjectName() + ErrorResponseEnum.NO_OBJECT_ERROR.getText()));
        } catch (Throwable e) {
            return newErrorResponse(responseError(ErrorResponseEnum.MAPPING_ERROR.getText()));
        }
    }

    //{"Args":["getVotes"]}
    private Response getVotes(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_0.getText()));
        try {
            String votesString = stub.getStringState(VotingObjectsEnum.TOKENS.getId());
            if (!checkString(votesString))
                return newErrorResponse(responseError("votesList" + ErrorResponseEnum.NO_OBJECT_ERROR.getText()));
            ObjectMapper objectMapper = new ObjectMapper();
            String votesJsonString = objectMapper.readValue(votesString, String.class);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccess(
                    (new ObjectMapper()).writeValueAsString(votesJsonString))));
        } catch (Throwable e) {
            return newErrorResponse(responseError(ErrorResponseEnum.MAPPING_ERROR.getText()));
        }
    }

    //{"Args":["getResultsByCandidates"]}
    private Response getResultsByCandidates(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_0.getText()));
        try {
            if (VotingStatusEnum.ENDED.equals(VotingHelper.getStatus(stub))) {
                Map<String, Integer> resultsByCandidates = new HashMap<>();

                ObjectMapper objectMapper = new ObjectMapper();

                JsonArray candidatesJsonArray = VotingHelper.getCandidatesJsonArray(stub);
                for (int i = 0; i < candidatesJsonArray.size(); i++) {
                    String candidateId = candidatesJsonArray.get(i).getAsJsonObject().get("cid").getAsString();
                    resultsByCandidates.put(candidateId, 0);
                }

                JsonArray votesJsonArray = VotingHelper.getVotesJsonArray(stub);
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
                return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccess((new ObjectMapper()).writeValueAsString(resultsJsonArray.toString()))));
            }
            return newErrorResponse(responseError(ErrorResponseEnum.VOTING_NOT_ENDED.getText()));
        } catch (NoObjectInStubException e) {
            return newErrorResponse(responseError(e.getObjectName() + ErrorResponseEnum.NO_OBJECT_ERROR.getText()));
        } catch (Throwable e) {
            return newErrorResponse(responseError(ErrorResponseEnum.MAPPING_ERROR.getText()));
        }
    }

    //{"Args":["getResultsByParties"]}
    private Response getResultsByParties(ChaincodeStub stub, List<String> args) {
        if (args.size() != 0)
            return newErrorResponse(responseError(ErrorResponseEnum.NUMBER_OF_ARGUMENTS_0.getText()));
        try {
            if (VotingStatusEnum.ENDED.equals(VotingHelper.getStatus(stub))) {
                Map<String, Integer> resultsByParties = new HashMap<>();
                List<Candidate> candidates = new ArrayList<>();

                ObjectMapper objectMapper = new ObjectMapper();

                JsonArray candidatesJsonArray = VotingHelper.getCandidatesJsonArray(stub);

                for (int i = 0; i < candidatesJsonArray.size(); i++) {
                    String candidateId = candidatesJsonArray.get(i).getAsJsonObject().get("cid").getAsString();
                    String candidateString = stub.getStringState(candidateId);
                    Candidate candidate = objectMapper.readValue(candidateString, Candidate.class);
                    if (!resultsByParties.containsKey(candidate.getParty()))
                        resultsByParties.put(candidate.getParty(), 0);

                    candidates.add(candidate);
                }

                JsonArray votesJsonArray = VotingHelper.getVotesJsonArray(stub);
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
                return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccess((new ObjectMapper()).writeValueAsString(resultsJsonArray.toString()))));
            }
            return newErrorResponse(responseError(ErrorResponseEnum.VOTING_NOT_ENDED.getText()));
        } catch (NoObjectInStubException e) {
            return newErrorResponse(responseError(e.getObjectName() + ErrorResponseEnum.NO_OBJECT_ERROR.getText()));
        } catch (Throwable e) {
            return newErrorResponse(responseError(ErrorResponseEnum.MAPPING_ERROR.getText()));
        }
    }

    public static void main(String[] args) {
        new VotingChaincode().start(args);
    }
}