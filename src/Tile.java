import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class Tile extends StackPane{
	
	private static final Image[] NUMBER = { 
		Res.image("zero.png"), Res.image("one.png"), Res.image("two.png"), 
		Res.image("three.png"), Res.image("four.png"), Res.image("five.png"), 
		Res.image("six.png"), Res.image("seven.png"), Res.image("eight.png") 
	};
	
	private static final Image FLAG = Res.image("flag.png");
	private static final Image BADFLAG = Res.image("flag.png");
	
	private static final Image MINE = Res.image("mine.png");
	private static final Image MINECLICKED = Res.image("mineclicked.png");
	
	private static final Image BACKGROUND = Res.image("background.png");
	private static final Image BORDEREDBACKGROUND = Res.image("borderedbackground.png");
	
	
	private TileListener player;
	private Game gameInstance;
	
	private StackPane stackpane;
	private Button tileButton;
	private ImageView number;
	private ImageView background;
	
	private boolean isClicked;
	private boolean isEmpty;
	private boolean isFlagged;

	public Tile(TileListener player) {
		this.player = player;
		gameInstance = player.getGameInstance();
		stackpane = new StackPane();
		tileButton = new Button();
		number = new ImageView(NUMBER[0]);
		background = new ImageView(BACKGROUND);
		isClicked = false;
		isEmpty = true;
		
		setSize(10);
		
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
	
	public void setTileNumber(int number) {
		if (number == 0) {
			isEmpty = true;
		}
		
		if (number >= 0 || number <= 8) {
			this.number = new ImageView(NUMBER[number]);
		}
	}
	
	public void click() {
		if (!isClicked) {
			this.isClicked = true;
			
			// If the tile is flagged, it can not be clicked or cleared by other tiles
			if (!isFlagged) {
				if (isEmpty) clearSurrounding();
				
				// TODO: What happens when the tile is clicked 
			}
		}
	}
	
	// invoke click of all surrounding tiles
	private void clearSurrounding() {
		
	}
	
	public void setSize(int width) {
		stackpane.setMaxSize(width, width);
		tileButton.setMaxSize(width, width);
		number.setFitHeight(width);
		number.setFitWidth(width);
	}
	
	public interface TileListener{
		public Game getGameInstance();
		public void setFaceToSmiley();
		public void setFaceToSurprised();
		public void setFaceToSunglasses();
		public void setFaceToDead();
	}
}
