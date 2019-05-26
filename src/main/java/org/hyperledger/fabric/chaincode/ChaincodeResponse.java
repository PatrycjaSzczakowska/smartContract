package org.hyperledger.fabric.chaincode;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChaincodeResponse {
    String message;
    String status;

    public ChaincodeResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public static String responseError(String errorMessage) {
        try {
            return (new ObjectMapper()).writeValueAsString(new ChaincodeResponse(errorMessage,  "ERROR"));
        } catch (Throwable e) {
            return "{\"message\":'" + e.getMessage() + " AND " + errorMessage + "', \"OK\":" + false + "}";
        }
    }

    public static String responseSuccess(String successMessage) {
        try {
            return (new ObjectMapper()).writeValueAsString(new ChaincodeResponse(successMessage, "OK"));
        } catch (Throwable e) {
            return "{\"message\":'" + e.getMessage() + " BUT " + successMessage + " (NO COMMIT)', \"OK\":" + false + "}";
        }
    }

    public static String responseSuccessObject(String object) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"message\":");
        stringBuilder.append(object);
        return stringBuilder.toString();
    }
}

