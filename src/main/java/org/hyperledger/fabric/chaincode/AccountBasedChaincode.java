package org.hyperledger.fabric.chaincode;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.Models.VotingCreator;
import org.hyperledger.fabric.chaincode.Models.VotingManager;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import static org.hyperledger.fabric.chaincode.ChaincodeResponse.responseError;
import static org.hyperledger.fabric.chaincode.ChaincodeResponse.responseSuccess;

public class AccountBasedChaincode extends ChaincodeBase {
    VotingManager votingManager;

    @Override
    public Response init(ChaincodeStub stub) {
        votingManager = new VotingManager(VotingCreator.createVoting());

        return newSuccessResponse(responseSuccess("Init"));
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        String func = stub.getFunction();
        List<String> params = stub.getParameters();
        if (func.equals("getCandidates"))
            return getCandidates(stub);
        else if (func.equals("getUsers"))
            return getVoters(stub);
        else if (func.equals("vote"))
            return vote(stub, params);
        else if (func.equals("getVoteResults"))
            return getVoteResults(stub);
        return newErrorResponse(responseError("Unsupported method", ""));
    }

    //getCandidates
    private Response getCandidates(ChaincodeStub stub) {
        return null;
    }

    //getUsers ?
    private Response getVoters(ChaincodeStub stub) {
        return null;
    }
    //vote

    private Response vote(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2)
            return newErrorResponse(responseError("Incorrect number of arguments, expecting 2", ""));
        String voterId = args.get(0);
        String candidateId = args.get(1);
        if (!Utils.checkString(voterId) || !Utils.checkString(candidateId)) {
            return newErrorResponse(responseError("Invalid argument(s)", ""));
        } else if (!votingManager.voterExist(voterId)) {
            return newErrorResponse(responseError("Invalid voter ID)", ""));
        } else if (!votingManager.candidateExist(candidateId)) {
            return newErrorResponse(responseError("Invalid candidate ID)", ""));
        }
        boolean votingFlag = votingManager.vote(candidateId);
        if (votingFlag) {
            return newSuccessResponse(responseSuccess("Voted"));
        }
        return newErrorResponse(responseError("Voting went wrong", ""));
    }

    private Response getVoteResults(ChaincodeStub stub) {
        try {
            stub.putState(votingManager.getVotingName(), (new ObjectMapper()).writeValueAsBytes(votingManager.getVotingResults()));
            return newSuccessResponse(responseSuccess("Wallet created"));
        } catch (JsonProcessingException e) {
            return newErrorResponse(responseError(e.getMessage(), ""));
        }
    }

    public static void main(String[] args) {
        new AccountBasedChaincode().start(args);
    }
}