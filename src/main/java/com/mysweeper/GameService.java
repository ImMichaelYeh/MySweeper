package com.mysweeper;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GameService {
    private final Map<UUID, MinesweeperGame> games = new ConcurrentHashMap<>();

    public GameView create(NewGameRequest request) {
        int width = request.width() == null ? 30 : request.width();
        int height = request.height() == null ? 16 : request.height();
        int mines = request.mines() == null ? 99 : request.mines();
        try {
            MinesweeperGame game = new MinesweeperGame(width, height, mines);
            GameView view = game.view();
            games.put(view.id(), game);
            return view;
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    public GameView get(UUID id) { return game(id).view(); }
    public GameView reveal(UUID id, int row, int column) { return update(id, game -> game.reveal(row, column)); }
    public GameView toggleFlag(UUID id, int row, int column) { return update(id, game -> game.toggleFlag(row, column)); }
    public GameView chord(UUID id, int row, int column) { return update(id, game -> game.chord(row, column)); }

    private GameView update(UUID id, GameAction action) {
        try {
            MinesweeperGame game = game(id);
            action.apply(game);
            return game.view();
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    private MinesweeperGame game(UUID id) {
        MinesweeperGame game = games.get(id);
        if (game == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found.");
        return game;
    }

    @FunctionalInterface
    private interface GameAction { void apply(MinesweeperGame game); }
}
