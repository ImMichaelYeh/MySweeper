import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Program extends Application{

    private final String GAME_TITLE = "MySweeper";

    private Text flagsRemainingText;
    private Button faceButton;
    private Text timerText;

   
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

    @Override
    public void start(Stage primaryStage) throws Exception {
	Game game = new Game(this, HEIGHT, WIDTH, MINES);

	// Prepare scene graph with required nodes
	VBox rootLayout = new VBox();
	BorderPane topBar = new BorderPane();
	StackPane flagsPane = new StackPane(), facePane = new StackPane(), timerPane = new StackPane();
	GridPane minefield = new GridPane();

	/** START TOP BAR **/
	BackgroundFill topBarBackgroundFill = new BackgroundFill(Color.rgb(222, 222, 222), CornerRadii.EMPTY,
		Insets.EMPTY);
	Background topBarBackground = new Background(topBarBackgroundFill);

	flagsRemainingText = new Text("099");
	faceButton = new Button();
	faceButton.setOnMouseReleased(new FaceButtonReleasedListener());
	timerText = new Text("000");

	flagsPane.getChildren().add(flagsRemainingText);
	flagsPane.setPrefWidth(STAGEWIDTH * 0.25);
	flagsRemainingText.setFont(Font.font("Lucida Sans Typewriter", FontWeight.BOLD, FontPosture.REGULAR, 50));
	flagsRemainingText.setFill(Color.RED);
	flagsRemainingText.setStroke(Color.BLACK);
	topBar.setMinHeight(TOPBARHEIGHT);

	facePane.getChildren().add(faceButton);
	setFaceToSmile();

	timerPane.getChildren().add(timerText);
	timerPane.setPrefWidth(STAGEWIDTH * 0.25);
	timerText.setFont(Font.font("Lucida Sans Typewriter", FontWeight.BOLD, FontPosture.REGULAR, 50));
	timerText.setFill(Color.RED);
	timerText.setStroke(Color.BLACK);

	topBar.setLeft(flagsPane);
	topBar.setCenter(facePane);
	topBar.setRight(timerPane);
	rootLayout.getChildren().add(topBar);
	topBar.setBackground(topBarBackground);
	/** END TOP BAR **/

	/** START MINEFIELD **/
	Tile[][] board = game.getBoard();
	rootLayout.getChildren().add(minefield);
	for (int row = 0; row < HEIGHT; row++) {
	    for (int col = 0; col < WIDTH; col++) {
		Node tile = board[row][col].getStackPane();
		// minefield.setMargin(tile, new Insets(0.5,0.5,0.5,0.5));
		minefield.add(tile, col, row); // for some reason, grid pane uses (col, row) for coordinates instead of
					       // (row, col)
	    }
	}
	/** END MINEFIELD **/

	/*
	 * Prepare a scene w/ required dimensions and add the scene graph (root node of
	 * the scene) to it
	 */
	Scene primaryScene = new Scene(rootLayout, STAGEWIDTH, STAGEHEIGHT);
	primaryStage.setTitle(GAME_TITLE);
	primaryStage.setScene(primaryScene);
	primaryStage.show();
	// FIX RESIZING ISSUES
    }

    public static void main(String[] args) {
	launch(args); // internally calls start()
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
    private class FaceButtonReleasedListener implements EventHandler<MouseEvent> {
	@Override
	public void handle(MouseEvent event) {
	    if (event.getButton() == MouseButton.PRIMARY){
		
	    }
	}
    }
    
    public int getMinesWidth(){
	return this.WIDTH;
    }
    
    public int getMinesHeight(){
	return this.HEIGHT;
    }
}
