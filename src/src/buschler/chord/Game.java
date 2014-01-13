package buschler.chord;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Game {

	private static Game game;

	private Game() {
	}

	public static Game getInstance() {
		if (game == null) {
			game = new Game();
		}
		return game;
	}

	public static void main(String[] args) {
		boolean run = true;
		Scanner input = new Scanner(System.in);
		PropertiesLoader.loadPropertyFile();
		List<Player> player = new ArrayList<Player>();
		String ip = "141.22.26.43";
		String port = "7081";
		String bootstrapIp = "141.22.26.192";
		String bootstrapPort = "8080";

		while (run) {

			if (input.hasNext("exit")) {
				input.next();
				run = !run;
				System.out.println("Good Bye!");
				
			} else if (input.hasNext("add")) {
				input.next();
					System.out.print("PORT: ");
					if (input.hasNext(Pattern.compile("[1-9][0-9][0-9][0-9]"))) {
						port = input.next();
						Player tmpPlayer = new Player();
						ChordImpl tmpNode = Chord.addChord(ip, port,
								bootstrapIp, bootstrapPort, tmpPlayer);
						tmpPlayer.setNode(tmpNode);
						player.add(tmpPlayer);
						tmpPlayer.initFleets();
						System.out.println("Added Chord " + tmpNode.getID());
					} else {
						input.next();
						System.out.println("No valid Port!");
					}

			} else if (input.hasNext("init")) {
				input.next();
				Player tmpPlayer = new Player();
				ChordImpl tmpNode = StartChord.init(bootstrapIp, bootstrapPort,
						tmpPlayer);
				tmpPlayer.setNode(tmpNode);
				player.add(tmpPlayer);
				System.out.println("Added Chord " + tmpNode.getID());
			} else if (input.hasNext("bootstrap")) {
				input.next();
				System.out.print("IP: ");
				if (input.hasNext(Pattern
						.compile("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
								+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
								+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
								+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])"))) {
					bootstrapIp = input.next();
					System.out.print("PORT: ");
					if (input.hasNext(Pattern.compile("[1-9][0-9][0-9][0-9]"))) {
						bootstrapPort = input.next();
						System.out
								.println("Set bootstrap node for future nodes to "
										+ bootstrapIp + ":" + bootstrapPort);
					} else {
						input.next();
						System.out.println("No valid Port!");
					}
				} else {
					input.next();
					System.out.println("No valid IP!");
				}

			} else if (input.hasNext("add2")) {
				input.next();

				for (int i = 0; i < 1; i++) {
					String tmpPort = (2313 + i) + "";
					System.out.print("|");
					Player tmpPlayer = new Player();
					tmpPlayer.setNode(Chord.addChord(ip, tmpPort, bootstrapIp,
							bootstrapPort, tmpPlayer));
					player.add(tmpPlayer);
				}
				System.out.println("!");

			} else if (input.hasNext("demo")) {
				input.next();
				Player tmpPlayer = new Player();
				ChordImpl tmpNode = StartChord.init(bootstrapIp, bootstrapPort,
						tmpPlayer);
				tmpPlayer.setNode(tmpNode);
				player.add(tmpPlayer);

				for (int i = 0; i < 4; i++) {
					String tmpPort = (2313 + i) + "";
					System.out.print("|");
					tmpPlayer = new Player();
					tmpNode = Chord.addChord(ip, tmpPort, bootstrapIp,
							bootstrapPort, tmpPlayer);
					tmpPlayer.setNode(tmpNode);
					player.add(tmpPlayer);
				}
				System.out.println("!");

				for (Player player2 : player) {
					player2.initFleets();

				}

				Thread t1 = new Thread(new Runna(player.get(0)));
				t1.run();

			} else if (input.hasNext("u")) {
				input.next();
				
				System.out.println("Fleet "
						+ player.get(0).getMightyArmada().getIdEnd() + " has "
						+ player.get(0).getMightyArmada().getNumberOfHits()
						+ " hits and "
						+ player.get(0).getMightyArmada().getNumberOfMisses()
						+ " misses!");
				for (Fleet fleet : player.get(0).getOcean()) {
					System.out.println("Fleet "
							+ fleet.getIdEnd() + " has "
							+ fleet.getNumberOfHits()
							+ " hits and "
							+ fleet.getNumberOfMisses()
							+ " misses!");
				}
				
			} else if (input.hasNext("los")) {
				input.next();
				
				player.get(0).initFleets();
				
				player.get(0).fire();
			} else {
				input.next();
			}

		}

		input.close();

	}
	// private static void initFleet(List<Player> player) {
	//
	// }

}
