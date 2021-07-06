package com.attilavarga.scrabblesuggestionengine.game;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class GameState {

    @Getter
    private final Tile[][] gameBoard;
    @Getter
    private Map<String, Integer> tileQuantities;
    private final Map<String, Integer> originalTileQuantities;

    @Getter
    private final Tile[] playerTiles;

    public GameState() {
        gameBoard = new Tile[15][15];
        tileQuantities = readTileQuantitiesFromFile("tilequantities");
        originalTileQuantities = new HashMap<>(tileQuantities);
        playerTiles = new Tile[7];
    }

    private Map<String, Integer> readTileQuantitiesFromFile(String fileName) {
        Map<String, Integer> tileQuantities = new HashMap<>();

        File file = null;
        try { file = ResourceUtils.getFile("classpath:" + fileName); }
        catch (FileNotFoundException e) { e.printStackTrace(); }

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1))) {
            String line;

            while ((line = fileReader.readLine()) != null) {
                String[] pairOfLetterAndQuantity = line.split(":");
                tileQuantities.put(pairOfLetterAndQuantity[0], Integer.parseInt(pairOfLetterAndQuantity[1]));
            }
        } catch (IOException e) { e.printStackTrace(); }

        return tileQuantities;
    }

    public void addTileToGameBoard(Tile tile, int x, int y) throws IllegalMoveException {
        if (tileQuantities.get(tile.getLetter()) <= 0 || gameBoard[x][y] != null || tile.getLetter().equals("joker")) throw new IllegalMoveException("Can't add tile to gameboard.");

        setGameBoardElement(tile, x, y);
        decreaseTileQuantity(tile.getLetter());
    }

    public void deleteTileFromGameBoard(Tile tile, int x, int y) throws IllegalMoveException {
        if (gameBoard[x][y] == null) throw new IllegalMoveException("Can't delete tile from gameboard.");

        setGameBoardElement(null, x, y);
        increaseTileQuantity(tile.getLetter());
    }

    private void setGameBoardElement(Tile tile, int x, int y) { gameBoard[x][y] = tile; }

    private void increaseTileQuantity(String letter) { tileQuantities.put(letter, tileQuantities.get(letter) + 1); }

    private void decreaseTileQuantity(String letter) { tileQuantities.put(letter, tileQuantities.get(letter) - 1); }

    public void addTileToPlayerTiles(Tile tile, int index) throws IllegalMoveException {
        if (tileQuantities.get(tile.getLetter()) <= 0 || playerTiles[index] != null) throw new IllegalMoveException("Can't add tile to player tiles.");

        setPlayerTilesElement(tile, index);
        decreaseTileQuantity(tile.getLetter());
    }

    public void deleteTileFromPlayerTiles(Tile tile, int index) throws IllegalMoveException {
        if (playerTiles[index] == null) throw new IllegalMoveException("Can't delete tile from player tiles.");

        setPlayerTilesElement(null, index);
        increaseTileQuantity(tile.getLetter());
    }

    private void setPlayerTilesElement(Tile tile, int index) { playerTiles[index] = tile; }

    public void randomizePlayerTiles() throws IllegalMoveException {
        resetPlayerTiles();
        List<String> tileQuantitiesKeys = new ArrayList<>(tileQuantities.keySet());
        Random r = new Random();
        int sumOfQuantities = tileQuantities.values().stream().reduce(0, Integer::sum);

        if (sumOfQuantities < 7) throw new IllegalMoveException("Not enough tile to randomize.");

        for (int i = 0; i < 7; ++i) {
            String randomKey = tileQuantitiesKeys.get(r.nextInt(tileQuantitiesKeys.size()));

            while (tileQuantities.get(randomKey) <= 0) { randomKey = tileQuantitiesKeys.get(r.nextInt(tileQuantitiesKeys.size())); }

            addTileToPlayerTiles(new Tile(randomKey, Tile.tileScores.get(randomKey)), i);
        }
    }

    public void reset() {
        resetGameBoard();
        resetPlayerTiles();
        resetTileQuantities();
    }

    private void resetGameBoard() {
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (gameBoard[i][j] != null) {
                    increaseTileQuantity(gameBoard[i][j].getLetter());
                    gameBoard[i][j] = null;
                }
            }
        }
    }

    private void resetPlayerTiles() {
        for (int i = 0; i < 7; ++i) {
            if (playerTiles[i] != null) {
                increaseTileQuantity(playerTiles[i].getLetter());
                playerTiles[i] = null;
            }
        }
    }

    private void resetTileQuantities() { tileQuantities = new HashMap<>(originalTileQuantities); }

    public boolean isGameBoardEmpty() {
        int sum = 0;

        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (gameBoard[i][j] != null) ++sum;
            }
        }

        return sum == 0;
    }

    public boolean isPlayerTilesEmpty() {
        int sum = 0;

        for (int i = 0; i < 7; ++i) {
            if (playerTiles[i] != null) sum++;
        }

        return sum == 0;
    }
}