import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javafx.scene.image.Image;

public class Res {
	
	public static Image image(String uri) {
		try {
			return new Image(Res.class.getResource("/res/" + uri).toURI().toString());
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
