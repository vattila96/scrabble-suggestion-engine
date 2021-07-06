package com.attilavarga.scrabblesuggestionengine.dao;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Repository
public class WordDAO {

    @Getter
    private final String[] words;

    public WordDAO() { words = readWordsFromFile("freedict"); }

    private String[] readWordsFromFile(String fileName) {
        List<String> words = new LinkedList<>();

        File file = null;
        try { file = ResourceUtils.getFile("classpath:" + fileName); }
        catch (FileNotFoundException e) { e.printStackTrace(); }

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1))) {
            String word;

            while ((word = fileReader.readLine()) != null) {
                if (!(word.contains("q") || word.contains("w") || word.contains("x") || word.contains("-") || word.matches(".*[^gnlt]y.*") || word.matches("y.*"))) {
                    words.add(word.toLowerCase());
                }
            }
        } catch (IOException e) { e.printStackTrace(); }

        return words.toArray(new String[]{});
    }
}