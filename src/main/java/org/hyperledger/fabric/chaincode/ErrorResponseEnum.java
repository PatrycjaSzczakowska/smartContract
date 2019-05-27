package org.hyperledger.fabric.chaincode;

public enum ErrorResponseEnum {
    NUMBER_OF_ARGUMENTS_0("Incorrect number of arguments, expecting 0"),
    NUMBER_OF_ARGUMENTS_1("Incorrect number of arguments, expecting 1"),
    NUMBER_OF_ARGUMENTS_2("Incorrect number of arguments, expecting 2"),
    NUMBER_OF_ARGUMENTS_3("Incorrect number of arguments, expecting 3"),
    UNSUPPORTED_METHOD("Unsupported method"),
    VOTING_ALREADY_STARTED("The voting has already been started"),
    VOTING_NOT_STARTED("The voting wasn't started"),
    VOTING_ALREADY_CREATED("The voting has already been created"),
    VOTING_NOT_CREATED("The voting wasn't created"),
    VOTING_ALREADY_ENDED("The voting has already been ended"),
    VOTING_NOT_ENDED("The voting wasn't ended"),
    STATUS_ERROR("Error when changing status"),
    NO_OBJECT_ERROR(":The object wasn't placed on the network"),
    OBJECT_ERROR(":The object has already been placed on the network"),
    CREATE_VOTING_ERROR("Error during creating voting"),
    COMMITTEE_LIMIT("Too many votes"),

    MAPPING_ERROR("Error during object mapping");


    private String text;

    ErrorResponseEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
