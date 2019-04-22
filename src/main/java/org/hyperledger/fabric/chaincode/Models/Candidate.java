package org.hyperledger.fabric.chaincode.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Candidate {
    private String candidateId;
    private String name;
}