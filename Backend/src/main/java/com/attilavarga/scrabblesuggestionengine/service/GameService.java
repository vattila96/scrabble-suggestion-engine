package com.attilavarga.scrabblesuggestionengine.service;

import com.attilavarga.scrabblesuggestionengine.dao.WordDAO;
import com.attilavarga.scrabblesuggestionengine.game.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameState gameState;
    private final WordDAO wordDAO;

    public void addTileToGameBoard(Tile tile, int x, int y) throws IllegalMoveException {
        if (tile.getLetter().equals("ű")) {
            gameState.addTileToGameBoard(new Tile("û", tile.getScore()), x, y);
        } else if (tile.getLetter().equals("ő")) {
            gameState.addTileToGameBoard(new Tile("õ", tile.getScore()), x, y);
        } else {
            gameState.addTileToGameBoard(tile, x, y);
        }
    }

    public void deleteTileFromGameBoard(Tile tile, int x, int y) throws IllegalMoveException {
        if (tile.getLetter().equals("ű")) {
            gameState.deleteTileFromGameBoard(new Tile("û", tile.getScore()), x, y);
        } else if (tile.getLetter().equals("ő")) {
            gameState.deleteTileFromGameBoard(new Tile("õ", tile.getScore()), x, y);
        } else {
            gameState.deleteTileFromGameBoard(tile, x, y);
        }
    }

    public void addTileToPlayerTiles(Tile tile, int index) throws IllegalMoveException {
        if (tile.getLetter().equals("ű")) {
            gameState.addTileToPlayerTiles(new Tile("û", tile.getScore()), index);
        } else if (tile.getLetter().equals("ő")) {
            gameState.addTileToPlayerTiles(new Tile("õ", tile.getScore()), index);
        } else {
            gameState.addTileToPlayerTiles(tile, index);
        }
    }

    public void deleteTileFromPlayerTiles(Tile tile, int index) throws IllegalMoveException {
        if (tile.getLetter().equals("ű")) {
            gameState.deleteTileFromPlayerTiles(new Tile("û", tile.getScore()), index);
        } else if (tile.getLetter().equals("ő")) {
            gameState.deleteTileFromPlayerTiles(new Tile("õ", tile.getScore()), index);
        } else {
            gameState.deleteTileFromPlayerTiles(tile, index);
        }
    }

    public void randomizePlayerTiles() throws IllegalMoveException { gameState.randomizePlayerTiles(); }

    public void reset() { gameState.reset(); }


    public Word[] recommendWords() throws IllegalMoveException {
        if (gameState.isGameBoardEmpty() || gameState.isPlayerTilesEmpty()) throw new IllegalMoveException("Can't start game : empty gameboard or empty playertiles");

        List<Word> playableWords = new ArrayList<>();

        for (String word : wordDAO.getWords()) {
            if (isWordFound(word)) playableWords.add(new Word(word, calculateScoreOfWord(word)));
        }

        playableWords.sort(Comparator.comparingInt(Word::getScore));
        Collections.reverse(playableWords);

        return playableWords.size() >= 5 ? playableWords.stream().limit(5).collect(Collectors.toList()).toArray(new Word[5]) : playableWords.stream().limit(playableWords.size()).collect(Collectors.toList()).toArray(new Word[playableWords.size()]);
    }

    private boolean isWordFound(String word) {
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                Tile currentTile = gameState.getGameBoard()[i][j];

                if (currentTile != null && word.contains(currentTile.getLetter().toLowerCase()) && (canConstructWord(gameState.getGameBoard()[i], word) || canConstructWord(getColumn(gameState.getGameBoard(), j), word))) return true;
            }
        }

        return false;
    }

    private boolean canConstructWord(Tile[] tilesInRow, String word) {

        for (int i = 0; i < 15 - (word.length() - 1); ++i) {
            if (i > 0 && tilesInRow[i-1] != null) continue;
            if (i + word.length() < 15 && tilesInRow[i + word.length()] != null) continue;
            if (i + (word.length() - 1) > 14) break;

            Tile[] tmpPlayerTiles = gameState.getPlayerTiles().clone();
            StringBuilder tmpWord = new StringBuilder();
            List<String> usedJokers = new LinkedList<>();
            boolean wordContainsPlayerTile = false;
            boolean wordContainsGameBoardTile = false;
            int index = 0;

            for (int j = i; j < i + word.length(); ++j) {
                if (index >= word.length()) break;

                boolean foundAppropriateTile = false;

                if (tilesInRow[j] == null) {

                    // Check if player has an appropriate double letter tile.
                    if (isDoubleLetter(word, index)) {
                        for (int k = 0; k < tmpPlayerTiles.length; ++k) {
                            if (tmpPlayerTiles[k] != null && tmpPlayerTiles[k].getLetter().equals(Character.toString(word.charAt(index)) + word.charAt(index+1))) {
                                tmpWord.append(tmpPlayerTiles[k].getLetter());
                                tmpPlayerTiles[k] = null;
                                index = index + 2;
                                foundAppropriateTile = true;
                                wordContainsPlayerTile = true;
                                break;
                            }
                        }
                    }

                    // Check if player has a joker to use as a double letter tile.
                    if (!foundAppropriateTile && isDoubleLetter(word, index)) {
                        for (int k = 0; k < tmpPlayerTiles.length; ++k) {
                            if (tmpPlayerTiles[k] != null && tmpPlayerTiles[k].getLetter().equals("joker")) {
                                tmpWord.append(word.charAt(index)).append(word.charAt(index + 1));
                                usedJokers.add(Character.toString(word.charAt(index)) + word.charAt(index + 1));
                                tmpPlayerTiles[k] = null;
                                index = index + 2;
                                foundAppropriateTile = true;
                                wordContainsPlayerTile = true;
                                break;
                            }
                        }
                    }

                    // Check if player has an appropriate single letter tile.
                    if (!foundAppropriateTile) {
                        for (int k = 0; k < tmpPlayerTiles.length; ++k) {
                            if (tmpPlayerTiles[k] != null && tmpPlayerTiles[k].getLetter().equals(Character.toString(word.charAt(index)))) {
                                tmpWord.append(tmpPlayerTiles[k].getLetter());
                                tmpPlayerTiles[k] = null;
                                ++index;
                                foundAppropriateTile = true;
                                wordContainsPlayerTile = true;
                                break;
                            }
                        }
                    }

                    // Check if player has a joker to use as a single letter tile.
                    if (!foundAppropriateTile) {
                        for (int k = 0; k < tmpPlayerTiles.length; ++k) {
                            if (tmpPlayerTiles[k] != null && tmpPlayerTiles[k].getLetter().equals("joker")) {
                                tmpWord.append(word.charAt(index));
                                usedJokers.add(Character.toString(word.charAt(index)));
                                tmpPlayerTiles[k] = null;
                                ++index;
                                foundAppropriateTile = true;
                                wordContainsPlayerTile = true;
                                break;
                            }
                        }
                    }

                    if (!foundAppropriateTile) { break; }
                } else {
                    if (tilesInRow[j].getLetter().length() == 2) {
                        ++index;
                    }

                    tmpWord.append(tilesInRow[j].getLetter());
                    wordContainsGameBoardTile = true;
                    ++index;
                }
            }

            if (wordContainsGameBoardTile && wordContainsPlayerTile && tmpWord.length() > 1 && tmpWord.length() == word.length() && tmpWord.toString().equals(word)) {
                Word.usedJokers = usedJokers;

                return true;
            }
        }

        return false;
    }

    private int calculateScoreOfWord(String word) {
        int scoreSum = 0;

        for (int i = 0; i < word.length(); ++i) {
            if (isDoubleLetter(word, i)) {
                String currentLetter = Character.toString(word.charAt(i)) + word.charAt(i+1);

                if (Word.usedJokers.contains(currentLetter)) {
                    Word.usedJokers.remove(currentLetter);
                } else {
                    scoreSum += Tile.tileScores.get(currentLetter);
                }

                ++i;
            } else {
                String currentLetter = Character.toString(word.charAt(i));

                if (Word.usedJokers.contains(currentLetter)) {
                    Word.usedJokers.remove(currentLetter);
                } else {
                    scoreSum += Tile.tileScores.get(currentLetter);
                }
            }
        }

        return scoreSum;
    }

    private boolean isDoubleLetter(String word, int index) {
        return ((index < word.length() - 1) && ((word.charAt(index) == 's' && word.charAt(index+1) == 'z') || (word.charAt(index) == 'c' && word.charAt(index+1) == 's') || (word.charAt(index) == 'z' && word.charAt(index+1) == 's') || (word.charAt(index) == 'g' && word.charAt(index+1) == 'y') || (word.charAt(index) == 'n' && word.charAt(index+1) == 'y') || (word.charAt(index) == 'l' && word.charAt(index+1) == 'y') || (word.charAt(index) == 't' && word.charAt(index+1) == 'y')));
    }

    private static Tile[] getColumn(Tile[][] array, int index){
        Tile[] column = new Tile[15];

        for (int i = 0; i < column.length; ++i) { column[i] = array[i][index]; }

        return column;
    }

    @RequiredArgsConstructor
    public static class Word {

        @Getter
        private final String word;

        @Getter
        private final int score;

        public static List<String> usedJokers;
    }

    public Tile[] getPlayerTiles() {
        Tile[] tmpPlayerTiles = gameState.getPlayerTiles().clone();

        for (int i = 0; i < tmpPlayerTiles.length; ++i) {
            if (tmpPlayerTiles[i].getLetter().equals("õ")) {
                tmpPlayerTiles[i] = new Tile("ő", tmpPlayerTiles[i].getScore());
            } else if (tmpPlayerTiles[i].getLetter().equals("û")) {
                tmpPlayerTiles[i] = new Tile("ű", tmpPlayerTiles[i].getScore());
            }
        }

        return tmpPlayerTiles;
    }
}