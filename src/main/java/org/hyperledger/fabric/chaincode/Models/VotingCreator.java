package org.hyperledger.fabric.chaincode.Models;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class VotingCreator {
    //List<> candidates, List <Voter> voters
    public static Voting createVoting() {
        File directory = new File("../data");
        if (! directory.exists()){
            directory.mkdir();
        }
        List<Candidate> candidates = createCandidates();
        List<Committee> committees = createCommittees();

        return new Voting(candidates, committees);
    }

    private static List<Candidate> createCandidates() {
        List<Candidate> candidates = new ArrayList<>();






        try (FileWriter writer = new FileWriter("../data/candidates.txt");
             BufferedWriter bw = new BufferedWriter(writer)) {

            String content = "";

            content += "C1, Tomasz, Jasiak, zieloni, 1, 1, 35\n";
            content += "C2, Tomasz2, Jasiak2, czerwoni, 2, 1, 35\n";
            content += "C3, Tomasz3, Jasiak3, czerwoni, 2, 1, 35\n";
            content += "C4, Tomasz4, Jasiak4, zieloni, 1, 1, 35\n";
            content += "C5, Tomasz5, Jasiak5, czerwoni, 2, 1, 35\n";
            content += "C6, Tomasz6, Jasiak6, zieloni, 1, 1, 35\n";




            bw.write(content);

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }







        try {
            BufferedReader reader = new BufferedReader(new FileReader("../data/candidates.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(", ");
                int v4 = Integer.parseInt(values[4], 10);
                int v5 = Integer.parseInt(values[5], 10);
                int v6 = Integer.parseInt(values[6], 10);

                candidates.add(new Candidate(values[0], values[1], values[2], values[3], v4, v5, v6));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return candidates;
    }

    private static List<Committee> createCommittees() {
        List<Committee> committees = new ArrayList<>();

        try (FileWriter writer = new FileWriter("../data/committees.txt");
             BufferedWriter bw = new BufferedWriter(writer)) {

            String content2 = "";

            content2 += "COM1, 20\n";
            content2 += "COM2, 20\n";




            bw.write(content2);

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }





        try {
            BufferedReader reader = new BufferedReader(new FileReader("../data/committees.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(", ");
                int v1 = Integer.parseInt(values[1], 10);

                committees.add(new Committee(values[0], v1));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }






        return committees;
    }

}
