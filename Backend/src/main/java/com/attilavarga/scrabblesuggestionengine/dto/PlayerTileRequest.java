package com.attilavarga.scrabblesuggestionengine.dto;

import com.attilavarga.scrabblesuggestionengine.game.Tile;
import lombok.Data;

@Data
public class PlayerTileRequest {
    private Tile tile;
    private int index;
}
