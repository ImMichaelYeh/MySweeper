import java.util.ArrayList;
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
    private ArrayList<Tile> tilesToReveal;
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
	this.tilesToReveal = new ArrayList<Tile>();

	this.minefieldHeight = minefieldHeight;
	this.minefieldWidth = minefieldWidth;

	// Added a 1 tile border around the board so that the playable board is
	// [1, minefieldHeight] x [1, minefieldWidth]
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
	/** 
	 * Setting the startingTiles. The startingTiles include the tile that 
	 * the player clicked on and the 8 adjacent tiles. These tiles will 
	 * be empty to ensure that you do not lose on the first click.
	 **/
	Random rand = new Random(System.currentTimeMillis());
	
	Tile[] startingTiles = startTile.getSurroundingTiles();
	for (Tile tile : startingTiles) {
	    if (tile != null) {
		tile.setIsStartingTile(true);
	    }
	}

	/**
	 * Randomly place mines all over the board except for the startingTiles
	 * 
	 * Algorithm:
	 * 1. Generate all possible positions on the board
	 * 2. Shuffle positions
	 * 3. Take next position and set as mine until all mines are set
	 **/
	ArrayList<int[]> positions = new ArrayList<int[]>();
	
	for (int row = 1; row <= programInstance.getMinefieldHeight(); row++) {
	    for (int col = 1; col <= programInstance.getMinefieldWidth(); col++) {
		int[] pos = new int[2];
		pos[0] = row;
		pos[1] = col;
		positions.add(pos);
	    }
	}

	ArrayList<int[]> positionsShuffled = new ArrayList<int[]>();
	for (int index = 0; index < positions.size(); index++) {
	    int rNum = rand.nextInt(positions.size());
	    
	    positionsShuffled.add(positions.get(rNum));
	    positions.remove(rNum);
	}
	
	
	for (int mines = 0; mines < programInstance.getNumberMines(); mines++) {

	    // Select a random tile
	    int randomRow; 
	    int randomCol;
	    Tile randomTile;
	    
	    do{
		int[] pos = positionsShuffled.get(0);
		positionsShuffled.remove(0);
		
		randomRow = pos[0];
		randomCol = pos[1];
		
		randomTile = board[randomRow][randomCol];
	    }
	    while (randomTile.getIsStartingTile());

	    randomTile.setIsMine(true);
	}

	/**
	 * At this point, all the mine locations are determined so we need to set each
	 * tile's middle layer.
	 * 
	 * The .setMiddle function tells each tile to figure out their own value.
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
		    if (!board[row][col].getIsMine()) {
			board[row][col].getTileButton().setVisible(false);
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
	while (remaining.length() < 3) {
	    remaining = "0" + remaining;
	}
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

    public ArrayList<Tile> getTilesToReveal() {
	return this.tilesToReveal;
    }
    
    public void setTilesToReveal(ArrayList<Tile> tilesToReveal) {
	this.tilesToReveal = tilesToReveal;
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
		
		if (deltaTimeSeconds <= 999) {// max time to prevent formatting issues
		    programInstance.setTimerText(deltaTimeSeconds);
		}
	    }
	}
    }
}
