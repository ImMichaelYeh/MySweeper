import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Program extends Application {

    private final String GAMETITLE = "MySweeper";

    private final int CELLSIZE = 40;
    private final int FACEBUTTONSIZE = 50;
    private final int TOPBARHEIGHT = 100;

    private final ImageView FACESMILEY = new ImageView("res/smiley.png");
    private final ImageView FACESHOCKED = new ImageView("res/shocked.png");
    private final ImageView FACESUNGLASSES = new ImageView("res/sunglasses.png");
    private final ImageView FACEDEAD = new ImageView("res/dead.png");

    private final VBox ROOTLAYOUT = new VBox();
    private final BorderPane TOPBAR = new BorderPane();
    private final GridPane MINEFIELD = new GridPane();

    private Game game;

    private Text flagsRemainingText;
    private Button faceButton;
    private Text timerText;

    // default values are for expert mode
    private int minefieldHeight = 16;
    private int minefieldWidth = 30;
    private int mines = 99;
    
    public static void main(String[] args) {
	launch(args); // internally calls start()
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
	
	/** START TOP MENU **/
	// Whenever a difficulty is selected, it will resize the window
	MenuBar menuBar = new MenuBar();
	Menu difficultyMenu = new Menu("Difficulty");
	MenuItem difficultyMenuBeginner = new MenuItem("Beginner");
	difficultyMenuBeginner.setOnAction(event -> {
	    minefieldHeight = 9;
	    minefieldWidth = 9;
	    mines = 10;
	    setStageSize(primaryStage);
	    newGame();
	});
	MenuItem difficultyMenuIntermediate = new MenuItem("Intermediate");
	difficultyMenuIntermediate.setOnAction(event -> {
	    minefieldHeight = 16;
	    minefieldWidth = 16;
	    mines = 40;
	    setStageSize(primaryStage);
	    newGame();
	});
	MenuItem difficultyMenuExpert = new MenuItem("Expert");
	difficultyMenuExpert.setOnAction(event -> {
	    minefieldHeight = 16;
	    minefieldWidth = 30;
	    mines = 99;
	    setStageSize(primaryStage);
	    newGame();
	});
	
	difficultyMenu.getItems().addAll(difficultyMenuBeginner, difficultyMenuIntermediate, difficultyMenuExpert);
	menuBar.getMenus().add(difficultyMenu);
	ROOTLAYOUT.getChildren().add(menuBar);
	/** END TOP MENU **/
	
	// Prepare scene graph with required nodes
	StackPane flagsPane = new StackPane();
	StackPane facePane = new StackPane();
	StackPane timerPane = new StackPane();

	/** START TOP BAR **/
	BackgroundFill topBarBackgroundFill = new BackgroundFill(Color.rgb(222, 222, 222), CornerRadii.EMPTY,
		Insets.EMPTY);
	Background topBarBackground = new Background(topBarBackgroundFill);

	flagsRemainingText = new Text("099");
	faceButton = new Button();
	faceButton.setOnMousePressed(new FaceButtonPressedListener(this));
	timerText = new Text("000");

	flagsPane.getChildren().add(flagsRemainingText);
	flagsPane.setPrefWidth(64);
	flagsRemainingText.setFont(Font.font("Lucida Sans Typewriter", FontWeight.BOLD, FontPosture.REGULAR, 50));
	flagsRemainingText.setFill(Color.RED);
	flagsRemainingText.setStroke(Color.BLACK);
	flagsPane.setMargin(flagsRemainingText, new Insets(0, 0, 0, 32));
	TOPBAR.setMinHeight(TOPBARHEIGHT);

	facePane.getChildren().add(faceButton);
	setFaceToSmile();

	timerPane.getChildren().add(timerText);
	timerPane.setPrefWidth(64);
	timerText.setFont(Font.font("Lucida Sans Typewriter", FontWeight.BOLD, FontPosture.REGULAR, 50));
	timerText.setFill(Color.RED);
	timerText.setStroke(Color.BLACK);
	timerPane.setMargin(timerText, new Insets(0, 32, 0, 0));
	
	TOPBAR.setLeft(flagsPane);
	TOPBAR.setCenter(facePane);
	TOPBAR.setRight(timerPane);
	TOPBAR.setBackground(topBarBackground);
	ROOTLAYOUT.getChildren().add(TOPBAR);
	/** END TOP BAR **/

	ROOTLAYOUT.getChildren().add(MINEFIELD);

	newGame();

	/*
	 * Prepare a scene w/ required dimensions and add the scene graph (root node of
	 * the scene) to it
	 */
	Scene primaryScene = new Scene(ROOTLAYOUT);
	setStageSize(primaryStage);
	primaryStage.setTitle(GAMETITLE);
	primaryStage.getIcons().add(new Image("res/mine.png"));
	primaryStage.setScene(primaryScene);
	primaryStage.show();
	primaryStage.setResizable(false);
	//TODO: FIX RESIZING ISSUE: User should not be able to resize window.
	// But locking window size breaks display for some reason (Probably a JavaFX issue).
    }
    
    private void setStageSize(Stage stage){
	
	// The factors 0.4 and 1.6 seems to be a weird quirk with javaFX. Not sure how to fix it yet.
	stage.setWidth(CELLSIZE * getMinefieldWidth() + CELLSIZE * 0.4);
	stage.setHeight(CELLSIZE * getMinefieldHeight() + TOPBARHEIGHT + CELLSIZE * 1.6);
    }
   
    public void updateFlagCounter() {
	flagsRemainingText.setText(game.getFlagsRemaining());
    }

    private void newGame() {
	if (game != null) {
	    game.setIsGameOver(true);
	}

	game = new Game(this, getMinefieldHeight(), getMinefieldWidth(), mines);
	MINEFIELD.getChildren().clear();
	flagsRemainingText.setText(game.getFlagsRemaining());
	timerText.setText("000");

	/** START MINEFIELD **/
	Tile[][] board = game.getBoard();

	for (int row = 1; row <= getMinefieldHeight(); row++) {
	    for (int col = 1; col <= getMinefieldWidth(); col++) {
		Node tile = board[row][col].getStackPane();
		MINEFIELD.add(tile, col, row); // for some reason, grid pane uses (col, row) for coordinates instead of
					       // (row, col)
	    }
	}
	/** END MINEFIELD **/
    }
    
    public int getCellSize() {
	return CELLSIZE;
    }

    public void setFaceToSmile() {
	FACESMILEY.setFitHeight(FACEBUTTONSIZE);
	FACESMILEY.setFitWidth(FACEBUTTONSIZE);
	faceButton.setGraphic(FACESMILEY);
    }

    public void setFaceToShocked() {
	FACESHOCKED.setFitHeight(FACEBUTTONSIZE);
	FACESHOCKED.setFitWidth(FACEBUTTONSIZE);
	faceButton.setGraphic(FACESHOCKED);
    }

    public void setFaceToSunglasses() {
	FACESUNGLASSES.setFitHeight(FACEBUTTONSIZE);
	FACESUNGLASSES.setFitWidth(FACEBUTTONSIZE);
	faceButton.setGraphic(FACESUNGLASSES);
    }

    public void setFaceToDead() {
	FACEDEAD.setFitHeight(FACEBUTTONSIZE);
	FACEDEAD.setFitWidth(FACEBUTTONSIZE);
	faceButton.setGraphic(FACEDEAD);
    }

    public void setTimerText(long seconds) {
	String timer = Long.toString(seconds);
	while (timer.length() < 3)
	    timer = "0" + timer;
	if (timerText != null)
	    timerText.setText(timer);
    }

    /**
     * faceButton action listener
     */
    private class FaceButtonPressedListener implements EventHandler<MouseEvent> {

	private Program programInstance;

	public FaceButtonPressedListener(Program programInstance) {
	    this.programInstance = programInstance;
	}

	@Override
	public void handle(MouseEvent event) {
	    if (event.getButton() == MouseButton.PRIMARY) {
		newGame();
		programInstance.setFaceToSmile();
	    }
	}
    }

    public int getMinefieldWidth() {
	return this.minefieldWidth;
    }

    public int getMinefieldHeight() {
	return this.minefieldHeight;
    }

    public int getNumberMines() {
	return mines;
    }
}
