package org.hyperledger.fabric.chaincode.ChaincodeExceptions;

public class ObjectInStubException extends Exception {
    String objectName;

    public ObjectInStubException(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectName() {
        return objectName;
    }
}
