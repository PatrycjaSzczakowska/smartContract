package org.hyperledger.fabric.chaincode;

import com.google.gson.JsonObject;

class ChaincodeResponse {
    static String responseError(String errorMessage) {
        return response(errorMessage,false);

    }

    static String responseSuccess(String successMessage) {
        return response(successMessage,true);
    }

    private static String response(String message, boolean isOk) {
        JsonObject response = new JsonObject();
        response.addProperty("message", message);
        response.addProperty("OK", isOk);
        return response.toString().replaceAll("\\\\","");
    }

}



