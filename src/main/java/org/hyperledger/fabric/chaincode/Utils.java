package org.hyperledger.fabric.chaincode;

public class Utils {
    public static boolean checkString(String str) {
        return (str == null || str.trim().length() <= 0);
    }
}
