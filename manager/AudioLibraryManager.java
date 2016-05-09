package audio.library.manager;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.vfs2.FileNotFoundException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Clasa care permite administrarea de fisiere media
 * 
 */
public class AudioLibraryManager {

	private String activeDirectory = "";
	public String start = "E:/Muzicã";
	ArrayList<String> favorites = new ArrayList<>();

	public AudioLibraryManager(String activeDirectory) {
		this.activeDirectory = activeDirectory;
	}

	/**
	 * Seteaza directorul curent
	 * 
	 * @param directory:
	 *            noul director de lucru
	 * @throws DirectoryNonExistentException
	 * @throws NoMediaFilesException
	 */
	public void cd(String directory) {
		this.activeDirectory = directory;
		System.out.println("Director setat la '" + this.activeDirectory + "'");
	}

	/**
	 * Listeaza continutul unui folder/al folderului curent
	 * 
	 * @param path:
	 *            calea spre fisierele listate
	 * @throws DirectoryNotFoundException
	 * @throws IOException
	 */
	public void list(String path) throws DirectoryNotFoundException {
		if (path.equals(".")) {
			File dir = new File(activeDirectory);
			File[] listOfFiles = dir.listFiles();
			System.out.println("[" + activeDirectory + "]:");
			for (File f : listOfFiles) {
				if (f.getName().matches("(.*).mp3|(.*).flac|(.*).wav"))
					System.out.println("	" + f.getName());
				if (f.isDirectory()) {
					System.err.println(" Nu exista fisiere media in director.");
					break;
				}
			}
		} else {
			File currentDirectory = new File(path);
			if (currentDirectory.isDirectory()) {
				File[] listOfFiles = currentDirectory.listFiles();
				System.out.println("[" + currentDirectory + "]:");
				for (File file : listOfFiles) {
					if (file.getName().matches("(.*).mp3|(.*).flac|(.*).wav"))
						System.out.println(file.getName());
					if (file.isDirectory()) {
						System.err.println(" Nu exista fisiere media in director.");
						break;
					}
				}
			}
		}
	}

	/**
	 * Reda fisierul selectat cu programul implicit
	 * 
	 * @param filename:
	 *            numele fisierului
	 */
	public void play(String filename) throws FileNotFoundException {
		Desktop system = Desktop.getDesktop();
		File target = new File(activeDirectory + "/" + filename);
		try {
			system.open(target);
		} catch (IllegalArgumentException e) {
			System.err.println("[play '" + filename + "'] FAILED: Fisier inexistent in director.");
		} catch (FileNotFoundException e) {
			System.err.println("[play '" + filename + "'] FAILED: Fisier inexistent in director.");
		} catch (IOException e) {
			e.getMessage();
		}
	}

	/**
	 * Returneaza metadatele unui fisier
	 * 
	 * @return: string formatat
	 */
	public void info(String filename) {
		try {
			InputStream input = new FileInputStream(new File(this.activeDirectory + "/" + filename));
			DefaultHandler handler = new DefaultHandler();
			Metadata metadata = new Metadata();
			Mp3Parser parser = new Mp3Parser();
			ParseContext parseContext = new ParseContext();
			parser.parse(input, handler, metadata, parseContext);
			input.close();
			System.out.println("['" + filename + "']");
			System.out.println("----------------------------------------------");
			System.out.println("Titlu: " + metadata.get("title"));
			System.out.println("Artist: " + metadata.get("xmpDM:artist"));
			System.out.println("Compozitor: " + metadata.get("xmpDM:composer"));
			System.out.println("Gen: " + metadata.get("xmpDM:genre"));
			System.out.println("Album: " + metadata.get("xmpDM:album"));
			System.out.println("----------------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cauta recursiv in home folder apoi in subdirectoare fisiere care contin
	 * stringul dat ca parametru
	 * 
	 * @param URI
	 * @param start
	 * @param foundFiles
	 */
	public void find(String URI, File start, ArrayList<File> foundFiles) {
		if (start.isDirectory()) {
			for (File file : start.listFiles()) {
				find(URI, file, foundFiles);
			}
		} else if (start.isFile() && start.getName().matches("(.*)" + URI + "(.*)")) {
			foundFiles.add(start);
		}
	}

	/**
	 * Adauga un fisier al carui nume este dat ca parametru intr-o lista
	 * serializata de favorite
	 * 
	 * @param filename
	 */
	public void fav(String filename) {
		try {
			if (filename.equals(null)) {
				throw new FileNotFoundException("Nume fisier negasit.");
			} else {
				InputStream input = new FileInputStream(new File(this.activeDirectory + "/" + filename));
				DefaultHandler handler = new DefaultHandler();
				Metadata metadata = new Metadata();
				Mp3Parser parser = new Mp3Parser();
				ParseContext parseContext = new ParseContext();
				String finalResultOfFavorite = "";

				parser.parse(input, handler, metadata, parseContext);
				input.close();
				finalResultOfFavorite += "['" + filename + "']" + "\n";
				finalResultOfFavorite += "----------------------------------------------" + "\n";
				finalResultOfFavorite += "Titlu: " + metadata.get("title") + "\n";
				finalResultOfFavorite += "Artist: " + metadata.get("xmpDM:artist") + "\n";
				finalResultOfFavorite += "Compozitor: " + metadata.get("xmpDM:composer") + "\n";
				finalResultOfFavorite += "Gen: " + metadata.get("xmpDM:genre") + "\n";
				finalResultOfFavorite += "Album: " + metadata.get("xmpDM:album") + "\n";
				finalResultOfFavorite += "----------------------------------------------" + "\n";

				this.favorites.add(finalResultOfFavorite);
				System.out.println(finalResultOfFavorite);

				FileWriter writer = new FileWriter("favorites.txt", true);
				writer.write(finalResultOfFavorite + "\n\n");
				writer.close();

				System.out.println(filename + " adaugat la Favorites");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metoda care serializeaza fisierele din Favorites in format de fisier
	 * 
	 * @throws IOException
	 */
	public void report() throws IOException {
		FileWriter writer = new FileWriter("Favorite Songs.txt", true);
		System.out.println("Reporting...");
		writer.write("Favorite songs:" + "\n");
		for (String song : this.favorites) {
			writer.write(song + "\n\n");
		}
		writer.close();
		Desktop.getDesktop().open(new File("Favorite Songs.txt"));
		System.out.println("Done.");
	}

	/**
	 * Getter pentru calea curenta
	 * 
	 * @return: root directory al aplicatiei
	 */
	public String getActiveDirectory() {
		return activeDirectory;
	}

}
