package org.hyperledger.fabric.chaincode;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        else if (func.equals("getCandidate"))
            return getCandidate(stub, params);
        else if (func.equals("getVoter"))
            return getVoter(stub, params);
        else if (func.equals("vote"))
            return vote(stub, params);
        else if (func.equals("getVoteResults"))
            return getVoteResults(stub, params);
        else if (func.equals("siema"))
            return siema(stub, params);
        else if (func.equals("getSiema"))
            return getSiema(stub, params);
        return newErrorResponse(responseError("Unsupported method", ""));
    }

    private Response siema(ChaincodeStub stub, List<String> args) {
        Siema siema = new Siema(args.get(0), args.get(1));
        try {
            stub.putState(siema.getSiemaId(), (new ObjectMapper()).writeValueAsBytes(siema));
        } catch (JsonProcessingException e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
        return newSuccessResponse(responseSuccess("siema added"));
    }

    private Response getSiema(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        String siemaId = args.get(0);
        if (!checkString(siemaId))
            return newErrorResponse(responseError("Invalid argument", ""));
        try {
            String siemaString = stub.getStringState(siemaId);
            if (false)
                return newErrorResponse(responseError("siema: " + siemaString, ""));
            if (!checkString(siemaString))
                return newErrorResponse(responseError("Nonexistent siema", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            Siema siema = objectMapper.readValue(siemaString, Siema.class);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(siema))));
            //TODO
        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }
    //{"Args":["createVoting","Voting1"]}
    private Response createVoting(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        String votingName = args.get(0);
        if (!checkString(votingName))
            return newErrorResponse(responseError("Invalid argument", ""));

        Voting voting = VotingCreator.createVoting();
        try {
            if (checkString(stub.getStringState(voting.getName()))) {
                // return newErrorResponse(responseError("Voting already exits", ""));
            }
            for (Candidate candidate: voting.getCandidates()) {
                stub.putState(candidate.getCandidateId(), (new ObjectMapper()).writeValueAsBytes(candidate));
            }

            for (Voter voter: voting.getVoters()) {
                stub.putState(voter.getVoterId(), (new ObjectMapper()).writeValueAsBytes(voter));
            }

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

    //{"Args":["getVoters","Voting1"]}
    private Response getVoter(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        String voterId = args.get(0);
        if (!checkString(voterId))
            return newErrorResponse(responseError("Invalid argument", ""));
        try {
            String voterString = stub.getStringState(voterId);
            if (!checkString(voterString))
                return newErrorResponse(responseError("Nonexistent voting", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            Voter voter = objectMapper.readValue(voterString, Voter.class);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(voter))));
            //TODO
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
        Voter voter = new Voter(voterId);
        Candidate candidate = new Candidate(candidateId);
        if (voter.vote(candidateId)) {
            candidate.addVote();
            try {
                stub.putState(voter.getVoterId(), (new ObjectMapper()).writeValueAsBytes(voter));
                stub.putState(candidate.getCandidateId(), (new ObjectMapper()).writeValueAsBytes(candidate));
                return newSuccessResponse(responseSuccess("Voting went good"));
            } catch (IOException e) {
                return newErrorResponse(responseError(e.getMessage(), ""));
            }
        }
        return newSuccessResponse(responseSuccess("Voter has already voted"));
    }

    //{"Args":["getVoteResults","Voting1"]}
    private Response getVoteResults(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        String votingName = args.get(0);
        if (!checkString(votingName))
            return newErrorResponse(responseError("Invalid argument", ""));
        try {
            String votingString = stub.getStringState(votingName);
            if (!checkString(votingString))
                return newErrorResponse(responseError("Nonexistent voting", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            Voting voting = objectMapper.readValue(votingString, Voting.class);
            return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject((new ObjectMapper()).writeValueAsString(voting.getVotingResults()))));
            //TODO
        } catch (Throwable e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    public static void main(String[] args) {
        new VotingChaincode().start(args);
    }
}