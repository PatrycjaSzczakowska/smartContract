package org.hyperledger.fabric.chaincode;

import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;

class ChaincodeResponse {
    static byte[] responseError(String errorMessage) {
        return response(errorMessage,false);

    }

    static byte[] responseSuccess(String successMessage) {
        return response(successMessage,true);
    }

    private static byte[] response(String message, boolean isOk) {
        JsonObject response = new JsonObject();
        response.addProperty("message", message);
        response.addProperty("OK", isOk);
        return response.toString().getBytes(StandardCharsets.UTF_8);
    }

}



