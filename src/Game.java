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
    
    private final int HEIGHT = 16;
    private final int WIDTH = 30;
    private final int MINES = 99;

    /**
     * The actually board width and height have padding so that we can perform
     * certain actions easier without over indexing on the array
     * 
     * BOMBS WILL NOT SPAWN IN THE PADDING
     */
    private int height, width;

    private Tile[][] board;

    private TimerThread timerThread;

    public Game(Program programInstance, int height, int width, int mines) {
	
	this.programInstance = programInstance;
	this.flagsRemaining = mines;
	this.isGameStarted = false;
	this.isGameOver = false;
	
	this.height = height + 2;
	this.width = width + 2;
	
	this.board = new Tile[this.height][this.width];
	for (int row = 0; row < this.height; row++) {
	    for (int col = 0; col < this.width; col++) {
		board[row][col] = new Tile(programInstance, this, row, col);
	    }
	}
	
	this.timerThread = new TimerThread(programInstance, this);
    }

    private class TimerThread extends Thread {
	private Program programInstance;
	private Game gameInstance;

	public TimerThread(Program programInstance, Game gameInstance) {
	    this.programInstance = programInstance;
	    this.gameInstance = gameInstance;
	}

	public void run() {
	    long startTime = System.currentTimeMillis();
	    long deltaTime = 0;
	    long deltaTimeSeconds = 0;
	    while (isGameStarted) {
		deltaTime = System.currentTimeMillis() - startTime;
		deltaTimeSeconds = deltaTime / 1000;
		if (deltaTimeSeconds <= 999) // max time to prevent formatting issues
		    programInstance.setTimerText(deltaTimeSeconds);
	    }
	}
    }
    
    /**
     * This function gets called after the first cell is clicked.
     */
    public void startGame(Tile startTile){
	int startRow = startTile.getRow();
	int startCol = startTile.getCol();
	Tile[] surroundingTiles = startTile.getSurroundingTiles();
	
	Random rand = new Random(System.currentTimeMillis());
	for(int mines = 0; mines < MINES; mines += 0){
	    int randomRow = rand.nextInt(programInstance.getMinesHeight()) + 1;
	    int randomCol;
	    for (Tile t : surroundingTiles){
		
	    }
	    mines++;
	}
    }

    /** GETTERS & SETTERS **/

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
    
    public boolean getIsGameOver(){
	return isGameOver;
    }
    
    public boolean getIsGameStarted(){
	return isGameStarted;
    }
    
    public void setFlagsRemaining(int flagsRemaining) {
	this.flagsRemaining = flagsRemaining;
    }
}
