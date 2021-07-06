package com.attilavarga.scrabblesuggestionengine.game;

import lombok.*;
import org.springframework.util.ResourceUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
public class Tile {

    public static Map<String, Integer> tileScores = new HashMap<>();

    static {
        File file = null;
        try { file = ResourceUtils.getFile("classpath:tilescores"); }
        catch (FileNotFoundException e) { e.printStackTrace(); }

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1))) {
            String line;

            while ((line = fileReader.readLine()) != null) {
                String[] pairOfLetterAndScore = line.split(":");
                tileScores.put(pairOfLetterAndScore[0], Integer.parseInt(pairOfLetterAndScore[1]));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Getter
    private final String letter;

    @Getter
    private final int score;
}