package org.example.Handlers;

import org.paukov.combinatorics3.Generator;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ObjectiveHandler {
    private static final String TEST_ALPHABET1 = "abcdefghijklmnopqrstuvwxyz1234567890";

    private static String calculateMD5Hash(String permutation) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(permutation.getBytes());

            // Преобразование массива байт в шестнадцатеричное представление
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashText = new StringBuilder(no.toString(16));
            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }

            return hashText.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String doTask(final int maxWordLen, final String hash, final List<Character> symbols) {
        for (int i = 1; i <= maxWordLen; i++) {

            Optional<String> matchingWord = Generator.permutation(symbols)
                    .withRepetitions(i)
                    .stream()
                    .map(permutation -> permutation.toString().replaceAll("[\\[\\],\\s+]", ""))
                    .filter(word -> Objects.requireNonNull(calculateMD5Hash(word)).equals(hash))
                    .findFirst();

            if (matchingWord.isPresent()) {
                return matchingWord.get();
            }
        }
        return "non";
    }

    private static List<Character> alphabet2List(){
        List<Character> list = new ArrayList<>();
        for (int i = 0; i < TEST_ALPHABET1.length(); i++){
            list.add(TEST_ALPHABET1.charAt(i));
        }
        return list;
    }

    public static String convertHash2Word(String hash, int maxWordLen) {
        List<Character> characters = alphabet2List();
        String buff = doTask(maxWordLen, hash, characters);
        if (buff.equals("non")){
            System.out.println("The word wasn't found.");
            return "non";
        }
        return buff;
    }

    private void doTest1(){
        String word = "abcd";
        String md5Hash = calculateMD5Hash(word);
        System.out.println("MD5 Hash of '" + word + "': " + md5Hash);
    }

    private void doTest2(){
        List<Character> characters = alphabet2List();  // using TEST_ALPHABET2
        String buff = doTask(4, "e2fc714c4727ee9395f324cd2e7f331f", characters); // abcd
        if (buff.equals("non")){
            System.out.println("The word wasn't found.");
        }
        System.out.println(buff);
    }
}
