import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    private final int HEIGHT = 16;
    private final int WIDTH = 30;
    private final int MINES = 99;

    private final int STAGEWIDTH = CELLSIZE * WIDTH;
    private final int STAGEHEIGHT = CELLSIZE * HEIGHT + TOPBARHEIGHT;

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
    
    public static void main(String[] args) {
	launch(args); // internally calls start()
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
	// Prepare scene graph with required nodes
	StackPane flagsPane = new StackPane(), facePane = new StackPane(), timerPane = new StackPane();

	/** START TOP BAR **/
	BackgroundFill topBarBackgroundFill = new BackgroundFill(Color.rgb(222, 222, 222), CornerRadii.EMPTY,
		Insets.EMPTY);
	Background topBarBackground = new Background(topBarBackgroundFill);

	flagsRemainingText = new Text("099");
	faceButton = new Button();
	faceButton.setOnMousePressed(new FaceButtonPressedListener(this));
	timerText = new Text("000");

	flagsPane.getChildren().add(flagsRemainingText);
	flagsPane.setPrefWidth(STAGEWIDTH * 0.25);
	flagsRemainingText.setFont(Font.font("Lucida Sans Typewriter", FontWeight.BOLD, FontPosture.REGULAR, 50));
	flagsRemainingText.setFill(Color.RED);
	flagsRemainingText.setStroke(Color.BLACK);
	TOPBAR.setMinHeight(TOPBARHEIGHT);

	facePane.getChildren().add(faceButton);
	setFaceToSmile();

	timerPane.getChildren().add(timerText);
	timerPane.setPrefWidth(STAGEWIDTH * 0.25);
	timerText.setFont(Font.font("Lucida Sans Typewriter", FontWeight.BOLD, FontPosture.REGULAR, 50));
	timerText.setFill(Color.RED);
	timerText.setStroke(Color.BLACK);

	TOPBAR.setLeft(flagsPane);
	TOPBAR.setCenter(facePane);
	TOPBAR.setRight(timerPane);
	ROOTLAYOUT.getChildren().add(TOPBAR);
	TOPBAR.setBackground(topBarBackground);
	/** END TOP BAR **/
	
	ROOTLAYOUT.getChildren().add(MINEFIELD);
	
	newGame();

	/*
	 * Prepare a scene w/ required dimensions and add the scene graph (root node of
	 * the scene) to it
	 */
	Scene primaryScene = new Scene(ROOTLAYOUT, STAGEWIDTH, STAGEHEIGHT);
	primaryStage.setTitle(GAMETITLE);
	primaryStage.getIcons().add(new Image("res/mine.png"));
	primaryStage.setScene(primaryScene);
	primaryStage.show();
	// FIX RESIZING ISSUES
    }

    public void updateFlagCounter(){
	flagsRemainingText.setText(game.getFlagsRemaining());
    }
    
    private void newGame(){
	if (game != null) {
	    game.setIsGameOver(true);
	}
	
	game = new Game(this, HEIGHT, WIDTH, MINES);
	MINEFIELD.getChildren().clear();
	flagsRemainingText.setText(game.getFlagsRemaining());
	timerText.setText("000");
	
	/** START MINEFIELD **/
	Tile[][] board = game.getBoard();
	
	for (int row = 1; row <= HEIGHT; row++) {
	    for (int col = 1; col <= WIDTH; col++) {
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
	
	public FaceButtonPressedListener(Program programInstance){
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

    public int getMinesWidth() {
	return this.WIDTH;
    }

    public int getMinesHeight() {
	return this.HEIGHT;
    }
    
    public int getNumberMines(){
	return MINES;
    }
}
