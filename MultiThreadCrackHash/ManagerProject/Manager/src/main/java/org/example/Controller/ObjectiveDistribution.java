package org.example.Controller;

import org.example.Model.Task;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.example.Const.Constants.WORKER_AMOUNT;

@Component
public class ObjectiveDistribution {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz1234567890"; // (36)

    /*
    * Передаю сюда большую таску
    * Теперь я должен в каждую "маленькую" таску запихнуть буквы
    * */
    List<Task> distributeObjectives(Task task){

        char[] alphabet = ALPHABET.toCharArray();
        List<Task> buff = new ArrayList<>();

        for (int i = 0; i < WORKER_AMOUNT; i++) {
            Task a = new Task();
            a.userID = task.userID;
            a.taskID = i;
            a.word = task.word;
            a.status = task.status;
            a.hash = task.hash;
            a.length = task.length;
            a.creationTime = task.creationTime;
            a.firstWord = getFirstWord(alphabet, i);
            a.lastWord = getLastWord(alphabet, i);
            buff.add(a);
        }
        return buff;
    }

    private char getFirstWord(char[] alphabet, int currentTaskCounter){
        int shift = alphabet.length / WORKER_AMOUNT;
        return alphabet[shift * currentTaskCounter];
    }

    private char getLastWord(char[] alphabet, int currentTaskCounter){
        int shift = alphabet.length / WORKER_AMOUNT;
        if (shift * currentTaskCounter + shift >= alphabet.length)
            return alphabet[alphabet.length - 1];
        return alphabet[shift * currentTaskCounter + shift - 1];
    }
}
