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
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz1234567890";
    private final static List<Character> alphabet = alphabet2List();


    public static String convertHash2Word(String hash, int maxWordLen, char firstWord, char lastWord) {
        /*
        * 1. Определить то множество букв, которые предназначены для данного воркера
        * 2. Создаю цикл. В этом цикле прохожусь сначала по всем уровням, а потом по всем буквам воркеров и отправляю их в doTask().
        * 3. doTask() возвращает либо слово, либо non.
        * */

        List<Character> buff = getSpecifiedLetters(firstWord, lastWord);
        String searchedWord = null;
        for (int i = 0; i < maxWordLen; i++){

            for (Character character : buff) {
                searchedWord = doTask(i, hash, character);

                if (!searchedWord.equals("non")){
                    return searchedWord;
                }
            }

//            System.out.println("i = " + i + " searchedWord: " + searchedWord);
        }
        return "non";
    }

    private static List<Character> getSpecifiedLetters(Character firstWord, Character lastWord) {
        return alphabet.subList(alphabet.indexOf(firstWord), alphabet.indexOf(lastWord) + 1);
    }

    private static String doTask(final int index, final String hash, final Character curLetter) {

        Optional<String> matchingWord = Generator.permutation(alphabet)
                .withRepetitions(index)
                .stream()
                .map(permutation -> permutation.toString().replaceAll("[\\[\\],\\s+]", ""))
                .filter(word -> Objects.requireNonNull(calculateMD5Hash(curLetter + word)).equals(hash))
                .findFirst();

        if (matchingWord.isPresent()) {
            return curLetter + matchingWord.get();
        }

        return "non";
    }

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
    
    private static List<Character> alphabet2List(){
        List<Character> list = new ArrayList<>();
        for (int i = 0; i < ALPHABET.length(); i++){
            list.add(ALPHABET.charAt(i));
        }
        return list;
    }

    private static void doConvertHash2WordTest(){
        String word = convertHash2Word(calculateMD5Hash("abcd"), 4, 'a', 'r');
        System.out.println("The word is " + word);
    }
    
    private void doTest1(){
        String word = "abcd";
        String md5Hash = calculateMD5Hash(word);
        System.out.println("MD5 Hash of '" + word + "': " + md5Hash);
    }

    private void doTest2(){
//        List<Character> characters = alphabet2List();  // using TEST_ALPHABET2
//        String buff = doTask(4, "e2fc714c4727ee9395f324cd2e7f331f", characters); // abcd
//        if (buff.equals("non")){
//            System.out.println("The word wasn't found.");
//        }
//        System.out.println(buff);
    }


}
