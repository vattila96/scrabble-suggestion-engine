package com.attilavarga.scrabblesuggestionengine.rest;

import com.attilavarga.scrabblesuggestionengine.game.IllegalMoveException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GameExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalMoveException.class)
    public String handleIllegalMoveException(IllegalMoveException exception) {
        return "Illegal move: " + exception.getMessage();
    }
}
