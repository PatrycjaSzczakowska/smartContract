package org.hyperledger.fabric.chaincode;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChaincodeResponse {
    public String message;
    public String code;
    public boolean OK;

    public ChaincodeResponse(String message, String code, boolean OK) {
        this.message = message;
        this.code = code;
        this.OK = OK;
    }

    public static String responseError(String errorMessage, String code) {
        try {
            return (new ObjectMapper()).writeValueAsString(new ChaincodeResponse(errorMessage, code, false));
        } catch (Throwable e) {
            return "{\"code\":'" + code + "', \"message\":'" + e.getMessage() + " AND " + errorMessage + "', \"OK\":" + false + "}";
        }
    }

    public static String responseSuccess(String successMessage) {
        try {
            return (new ObjectMapper()).writeValueAsString(new ChaincodeResponse(successMessage, "", true)).replace("\\", "");
        } catch (Throwable e) {
            return "{\"message\":'" + e.getMessage() + " BUT " + successMessage + " (NO COMMIT)', \"OK\":" + false + "}";
        }
    }

    public static String responseSuccessObject(String object) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"message\":");
        stringBuilder.append(object);
        return stringBuilder.toString().replace("\\", "");
    }


}

