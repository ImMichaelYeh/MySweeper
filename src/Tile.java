import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class Tile extends StackPane {

    private static final String NUMBERS[] = { 
	    "res/zero.png", "res/one.png", "res/two.png", 
	    "res/three.png", "res/four.png", "res/five.png", 
	    "res/six.png", "res/seven.png", "res/eight.png" };

    private static final String FLAG = "res/flag.png";
    private static final String BADFLAG = "res/badflag.png";
    private static final String MINE = "res/mine.png";
    private static final String MINECLICKED = "res/mineclicked.png";
    private static final String BORDEREDBACKGROUND = "res/borderedbackground.png";

    private Program programInstance;
    private Game gameInstance;
    private int row, col;
    private int numberMinesSurrounding;
    private boolean isFlagged;
    private boolean isMine;
    private boolean isRevealed;
    private boolean isStartingTile;

    private StackPane stackPane;
    private Button tileButton; // Possible images: FLAG
    private ImageView middle; // Possible images: NUMBER, BADFLAG, MINE, MINECLICKED
    private ImageView background; // Possible images: BORDEREDBACKGROUND

    private static boolean REVEALALLMINES = false; // for debugging purposes

    public Tile(Program programInstance, Game gameInstance, int row, int col) {
	this.programInstance = programInstance;
	this.gameInstance = gameInstance;
	this.row = row;
	this.col = col;

	numberMinesSurrounding = 0;
	isFlagged = false;
	isMine = false;
	isRevealed = false;
	isStartingTile = false;

	stackPane = new StackPane();
	tileButton = new Button();
	tileButton.setOnMousePressed(new ButtonPressedListener(programInstance, gameInstance, this));
	tileButton.setOnMouseReleased(new ButtonReleasedListener(programInstance, gameInstance));

	middle = new ImageView(NUMBERS[0]);
	background = new ImageView(BORDEREDBACKGROUND);
	stackPane.getChildren().add(background);
	stackPane.getChildren().add(middle);
	stackPane.getChildren().add(tileButton);
	setSize(programInstance.getCellSize());

    }

    public void setSize(int width) {
	stackPane.setMinSize(width, width);
	stackPane.setMaxSize(width, width);
	stackPane.setPrefSize(width, width);

	tileButton.setMinSize(width, width);
	tileButton.setMaxSize(width, width);
	tileButton.setPrefSize(width, width);

	middle.setFitHeight(width);
	middle.setFitWidth(width);

	background.setFitHeight(width);
	background.setFitWidth(width);
    }

    /**
     * Performs all action after a click
     */
    private void click() {
	if (!gameInstance.getIsGameOver()) {
	    gameInstance.getTilesToReveal().add(this);
	    setIsRevealed(true);

	    if (!getIsMine()) {
		if (numberMinesSurrounding == 0) {
		    for (Tile t : getSurroundingTiles()) {
			if (t != null && t != this && !t.getIsFlagged() && !t.getIsRevealed()) {
			    t.click();
			}
		    }
		}

		gameInstance.subtractSafeCell();

		if (gameInstance.getNumberSafeCellsRemaining() <= 0) {
		    gameInstance.gameOver(true);
		}

	    } else {
		gameInstance.gameOver(false);
		this.middle = new ImageView(MINECLICKED);
		middle.setFitHeight(programInstance.getCellSize() * .75);
		middle.setFitWidth(programInstance.getCellSize() * .75);
		stackPane.getChildren().set(1, middle);
	    }
	}
    }

    public int getRow() {
	return this.row;
    }

    public int getCol() {
	return this.col;
    }

    public Button getTileButton() {
	return tileButton;
    }

    public boolean getIsFlagged() {
	return isFlagged;
    }

    public StackPane getStackPane() {
	return stackPane;
    }

    public boolean getIsMine() {
	return isMine;
    }

    public boolean getIsRevealed() {
	return this.isRevealed;
    }

    public void setIsMine(boolean bool) {
	this.isMine = bool;
    }

    public void setIsRevealed(Boolean bool) {
	this.isRevealed = bool;
    }

    public void setIsStartingTile(Boolean bool) {
	this.isStartingTile = bool;
    }

    public boolean getIsStartingTile() {
	return this.isStartingTile;
    }

    /**
     * Automatically decides the middle layer based on it's current state
     */
    public void setMiddle() {
	if (isMine) {
	    middle = new ImageView(MINE);
	} else {
	    Tile[] surroundingTiles = getSurroundingTiles();
	    this.numberMinesSurrounding = 0;

	    for (Tile t : surroundingTiles) {
		if (t != null && t.getIsMine()) {
		    this.numberMinesSurrounding++;
		}
	    }
	    middle = new ImageView(NUMBERS[numberMinesSurrounding]);
	}
	middle.setFitHeight(programInstance.getCellSize() * .75);
	middle.setFitWidth(programInstance.getCellSize() * .75);
	stackPane.getChildren().set(1, middle);
    }

    public void setMiddleBadFlag() {
	middle = new ImageView(BADFLAG);
	middle.setFitHeight(programInstance.getCellSize() * .75);
	middle.setFitWidth(programInstance.getCellSize() * .75);
	stackPane.getChildren().set(1, middle);
    }

    public Tile[] getSurroundingTiles() {
	// top-left, top, top-right, left, current, right, bottom-left, bottom,
	// bottom-right
	Tile[] surroundingTiles = new Tile[9];
	int index = 0;
	int startRow = this.row - 1;
	int startCol = this.col - 1;
	for (int row = startRow; row < startRow + 3; row++) {
	    for (int col = startCol; col < startCol + 3; col++) {
		surroundingTiles[index] = gameInstance.getBoard()[row][col];
		index++;
	    }
	}
	
	return surroundingTiles;
    }

    /**
     * Button pressed listener
     * 
     * On press, if right click, add flag if left click, change face to shocked
     */
    private class ButtonPressedListener implements EventHandler<MouseEvent> {

	private Program programInstance;
	private Game gameInstance;
	private Tile parentTile;

	public ButtonPressedListener(Program programInstance, Game gameInstance, Tile parentTile) {
	    this.programInstance = programInstance;
	    this.gameInstance = gameInstance;
	    this.parentTile = parentTile;
	}

	@Override
	public void handle(MouseEvent event) {
	    if (event.getButton() == MouseButton.PRIMARY && !gameInstance.getIsGameOver()) {
		setFaceToShocked();

		if (!parentTile.getIsFlagged() && !gameInstance.getIsGameStarted()) {
		    gameInstance.startGame(parentTile);
		}

		if (!parentTile.getIsFlagged()) {
		    click();
		}

	    }
	    // RIGHT CLICK (Setting flags)
	    if (event.getButton() == MouseButton.SECONDARY) {
		if (!isFlagged && !gameInstance.getIsGameOver()) {
		    if (gameInstance.getNumberFlagsRemaining() > 0) {
			isFlagged = true;
			ImageView flag = new ImageView(FLAG);
			flag.setFitWidth(programInstance.getCellSize() * .75);
			flag.setFitHeight(programInstance.getCellSize() * .75);
			parentTile.getTileButton().setGraphic(flag);
			gameInstance.subtractFlag();
			programInstance.updateFlagCounter();
		    }
		} else if (!gameInstance.getIsGameOver()) {
		    isFlagged = false;
		    parentTile.getTileButton().setGraphic(null);
		    gameInstance.addFlag();
		    programInstance.updateFlagCounter();
		}
	    }
	}

	public void setFaceToShocked() {
	    programInstance.setFaceToShocked();
	}

    }

    /**
     * Button released listener On release, if left click, check if cell is bomb if
     * cell is bomb, change face to dead if cell is not bomb, change face to smiley
     * if all cells cleared, change face to sunglasses
     */
    private class ButtonReleasedListener implements EventHandler<MouseEvent> {

	private Program programInstance;
	private Game gameInstance;

	public ButtonReleasedListener(Program programInstance, Game gameInstance) {
	    this.programInstance = programInstance;
	    this.gameInstance = gameInstance;
	}

	@Override
	public void handle(MouseEvent event) {
	    if (event.getButton() == MouseButton.PRIMARY) {

		if (!gameInstance.getIsGameOver()) {
		    setFaceToSmile();

		    for (int i = 0; i < gameInstance.getTilesToReveal().size(); i++) {
			gameInstance.getTilesToReveal().get(i).tileButton.setVisible(false);
		    }

		    gameInstance.setTilesToReveal(new ArrayList<Tile>());
		}
	    }

	    /**
	     * DEBUGGING (Reveal all bombs)
	     */
	    if (REVEALALLMINES && event.getButton() == MouseButton.MIDDLE) {

		Tile[][] board = gameInstance.getBoard();
		for (int row = 1; row <= programInstance.getMinefieldHeight(); row++) {
		    for (int col = 1; col <= programInstance.getMinefieldWidth(); col++) {
			if (board[row][col].getIsMine()) {
			    if (board[row][col].getTileButton().isVisible()) {
				board[row][col].getTileButton().setVisible(false);
			    } else {
				board[row][col].getTileButton().setVisible(true);
			    }
			}
		    }
		}
	    }

	}

	public void setFaceToSmile() {
	    programInstance.setFaceToSmile();
	}
    }
}