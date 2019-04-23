package org.hyperledger.fabric.chaincode.Models;

public class Siema {
    private String name;
    private String siemaId;

    public Siema() {

    }

    public Siema(String name, String siemaId) {
        this.name = name;
        this.siemaId = siemaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSiemaId() {
        return siemaId;
    }

    public void setSiemaId(String siemaId) {
        this.siemaId = siemaId;
    }
}
