package org.hyperledger.fabric.chaincode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.chaincode.ChaincodeExceptions.NoObjectInStubException;
import org.hyperledger.fabric.chaincode.Models.VotingObjectsEnum;
import org.hyperledger.fabric.chaincode.Models.VotingStatusEnum;
import org.hyperledger.fabric.shim.ChaincodeStub;

import static org.hyperledger.fabric.chaincode.ChaincodeResponse.responseError;
import static org.hyperledger.fabric.chaincode.Utils.checkString;

public class VotingHelper {
    public static VotingStatusEnum getStatus(ChaincodeStub stub) throws Throwable {
        String votingStatusString = stub.getStringState(VotingObjectsEnum.STATUS.getId());
        if (!checkString(votingStatusString))
            throw new NoObjectInStubException("Voting");

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(votingStatusString, VotingStatusEnum.class);
    }
}
