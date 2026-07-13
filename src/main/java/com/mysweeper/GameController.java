package com.mysweeper;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService games;

    public GameController(GameService games) { this.games = games; }

    @PostMapping
    public GameView create(@RequestBody(required = false) NewGameRequest request) {
        return games.create(request == null ? new NewGameRequest(null, null, null) : request);
    }

    @GetMapping("/{id}")
    public GameView get(@PathVariable UUID id) { return games.get(id); }

    @PostMapping("/{id}/reveal")
    public GameView reveal(@PathVariable UUID id, @RequestParam int row, @RequestParam int column) { return games.reveal(id, row, column); }

    @PostMapping("/{id}/flag")
    public GameView flag(@PathVariable UUID id, @RequestParam int row, @RequestParam int column) { return games.toggleFlag(id, row, column); }

    @PostMapping("/{id}/chord")
    public GameView chord(@PathVariable UUID id, @RequestParam int row, @RequestParam int column) { return games.chord(id, row, column); }
}
