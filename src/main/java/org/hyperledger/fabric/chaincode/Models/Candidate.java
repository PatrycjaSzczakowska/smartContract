package org.hyperledger.fabric.chaincode.Models;

public class Candidate {
    public String candidateId;
    public String firstName;
    public String lastName;
    public String party;
    public int partyNo; //number on the list
    public int listNo; //number of the list
    public int age;

    public Candidate() {
    }

    public Candidate(String candidateId, String firstName, String lastName, String party, int partyNo, int listNo, int age) {
        this.candidateId = candidateId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.party = party;
        this.partyNo = partyNo;
        this.listNo = listNo;
        this.age = age;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public int getPartyNo() {
        return partyNo;
    }

    public void setPartyNo(int partyNo) {
        this.partyNo = partyNo;
    }

    public int getListNo() {
        return listNo;
    }

    public void setListNo(int listNo) {
        this.listNo = listNo;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}