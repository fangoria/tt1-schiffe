package buschler.chord;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Game {

	private static Game game;
	private static boolean debug = false;

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
		List<Player> player = new ArrayList<Player>();
		String ip = "141.22.26.43";
		String port = "7081";
		String bootstrapIp = "141.22.26.43";
		String bootstrapPort = "8080";
		PropertiesLoader.loadPropertyFile();

		debug = false;

		while (run) {

			/**
			 * ADD add new Player
			 */
			if (input.hasNext("add")) {
				input.next();
				System.out.print("PORT: ");
				if (input.hasNext(Pattern.compile("[1-9][0-9][0-9][0-9]"))) {
					port = input.next();
					addPlayer(player, ip, port, bootstrapIp, bootstrapPort,
							false);
				} else {
					input.next();
					System.out.println("No valid Port!");
				}

				/**
				 * INIT init Chord
				 */
			} else if (input.hasNext("init")) {
				input.next();
				addPlayer(player, ip, port, bootstrapIp, bootstrapPort, true);
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

			} else if (input.hasNext("info")) {
				input.next();

				System.out.println("Fleet "
						+ player.get(0).getMightyArmada().getIdEnd() + " has "
						+ player.get(0).getMightyArmada().getNumberOfHits()
						+ " hits and "
						+ player.get(0).getMightyArmada().getNumberOfMisses()
						+ " misses!");
				for (Fleet fleet : player.get(0).getOcean()) {
					System.out.println("Fleet " + fleet.getIdEnd() + " has "
							+ fleet.getNumberOfHits() + " hits and "
							+ fleet.getNumberOfMisses() + " misses!");
				}

			} else if (input.hasNext("fire")) {
				input.next();

				if (!player.isEmpty()) {
					player.get(0).fire();					
				}

			} else if (input.hasNext("initf")) {
				input.next();
				for (Player p : player) {
					p.initFleets();
				}
			} else {
				input.next();
				System.out.println("No valid command");
				System.out.println("Commands: ");
				System.out.println("          init");
				System.out.println("          initf");
				System.out.println("          add");
				System.out.println("          bootstrap");
				System.out.println("          fire");
				System.out.println("          info");
			}

		}

		input.close();

	}

	private static void addPlayer(List<Player> player, String ip, String port,
			String bootstrapIp, String bootstrapPort, boolean initPlayer) {
		Player armada;
		ChordImpl node;

		if (debug) {
			armada = new PlayerDebug();
		} else {
			armada = new Player();
		}

		if (initPlayer) {
			node = Chord.init(bootstrapIp, bootstrapPort, armada);
		} else {
			node = Chord.addChord(ip, port, bootstrapIp, bootstrapPort, armada);
		}

		armada.setNode(node);
		player.add(armada);
		System.out.println("Added Chord " + armada.getNode().getID());
	}

}
