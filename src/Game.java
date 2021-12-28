import java.util.Random;

public class Game {
    /**
     * We'll need to pass this instance variable to each Tile so that we can access
     * the faceButton and flag counter
     */
    private Program programInstance;
    private int flagsRemaining;
    private boolean isGameStarted;
    private boolean isGameOver;
    private int safeCellsRemaining;

    /**
     * The actually board width and height have padding so that we can perform
     * certain actions easier without over indexing on the array
     * 
     * BOMBS WILL NOT SPAWN IN THE PADDING
     */
    private int minefieldHeight, minefieldWidth;

    private Tile[][] board;

    private TimerThread timerThread;

    public Game(Program programInstance, int minefieldHeight, int minefieldWidth, int mines) {

	this.programInstance = programInstance;
	this.flagsRemaining = mines;
	this.isGameStarted = false;
	this.isGameOver = false;
	this.safeCellsRemaining = minefieldHeight * minefieldWidth - mines;

	this.minefieldHeight = minefieldHeight;
	this.minefieldWidth = minefieldWidth;

	this.board = new Tile[this.minefieldHeight + 2][this.minefieldWidth + 2];
	for (int row = 1; row <= this.minefieldHeight; row++) {
	    for (int col = 1; col <= this.minefieldWidth; col++) {
		board[row][col] = new Tile(programInstance, this, row, col);
	    }
	}

	this.timerThread = new TimerThread(programInstance);
    }

    /**
     * This function gets called after the first cell is clicked.
     */
    public void startGame(Tile startTile) {
	// These tile will be empty since they are around the starting tiles
	Tile[] startingTiles = startTile.getSurroundingTiles();
	for (Tile tile : startingTiles) {
	    if (tile != null)
		tile.setIsStartingTile(true);
	}

	/**
	 * Places mines all over the board except for cells adjacent to the startTile
	 **/
	Random rand = new Random(System.currentTimeMillis());
	for (int mines = 0; mines < programInstance.getNumberMines(); mines++) {

	    // Select a random tile
	    int randomRow; 
	    int randomCol;
	    Tile randomTile;
	    
	    do{
		randomRow = rand.nextInt(programInstance.getMinefieldHeight()) + 1;
		randomCol = rand.nextInt(programInstance.getMinefieldWidth()) + 1;
		randomTile = board[randomRow][randomCol];
	    }
	    while (randomTile.getIsMine() || randomTile.getIsStartingTile());

	    randomTile.setIsMine(true);
	}

	/**
	 * At this point, all the mine locations are determined so we need to set each
	 * tile's middle layer
	 **/
	for (int row = 1; row <= minefieldHeight; row++) {
	    for (int col = 1; col <= minefieldWidth; col++) {
		board[row][col].setMiddle();
	    }
	}

	isGameStarted = true;
	timerThread = new TimerThread(programInstance);
	timerThread.start();
    }

    public void gameOver(boolean win) {
	setIsGameOver(true);

	if (win) {
	    programInstance.setFaceToSunglasses();
	    for (int row = 1; row <= minefieldHeight; row++) {
		for (int col = 1; col <= minefieldWidth; col++) {
		    if (board[row][col].getIsMine()) {
			board[row][col].getTileButton().setVisible(true);
		    }
		}
	    }
	} else {
	    programInstance.setFaceToDead();

	    // reveal the entire board
	    for (int row = 1; row <= minefieldHeight; row++) {
		for (int col = 1; col <= minefieldWidth; col++) {
		    if (board[row][col].getIsMine() && !board[row][col].getIsFlagged()) {
			board[row][col].getTileButton().setVisible(false);
		    }
		    if (!board[row][col].getIsMine() && board[row][col].getIsFlagged()) {
			board[row][col].setMiddleBadFlag();
			board[row][col].getTileButton().setVisible(false);
		    }
		}
	    }
	}
    }

    /** GETTERS & SETTERS **/
    public void addFlag() {
	this.flagsRemaining++;
    }

    public Tile[][] getBoard() {
	return board;
    }

    public void setBoard(Tile[][] board) {
	this.board = board;
    }

    public String getFlagsRemaining() {
	String remaining = Integer.toString(flagsRemaining);
	while (remaining.length() < 3)
	    remaining = "0" + remaining;
	return remaining;
    }

    public int getNumberFlagsRemaining() {
	return flagsRemaining;
    }

    public boolean getIsGameOver() {
	return isGameOver;
    }

    public boolean getIsGameStarted() {
	return isGameStarted;
    }

    public int getNumberSafeCellsRemaining() {
	return this.safeCellsRemaining;
    }

    public void setFlagsRemaining(int flagsRemaining) {
	this.flagsRemaining = flagsRemaining;
    }

    public void setIsGameOver(boolean bool) {
	isGameOver = bool;
    }

    /**
     * CAREFUL: This function doesn't check for over flagging. Check elsewhere
     */
    public void subtractFlag() {
	this.flagsRemaining--;
    }

    /**
     * CAREFUL: This function doesn't check for over flagging. Check elsewhere
     */
    public void subtractSafeCell() {
	this.safeCellsRemaining--;
    }

    private class TimerThread extends Thread {
	private Program programInstance;

	public TimerThread(Program programInstance) {
	    this.programInstance = programInstance;
	}

	public void run() {
	    long startTime = System.currentTimeMillis();
	    long deltaTime = 0;
	    long deltaTimeSeconds = 0;
	    while (!getIsGameOver()) {
		deltaTime = System.currentTimeMillis() - startTime;
		deltaTimeSeconds = deltaTime / 1000;
		if (deltaTimeSeconds <= 999) // max time to prevent formatting issues
		    programInstance.setTimerText(deltaTimeSeconds);
	    }
	}
    }
}
