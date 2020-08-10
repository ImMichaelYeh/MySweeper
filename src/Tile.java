import javafx.scene.image.Image;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class Tile extends StackPane{
	
	private static final Image[] NUMBERTILES = {new Image("res/zero.png"), new Image("res/one.png"), new Image("res/two.png"), 
											new Image("res/three.png"), new Image("res/four.png"), new Image("res/five.png"), 
											new Image("res/six.png"), new Image("res/seven.png"), new Image("res/eight.png")};
	private static final Image FLAG = new Image("res/flag.png");
	private static final Image BADFLAG = new Image("res/flag.png");
	
	private static final Image MINE = new Image("res/mine.png");
	private static final Image MINECLICKED = new Image("res/mineclicked.png");
	
	private static final Image BACKGROUND = new Image("res/background.png");
	private static final Image BORDEREDBACKGROUND = new Image("res/borderedbackground.png");
	
	private Button tileButton;
	private Image number;
	private Image background;

	
	public interface TileListener{
		public void setFaceToSmile();
		public void setFaceToSurprised();
		public void setFaceToSunglasses();
		public void setFaceToDead();
	}
}
