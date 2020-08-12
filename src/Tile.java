import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class Tile extends StackPane{
	
	private static final Image[] NUMBER = { new Image("res/zero.png"), new Image("res/one.png"), new Image("res/two.png"), 
											new Image("res/three.png"), new Image("res/four.png"), new Image("res/five.png"), 
											new Image("res/six.png"), new Image("res/seven.png"), new Image("res/eight.png") };
	private static final Image FLAG = new Image("res/flag.png");
	private static final Image BADFLAG = new Image("res/flag.png");
	
	private static final Image MINE = new Image("res/mine.png");
	private static final Image MINECLICKED = new Image("res/mineclicked.png");
	
	private static final Image BACKGROUND = new Image("res/background.png");
	private static final Image BORDEREDBACKGROUND = new Image("res/borderedbackground.png");
	
	
	private TileListener player;
	
	private StackPane stackpane;
	private Button tileButton;
	private ImageView number;
	private ImageView background;

	public Tile(TileListener player) {
		this.player = player;
		stackpane = new StackPane();
		tileButton = new Button();
		number = new ImageView(NUMBER[0]);
		background = new ImageView(BACKGROUND);
		
		stackpane.getChildren().add(background);
		stackpane.getChildren().add(number);
		stackpane.getChildren().add(tileButton);
	}
	
	public Tile(TileListener player, int tileNumber) {
		this(player);
		
		if (tileNumber >= 0 && tileNumber <= 8) {
			number.setImage(NUMBER[tileNumber]);
		}
	}
	
	public void setSize(int width) {
		stackpane.setMaxSize(width, width);
		tileButton.setMaxSize(width, width);
		number.setFitHeight(width);
		number.setFitWidth(width);
	}
	
	public interface TileListener{
		public void setFaceToSmile();
		public void setFaceToSurprised();
		public void setFaceToSunglasses();
		public void setFaceToDead();
	}
}
