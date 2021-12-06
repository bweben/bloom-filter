package ch.fhnw.dist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) throws IOException {
        double pErrorProbability;
        try {
            pErrorProbability = Double.parseDouble(args[0]);
        } catch (Exception e) {
            throw new RuntimeException("First program argument must be expected error probability (as floating point value).");
        }

        new Application().run(pErrorProbability);
    }

    private void run(double pErrorProbability) throws IOException {
        List<String> existingWordList = Files.readAllLines(new File(getClass().getResource("/words.txt").getFile()).toPath());
        List<String> notExistingWordList  = existingWordList.stream().map(this::rotate2).collect(Collectors.toList());
        notExistingWordList.removeAll(existingWordList); //make sure that this list only contains non-existing words.

        BloomFilter bloomFilter = new BloomFilter(existingWordList.size(), pErrorProbability);
        bloomFilter.addAll(existingWordList);

        for(String existingWord : existingWordList) {
            if(!bloomFilter.contains(existingWord)) {
                throw new IllegalStateException("An existing word SHALL NEVER be detected as non-existing!");
            }
        }

        int numOfFalsePositives = 0;
        for(String nonExistingWord : notExistingWordList) {
            if(bloomFilter.contains(nonExistingWord)) {
                numOfFalsePositives++;
            }
        }

        System.out.format("----------------- PARAMETERS ------------------%n");
        System.out.format("Number of elements in filter:              %d %n", bloomFilter.getNumberOfElements());
        System.out.format("Calibrated probability of false positives: %f %n", bloomFilter.getAcceptedErrorProbability());
        System.out.format("Amount of hash functions:                  %d %n", bloomFilter.getHashFunctionSize());
        System.out.format("Filter size:                               %d %n", bloomFilter.getFilterSize());
        System.out.format("-----------------------------------------------%n%n");
        System.out.format("------------------ RESULTS ------------------- %n");
        System.out.format("Number of false positives:                 %d %n", numOfFalsePositives);
        System.out.format("Actual probability of false positives:     %f %n", (float)numOfFalsePositives / bloomFilter.getNumberOfElements());
    }

    /**
     * Applies a caesar cipher with offset 2 to the input.
     * @param s The input string
     * @return the "encrypted" string
     */
    private String rotate2(String s) {
        char[] org = s.toCharArray();
        char[] result = new char[s.length()];
        for(int i = 0; i < result.length; i++) {
            int orgPosition = org[i] - 'a';
            int newPosition = (orgPosition + 2) % 26;
            result[i] = (char)('a' + newPosition);
        }
        return new String(result);
    }

}
