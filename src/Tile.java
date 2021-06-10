import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class Tile extends StackPane {

    private static final ImageView[] NUMBER = { new ImageView("res/zero.png"), new ImageView("res/one.png"),
	    new ImageView("res/two.png"), new ImageView("res/three.png"), new ImageView("res/four.png"),
	    new ImageView("res/five.png"), new ImageView("res/six.png"), new ImageView("res/seven.png"),
	    new ImageView("res/eight.png") };
    private static final ImageView FLAG = new ImageView("res/flag.png");
    private static final ImageView BADFLAG = new ImageView("res/flag.png");

    private static final ImageView MINE = new ImageView("res/mine.png");
    private static final ImageView MINECLICKED = new ImageView("res/mineclicked.png");

    private static final ImageView BACKGROUND = new ImageView("res/background.png");
    private static final ImageView BORDEREDBACKGROUND = new ImageView("res/borderedbackground.png");

    private Program programInstance;
    private Game gameInstance;
    private int row, col;
    private int bombsSurrounding;
    private boolean isFlagged;
    private boolean isBomb;
    private boolean isRevealed;

    private StackPane stackPane;
    private Button tileButton; // Possible images: FLAG
    private ImageView middle; // Possible images: NUMBER, BADFLAG, MINE, MINECLICKED
    private ImageView background; // Possible images: BORDEREDBACKGROUND

    public Tile(Program programInstance, Game gameInstance, int row, int col) {
	this.programInstance = programInstance;
	this.gameInstance = gameInstance;
	this.row = row;
	this.col = col;
	bombsSurrounding = 0;
	isFlagged = false;
	isBomb = false;
	isRevealed = false;

	stackPane = new StackPane();
	tileButton = new Button();
	tileButton.setOnMousePressed(new ButtonPressedListener(programInstance, gameInstance, this));
	tileButton.setOnMouseReleased(new ButtonReleasedListener(programInstance, gameInstance, this));

	middle = NUMBER[bombsSurrounding];
	background = BORDEREDBACKGROUND;
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
    private void click(){
	
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

	public ButtonPressedListener(Program programInstance, Game gameInstance, Tile parentTile){
	    this.programInstance = programInstance;
	    this.gameInstance = gameInstance;
	    this.parentTile = parentTile;
	}

	@Override
	public void handle(MouseEvent event) {
	    if (event.getButton() == MouseButton.PRIMARY) {
		setFaceToShocked();
	    }
	    // RIGHT CLICK (Setting flags)
	    if (event.getButton() == MouseButton.SECONDARY) {
		if (!isFlagged) {
		    isFlagged = true;
		    FLAG.setFitWidth(programInstance.getCellSize() * .75);
		    FLAG.setFitHeight(programInstance.getCellSize() * .75);
		    tileButton.setGraphic(FLAG);
		} else {
		    isFlagged = false;
		    tileButton.setGraphic(null);
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
	private Tile parentTile;

	public ButtonReleasedListener(Program programInstance, Game gameInstance, Tile parenTile) {
	    this.programInstance = programInstance;
	    this.gameInstance = gameInstance;
	    this.parentTile = parentTile;
	}

	@Override
	public void handle(MouseEvent event) {
	    if (event.getButton() == MouseButton.PRIMARY) {
		if (!gameInstance.getIsGameStarted()){
		    gameInstance.startGame(parentTile);
		}
		click();
		if (!gameInstance.getIsGameOver()){
		    setFaceToSmile();
		}
	    }
	}

	public void setFaceToSmile() {
	    programInstance.setFaceToSmile();
	}

	public void setFaceToSunglasses() {
	    programInstance.setFaceToSunglasses();
	}

	public void setFaceToDead() {
	    programInstance.setFaceToDead();
	}
    }

    public int getRow() {
	return this.row;
    }

    public int getCol() {
	return this.col;
    }

    public Tile[] getSurroundingTiles() {
	// top left, top, top right, left, current, right, bottom left, bottom, bottom
	// right
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
    
    public StackPane getStackPane() {
	return stackPane;
    }
}
