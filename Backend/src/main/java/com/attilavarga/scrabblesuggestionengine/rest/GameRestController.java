package com.attilavarga.scrabblesuggestionengine.rest;

import com.attilavarga.scrabblesuggestionengine.dto.*;
import com.attilavarga.scrabblesuggestionengine.game.IllegalMoveException;
import com.attilavarga.scrabblesuggestionengine.game.Tile;
import com.attilavarga.scrabblesuggestionengine.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("game")
@RequiredArgsConstructor
public class GameRestController {

    private final GameService gameService;

    @PostMapping("/gameboard-tiles")
    public ResponseEntity<Void> addTileToGameBoard(@RequestBody GameBoardTileRequest request) throws IllegalMoveException {
        gameService.addTileToGameBoard(request.getTile(), request.getX(), request.getY());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/gameboard-tiles")
    public ResponseEntity<Void> deleteTileFromGameBoard(@RequestBody GameBoardTileRequest request) throws IllegalMoveException {
        gameService.deleteTileFromGameBoard(request.getTile(), request.getX(), request.getY());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/player-tiles")
    public ResponseEntity<Void> addTileToPlayerTiles(@RequestBody PlayerTileRequest request) throws IllegalMoveException {
        gameService.addTileToPlayerTiles(request.getTile(), request.getIndex());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/player-tiles")
    public ResponseEntity<Void> deleteTileFromPlayerTiles(@RequestBody PlayerTileRequest request) throws IllegalMoveException {
        gameService.deleteTileFromPlayerTiles(request.getTile(), request.getIndex());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/player-tiles/randomization")
    public List<Tile> randomizePlayerTiles() throws IllegalMoveException {
        gameService.randomizePlayerTiles();

        return new ArrayList<>(Arrays.asList(gameService.getPlayerTiles()));
    }

    @GetMapping("/recommendation")
    public List<GameService.Word> recommendWords() throws IllegalMoveException { return new ArrayList<>(Arrays.asList(gameService.recommendWords())); }

    @PostMapping("/reset")
    public ResponseEntity<Void> reset() {
        gameService.reset();

        return ResponseEntity.ok().build();
    }
}