import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Program extends Application implements Tile.TileListener{
	
	private final String GAME_TITLE = "MySweeper";
	
	private final Image FACESMILEY = Res.image("smiley.png");
	private final Image FACESHOCKED = Res.image("shocked.png");
	private final Image FACESUNGLASSES = Res.image("sunglasses.png");
	private final Image FACEDEAD = Res.image("dead.png");
	
	private Game gameInstance;

	private Button face;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// Prepare scene graph with required nodes
		VBox rootLayout = new VBox();
		BorderPane topBar = new BorderPane();
		GridPane minefield = new GridPane();
		
		/** Top bar **/
		BackgroundFill topBarBackgroundFill = new BackgroundFill(Color.rgb(192,192,192), 
				CornerRadii.EMPTY, Insets.EMPTY);
		Background topBarBackground = new Background(topBarBackgroundFill);
		face = new Button();
		face.setPrefSize(80, 80);
		setFaceToSmiley();

		topBar.setCenter(face);
		
		rootLayout.getChildren().addAll(topBar, minefield);
		topBar.setBackground(topBarBackground);
		
		/* 
		 * Prepare a scene w/ required dimensions and
		 * add the scene graph (root node of the scene) to it
		 */
		Scene primaryScene = new Scene(rootLayout, 600, 300);
		
		// Generate game
		gameInstance = new Game();
		// Fill gridpane with tiles from gameInstance
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				minefield.add(new Tile(this), x, y);
			}
		}
		
		
		// Prepare a stage and add the scene to the stage and display the contents
		primaryStage.setTitle(GAME_TITLE);
		primaryStage.setScene(primaryScene);
		primaryStage.show();
	}

	
	public static void main(String[] args) {
		launch(args); // internally calls start()
	}
	
	/** TILELISTENER INTERFACE **/
	public Game getGameInstance() {
		return gameInstance;
	}
	public void setFaceToSmiley() {
		face.setGraphic(new ImageView(FACESMILEY));
	}
	public void setFaceToSurprised() {}
	public void setFaceToSunglasses() {}
	public void setFaceToDead() {}

}
