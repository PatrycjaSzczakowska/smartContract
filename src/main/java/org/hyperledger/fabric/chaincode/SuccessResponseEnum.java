package org.hyperledger.fabric.chaincode;

public enum SuccessResponseEnum {
    INIT("Init"),
    VOTING_STARTED("The voting was successfully started"),
    VOTING_ENDED("The voting was successfully ended"),
    VOTING_CREATED("The voting was successfully created"),
    VOTED_SUCCESS("You voted successfully");

    private String text;

    SuccessResponseEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
