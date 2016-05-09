package audio.library.manager;

import java.io.IOException;
import org.apache.commons.vfs2.FileNotFoundException;

public class Test {

	public static void main(String[] args) throws NullArgumentException, InvalidCommandException, IOException {
		AudioLibraryManager manager = new AudioLibraryManager("E:/Muzicã");
		ALMController controller = new ALMController(manager);

		try {
			controller.control();
		} catch (NullArgumentException e) {
			System.err.println(e.getMessage());
			controller.control();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			controller.control();
		} catch (InvalidCommandException e) {
			controller.control();
		}
	}
}
