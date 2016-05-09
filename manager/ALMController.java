package audio.library.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ALMController {

	private AudioLibraryManager manager;

	public ALMController(AudioLibraryManager alm) {
		this.manager = alm;
	}

	public void meniu() {
		System.out.print("introduceti 'pwd', 'cd', 'list', 'play', 'info', 'find', 'fav', 'report' sau 'quit':");
	}

	/**
	 * Metoda care realizeaza operatiile si interactiunea cu utilizatorul
	 * 
	 * @throws NullArgumentException
	 * @throws InvalidCommandException
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public void control() throws NullArgumentException, InvalidCommandException, IOException {
		Scanner scan = new Scanner(System.in);
		String[] instr = null;
		String linie = "";
		String argument = "";
		String comanda = "";
		while (true) {
			meniu();
			linie = scan.nextLine();
			instr = linie.split(" ");
			comanda = instr[0];

			if (comanda.equalsIgnoreCase("quit")) {
				System.out.println("Exiting...");
				scan.close();
				break;
			}

			if (comanda.equalsIgnoreCase("pwd")) {
				System.out.println("Director curent: " + manager.getActiveDirectory());
			}

			for (String s : instr) {
				if (s == instr[0])
					continue;
				argument += s + " ";
			}

			if (comanda.equalsIgnoreCase("cd")) {
				if (argument.equalsIgnoreCase("")) {
					throw new NullArgumentException("Argument invalid! Sintaxa: cd <path>");
				} else {
					if (argument.equalsIgnoreCase(". ")) {
						System.out.println("Sunteti deja aici.  (" + manager.getActiveDirectory() + ")");
					}
					if (argument.equalsIgnoreCase(".. ")) {
						System.out.println("Rolling back...");
						this.manager.cd(manager.start);
					} else {
						argument = argument.substring(0, argument.length() - 1);
						if (argument.matches("[A-Za-z]:/")) {
							this.manager.cd(argument);
							argument = "";
						} else {
							this.manager.cd(manager.getActiveDirectory() + "/" + argument);
							argument = "";
						}
					}
				}
				argument = "";
			}

			if (comanda.equalsIgnoreCase("list")) {
				if (argument.equalsIgnoreCase(""))
					throw new NullArgumentException("Argument invalid! Sintaxa: list <path>");

				if (argument.equalsIgnoreCase(". "))
					try {
						this.manager.list(".");
					} catch (DirectoryNotFoundException e) {
						System.out.println(e.getMessage());
						this.manager.cd("E:/Muzicã");
						continue;
					} catch (NullPointerException e) {
						System.out.println(e.getMessage());
						this.manager.cd("E:/Muzicã");
						continue;
					}
				else {
					try {
						argument = argument.substring(0, argument.length() - 1);
						this.manager.list(argument);
					} catch (DirectoryNotFoundException e) {
						System.out.println(e.getMessage());
					}
				}
				argument = "";
			}

			if (comanda.equalsIgnoreCase("play")) {
				argument = argument.substring(0, argument.length() - 1);
				this.manager.play(argument);
				argument = "";
			}

			if (comanda.equalsIgnoreCase("info")) {
				argument = argument.substring(0, argument.length() - 1);
				this.manager.info(argument);
				argument = "";
			}

			if (comanda.equalsIgnoreCase("fav")) {
				argument = argument.substring(0, argument.length() - 1);
				this.manager.fav(argument);
				argument = "";
			}

			if (comanda.equalsIgnoreCase("find")) {
				ArrayList<File> foundSongs = new ArrayList<>();
				argument = argument.substring(0, argument.length() - 1);
				this.manager.find(argument, new File(this.manager.getActiveDirectory()), foundSongs);
				argument = "";
				for (File song : foundSongs) {
					System.out.println("\t[" + song.getAbsolutePath() + "]");
				}
			}

			if (comanda.equalsIgnoreCase("report")) {
				this.manager.report();
			}

			if (!comanda.equalsIgnoreCase("pwd") && !comanda.equalsIgnoreCase("cd") && !comanda.equalsIgnoreCase("list")
					&& !comanda.equalsIgnoreCase("play") && !comanda.equalsIgnoreCase("info")
					&& !comanda.equalsIgnoreCase("find") && !comanda.equalsIgnoreCase("fav")
					&& !comanda.equalsIgnoreCase("report") && !comanda.equalsIgnoreCase("quit")) {
				throw new InvalidCommandException("Comanda introdusa nu este valida!");
			}

		}
	}
}
