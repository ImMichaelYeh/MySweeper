package com.mysweeper;

import java.util.List;
import java.util.UUID;

public record GameView(UUID id, int width, int height, int mines, int flagsRemaining, long elapsedSeconds,
                       Long startedAtEpochMillis, GameStatus status, List<List<CellView>> board) { }
