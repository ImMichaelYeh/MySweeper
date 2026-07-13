package com.mysweeper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class MinesweeperGame {
    private final UUID id = UUID.randomUUID();
    private final int width;
    private final int height;
    private final int mines;
    private final Cell[][] board;
    private GameStatus status = GameStatus.READY;
    private int flags;
    private int revealedSafeCells;
    private long startedAtEpochMillis;
    private long elapsedSeconds;

    public MinesweeperGame(int width, int height, int mines) {
        if (width < 5 || height < 5 || mines < 1 || mines > width * height - 9) {
            throw new IllegalArgumentException("Invalid board dimensions or mine count.");
        }
        this.width = width;
        this.height = height;
        this.mines = mines;
        this.flags = mines;
        board = new Cell[height][width];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) board[row][column] = new Cell();
        }
    }

    public synchronized void reveal(int row, int column) {
        Cell cell = cellAt(row, column);
        if (status == GameStatus.WON || status == GameStatus.LOST || cell.flagged || cell.revealed) return;
        if (status == GameStatus.READY) {
            placeMines(row, column);
            startedAtEpochMillis = System.currentTimeMillis();
        }
        status = GameStatus.PLAYING;
        if (cell.mine) {
            cell.revealed = true;
            status = GameStatus.LOST;
            finishTimer();
            return;
        }
        revealSafeArea(row, column);
        if (revealedSafeCells == width * height - mines) {
            status = GameStatus.WON;
            finishTimer();
        }
    }

    public synchronized void toggleFlag(int row, int column) {
        Cell cell = cellAt(row, column);
        if (status == GameStatus.WON || status == GameStatus.LOST || cell.revealed) return;
        if (cell.flagged) {
            cell.flagged = false;
            flags++;
        } else if (flags > 0) {
            cell.flagged = true;
            flags--;
        }
    }

    public synchronized void chord(int row, int column) {
        Cell cell = cellAt(row, column);
        if (status != GameStatus.PLAYING || !cell.revealed || cell.adjacentMines == 0 || flaggedNeighbors(row, column) != cell.adjacentMines) return;
        forEachNeighbor(row, column, (neighborRow, neighborColumn) -> {
            Cell neighbor = board[neighborRow][neighborColumn];
            if (!neighbor.flagged && !neighbor.revealed) reveal(neighborRow, neighborColumn);
        });
    }

    public synchronized GameView view() {
        List<List<CellView>> rows = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            List<CellView> cells = new ArrayList<>();
            for (int column = 0; column < width; column++) {
                Cell cell = board[row][column];
                String state = cell.revealed ? (cell.mine ? "exploded" : "revealed")
                    : status == GameStatus.LOST && cell.flagged && !cell.mine ? "badflag"
                    : cell.flagged ? "flagged" : status == GameStatus.LOST && cell.mine ? "mine" : "hidden";
                cells.add(new CellView(state, cell.adjacentMines));
            }
            rows.add(cells);
        }
        return new GameView(id, width, height, mines, flags, currentElapsedSeconds(),
            startedAtEpochMillis == 0 ? null : startedAtEpochMillis, status, rows);
    }

    private void placeMines(int safeRow, int safeColumn) {
        List<Integer> positions = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (Math.abs(row - safeRow) > 1 || Math.abs(column - safeColumn) > 1) positions.add(row * width + column);
            }
        }
        Collections.shuffle(positions, ThreadLocalRandom.current());
        for (int index = 0; index < mines; index++) {
            int position = positions.get(index);
            board[position / width][position % width].mine = true;
        }
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) board[row][column].adjacentMines = countAdjacentMines(row, column);
        }
    }

    private void revealSafeArea(int startRow, int startColumn) {
        ArrayDeque<Integer> pending = new ArrayDeque<>();
        pending.add(startRow * width + startColumn);
        while (!pending.isEmpty()) {
            int position = pending.remove();
            int row = position / width;
            int column = position % width;
            Cell cell = board[row][column];
            if (cell.revealed || cell.flagged || cell.mine) continue;
            cell.revealed = true;
            revealedSafeCells++;
            if (cell.adjacentMines == 0) forEachNeighbor(row, column, (r, c) -> pending.add(r * width + c));
        }
    }

    private int countAdjacentMines(int row, int column) {
        int[] count = {0};
        forEachNeighbor(row, column, (r, c) -> { if (board[r][c].mine) count[0]++; });
        return count[0];
    }

    private int flaggedNeighbors(int row, int column) {
        int[] count = {0};
        forEachNeighbor(row, column, (r, c) -> { if (board[r][c].flagged) count[0]++; });
        return count[0];
    }

    private void forEachNeighbor(int row, int column, NeighborAction action) {
        for (int r = Math.max(0, row - 1); r <= Math.min(height - 1, row + 1); r++) {
            for (int c = Math.max(0, column - 1); c <= Math.min(width - 1, column + 1); c++) {
                if (r != row || c != column) action.accept(r, c);
            }
        }
    }

    private Cell cellAt(int row, int column) {
        if (row < 0 || row >= height || column < 0 || column >= width) throw new IllegalArgumentException("Cell is outside board.");
        return board[row][column];
    }

    private long currentElapsedSeconds() {
        return status == GameStatus.PLAYING ? Math.min(999, (System.currentTimeMillis() - startedAtEpochMillis) / 1000) : elapsedSeconds;
    }

    private void finishTimer() {
        elapsedSeconds = Math.min(999, (System.currentTimeMillis() - startedAtEpochMillis) / 1000);
    }

    @FunctionalInterface
    private interface NeighborAction { void accept(int row, int column); }
}
