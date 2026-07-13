package com.mysweeper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class MinesweeperGameTest {
    @Test
    void firstClickStartsGameAndIsSafe() {
        MinesweeperGame game = new MinesweeperGame(9, 9, 10);

        game.reveal(4, 4);

        GameView view = game.view();
        assertNotEquals(GameStatus.LOST, view.status());
        assertNotEquals("exploded", view.board().get(4).get(4).state());
        assertNotEquals(null, view.startedAtEpochMillis());
    }

    @Test
    void flagCanBeAddedAndRemovedBeforeFirstMove() {
        MinesweeperGame game = new MinesweeperGame(9, 9, 10);

        game.toggleFlag(0, 0);
        assertEquals("flagged", game.view().board().get(0).get(0).state());
        game.toggleFlag(0, 0);
        assertEquals("hidden", game.view().board().get(0).get(0).state());
    }

    @Test
    void chordNeedsMatchingFlags() {
        MinesweeperGame game = new MinesweeperGame(9, 9, 10);
        game.reveal(4, 4);
        GameView before = game.view();
        int row = -1;
        int column = -1;
        for (int r = 0; r < before.height() && row == -1; r++) {
            for (int c = 0; c < before.width(); c++) {
                CellView cell = before.board().get(r).get(c);
                if (cell.state().equals("revealed") && cell.adjacentMines() > 0) {
                    row = r;
                    column = c;
                    break;
                }
            }
        }

        assertNotEquals(-1, row);
        game.chord(row, column);

        assertEquals(before.board(), game.view().board());
    }
}
