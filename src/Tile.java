import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class Tile extends StackPane {

    private static final ImageView[] NUMBER = { new ImageView("res/zero.png"), new ImageView("res/one.png"),
	    new ImageView("res/two.png"), new ImageView("res/three.png"), new ImageView("res/four.png"),
	    new ImageView("res/five.png"), new ImageView("res/six.png"), new ImageView("res/seven.png"),
	    new ImageView("res/eight.png") };
    private static final Image FLAG = new Image("res/flag.png");
    private static final Image BADFLAG = new Image("res/flag.png");

    private static final Image MINE = new Image("res/mine.png");
    private static final Image MINECLICKED = new Image("res/mineclicked.png");

    private static final Image BACKGROUND = new Image("res/background.png");
    private static final Image BORDEREDBACKGROUND = new Image("res/borderedbackground.png");

    private Program instance;

    private StackPane stackPane;
    private Button tileButton; // Possible images: FLAG
    private ImageView middle; // Possible images: NUMBER, BADFLAG, MINE, MINECLICKED
    private ImageView background; // Possible images: BORDEREDBACKGROUND

    public Tile(Program instance) {
	this.instance = instance;
	stackPane = new StackPane();
	tileButton = new Button();
	middle = NUMBER[0];
	background = new ImageView(BORDEREDBACKGROUND);
	stackPane.getChildren().add(background);
	stackPane.getChildren().add(middle);
	stackPane.getChildren().add(tileButton);
	setSize(instance.getCellSize());
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

    public StackPane getStackPane() {
	return stackPane;
    }

    public interface TileListener {
	public void setFaceToSmile();

	public void setFaceToSurprised();

	public void setFaceToSunglasses();

	public void setFaceToDead();
    }
}
