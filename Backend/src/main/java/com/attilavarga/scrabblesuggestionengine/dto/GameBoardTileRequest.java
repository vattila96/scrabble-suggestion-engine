package com.attilavarga.scrabblesuggestionengine.dto;

import com.attilavarga.scrabblesuggestionengine.game.Tile;
import lombok.Data;

@Data
public class GameBoardTileRequest {
    private Tile tile;
    private int x;
    private int y;
}
