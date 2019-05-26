package org.hyperledger.fabric.chaincode;

import com.google.gson.JsonObject;

public class ChaincodeResponse {

    public static String responseError(String errorMessage) {
        return response(errorMessage,false);

    }

    public static String responseSuccess(String successMessage) {
            return response(successMessage,true);
    }

    private static String response(String successMessage, boolean ok) {
        JsonObject obj = new JsonObject();
        obj.addProperty("message", successMessage);
        obj.addProperty("OK", ok);
        return obj.getAsString();
    }


    public static String responseSuccessObject(String object) {
        JsonObject obj = new JsonObject();
        obj.addProperty("message", object);
        return obj.getAsString();
    }
}

