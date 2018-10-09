package com.gamesbykevin.bingbot.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.gamesbykevin.bingbot.util.LogFile.displayMessage;

public class Words {

    //list of words
    private static List<String> WORDS;

    public static void recycle() {

        if (WORDS != null)
            WORDS.clear();

        WORDS = null;
    }

    public static void load() {

        if (WORDS == null) {

            try {

                //instantiate new list
                WORDS = new ArrayList();

                //create our input stream and load our file to the array list
                InputStream is = Words.class.getClassLoader().getResourceAsStream("words.txt");
                InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(streamReader);
                for (String line; (line = reader.readLine()) != null; ) {

                    //only add valid words
                    if (line != null && line.trim().length() > 1)
                        WORDS.add(line);
                }

            } catch (Exception e) {
                WORDS = null;
                displayMessage(e);
            }
        }
    }

    public static String getRandomWord() {

        //load our list of words
        load();

        //pick random word
        int index = (int)(WORDS.size() * Math.random());

        //get the word
        String word = WORDS.get(index);

        //make sure we don't pick the word again
        WORDS.remove(index);

        //return our result
        return word;
    }
}