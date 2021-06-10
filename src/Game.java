import javafx.stage.Stage;

public class Game {
	private Stage stage;
	
	public Game() {
		
	}
	
	public interface GamePlayer{
		public Stage getStage();
	}
}
