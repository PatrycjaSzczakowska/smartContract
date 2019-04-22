package org.hyperledger.fabric.chaincode;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChaincodeResponse {
    public String message;
    public String code;
    public boolean OK;

    public static String responseError(String errorMessage, String code) {
        try {
            return (new ObjectMapper()).writeValueAsString(new ChaincodeResponse(errorMessage, code, false));
        } catch (Throwable e) {
            return "{\"code\":'" + code + "', \"message\":'" + e.getMessage() + " AND " + errorMessage + "', \"OK\":" + false + "}";
        }
    }

    public static String responseSuccess(String successMessage) {
        try {
            return (new ObjectMapper()).writeValueAsString(new ChaincodeResponse(successMessage, "", true));
        } catch (Throwable e) {
            return "{\"message\":'" + e.getMessage() + " BUT " + successMessage + " (NO COMMIT)', \"OK\":" + false + "}";
        }
    }

    public static String responseSuccessObject(String object) {
        return "{\"message\":" + object + ", \"OK\":" + true + "}";
    }


}

