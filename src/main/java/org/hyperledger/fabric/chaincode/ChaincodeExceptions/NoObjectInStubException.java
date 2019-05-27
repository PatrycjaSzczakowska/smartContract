package org.hyperledger.fabric.chaincode.ChaincodeExceptions;

public class NoObjectInStubException extends Exception {
    String objectName;

    public NoObjectInStubException(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectName() {
        return objectName;
    }
}
