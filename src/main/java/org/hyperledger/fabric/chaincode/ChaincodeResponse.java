package org.hyperledger.fabric.chaincode;

import com.fasterxml.jackson.databind.ObjectMapper;

class ChaincodeResponse {
    public String message;
    public String code;
    public boolean OK;

    private ChaincodeResponse(String message, String code, boolean OK) {
        this.message = message;
        this.code = code;
        this.OK = OK;
    }

    static String responseError(String errorMessage) {
        try {
            return (new ObjectMapper()).writeValueAsString(new ChaincodeResponse(errorMessage, "", false));
        } catch (Throwable e) {
            return "{\"code\":'" + "" + "', \"message\":'" + e.getMessage() + " AND " + errorMessage + "', \"OK\":" + false + "}";
        }
    }

    static String responseSuccess(String successMessage) {
        try {
            return (new ObjectMapper()).writeValueAsString(new ChaincodeResponse(successMessage, "", true));
        } catch (Throwable e) {
            return "{\"message\":'" + e.getMessage() + " BUT " + successMessage + " (NO COMMIT)', \"OK\":" + false + "}";
        }
    }

    static String responseSuccessObject(String object) {
        return "{\"message\":" + object + ", \"OK\":" + true + "}";
    }
}



