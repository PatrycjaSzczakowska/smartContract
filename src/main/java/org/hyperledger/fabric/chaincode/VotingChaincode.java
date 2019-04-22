package org.hyperledger.fabric.chaincode;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.Models.Voting;
import org.hyperledger.fabric.chaincode.Models.VotingCreator;
import org.hyperledger.fabric.chaincode.Models.VotingManager;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import static org.hyperledger.fabric.chaincode.ChaincodeResponse.*;
import static org.hyperledger.fabric.chaincode.Utils.checkString;

public class VotingChaincode extends ChaincodeBase {

    @Override
    public Response init(ChaincodeStub stub) {
        Voting voting = VotingCreator.createVoting();
        try {
            if (checkString(stub.getStringState(voting.getName()))) {
                return newErrorResponse(responseError("Voting already exits", ""));
            }
            stub.putState(voting.getName(), (new ObjectMapper()).writeValueAsBytes(voting));
        } catch (JsonProcessingException e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
        return newSuccessResponse(responseSuccess("Init"));
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        String func = stub.getFunction();
        List<String> params = stub.getParameters();
        if (func.equals("getCandidates"))
            return getCandidates(stub,params);
        else if (func.equals("getUsers"))
            return getVoters(stub,params);
        else if (func.equals("vote"))
            return vote(stub, params);
        else if (func.equals("getVoteResults"))
            return getVoteResults(stub, params);
        return newErrorResponse(responseError("Unsupported method", ""));
    }

    //getCandidates
    private Response getCandidates(ChaincodeStub stub, List<String> args ) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        String votingName = args.get(0);
        if (!checkString(votingName))
            return newErrorResponse(responseError("Invalid argument", ""));
        try {
            String votingString = stub.getStringState(votingName);
            if(!checkString(votingString))
                return newErrorResponse(responseError("Nonexistent voting", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            Voting voting = objectMapper.readValue(votingString, Voting.class);
            //return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(voting.getCandidates())));
            //TODO
            return newErrorResponse();
        } catch(Throwable e){
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    //getUsers ?
    private Response getVoters(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        String votingName = args.get(0);
        if (!checkString(votingName))
            return newErrorResponse(responseError("Invalid argument", ""));
        try {
            String votingString = stub.getStringState(votingName);
            if(!checkString(votingString))
                return newErrorResponse(responseError("Nonexistent voting", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            Voting voting = objectMapper.readValue(votingString, Voting.class);
            //return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(voting.getCandidates())));
            //TODO
            return newErrorResponse();
        } catch(Throwable e){
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    //vote
    private Response vote(ChaincodeStub stub, List<String> args) {
        if (args.size() != 3)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 2", ""));
        String votingName= args.get(0);
        String voterId = args.get(1);
        String candidateId = args.get(2);
        if (!checkString(votingName) || !checkString(voterId) || !checkString(candidateId)) {
            return newErrorResponse(responseError("Invalid argument(s)", ""));
        }

        try {
            String votingString = stub.getStringState(votingName);
            if(!checkString(votingString))
                return newErrorResponse(responseError("Nonexistent voting", ""));

            ObjectMapper objectMapper = new ObjectMapper();
            Voting voting = objectMapper.readValue(votingString, Voting.class);

            if (!VotingManager.voterExist(voting, voterId)) {
                return newErrorResponse(responseError("Invalid voter ID)", ""));
            } else if (!VotingManager.candidateExist(voting, candidateId)) {
                return newErrorResponse(responseError("Invalid candidate ID)", ""));
            }
            boolean votingFlag = VotingManager.vote(voting, candidateId);
            if (votingFlag) {
                stub.putState(votingName, objectMapper.writeValueAsBytes(voting));
                return newSuccessResponse(responseSuccess("Voting went good"));
            }
            return newErrorResponse(responseError("Voting went wrong", ""));


        } catch (IOException e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    private Response getVoteResults(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 1", ""));
        String votingName = args.get(0);
        if (!checkString(votingName))
            return newErrorResponse(responseError("Invalid argument", ""));
        try {
            String votingString = stub.getStringState(votingName);
            if(!checkString(votingString))
                return newErrorResponse(responseError("Nonexistent voting", ""));
            ObjectMapper objectMapper = new ObjectMapper();
            Voting voting = objectMapper.readValue(votingString, Voting.class);
            //return newSuccessResponse((new ObjectMapper()).writeValueAsBytes(responseSuccessObject(voting.getCandidates())));
            //TODO
            return newErrorResponse();
        } catch(Throwable e){
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    public static void main(String[] args) {
        new VotingChaincode().start(args);
    }
}