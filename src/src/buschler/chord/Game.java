package buschler.chord;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
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
		String ip = "127.0.0.1";
		String bIp = "127.0.0.1";
		String port = "7081";
		String bPort = "8080";
		
		while (run) {
						
			if (input.hasNext("exit")) {
				input.next();
				run = !run;
				System.out.println("Good Bye!");
			} else if (input.hasNext("add")) {
				input.next();
				System.out.print("IP: ");
				if (input.hasNext(Pattern.compile("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
												  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
												  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
												  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])"))) {
					ip = input.next();
					System.out.print("PORT: ");
					if (input.hasNext(Pattern.compile("[1-9][0-9][0-9][0-9]"))) {
						port = input.next();
						Player tmpPlayer = new Player();
						ChordImpl tmpNode = Chord.addChord(ip, port, bIp, bPort, tmpPlayer);
						tmpPlayer.setNode(tmpNode);
						player.add(tmpPlayer);
						System.out.println("Added Chord " + tmpNode.getID());
					} else {
						input.next();
						System.out.println("No valid Port!");
					}
				} else {
					input.next();
					System.out.println("No valid IP!");
				}
				
			} else if (input.hasNext("init")) {
				input.next();
				Player tmpPlayer = new Player();
				ChordImpl tmpNode = StartChord.init(bIp, bPort, tmpPlayer);	
				tmpPlayer.setNode(tmpNode);
				player.add(tmpPlayer);
			} else if (input.hasNext("bootstrap")) {
				input.next();
				System.out.print("IP: ");
				if (input.hasNext(Pattern.compile("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
												  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
												  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
												  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])"))) {
					bIp = input.next();
					System.out.print("PORT: ");
					if (input.hasNext(Pattern.compile("[1-9][0-9][0-9][0-9]"))) {
						bPort = input.next();
						System.out.println("Set bootstrap node for future nodes to " + bIp + ":" + bPort);
					} else {
						input.next();
						System.out.println("No valid Port!");
					}
				} else {
					input.next();
					System.out.println("No valid IP!");
				}
				
			} else if (input.hasNext("arrrgh!")) {
				Node[] fingerTable;
				StringBuffer buf;
				input.next();
				
				Player[] players = player.toArray(new Player[player.size()]);
				System.out.println("|---------------------------------------------------------------|");
				System.out.println("|------------------------Fingertables---------------------------|");
				System.out.println("|---------------------------------------------------------------|");
				for (Player p : players) {
					fingerTable = p.getNode().getFingerTable().toArray(new Node[p.getNode().getFingerTable().size()]);
					buf = new StringBuffer("| " + p.getNode().getID() + ": ");
					for (Node finger : fingerTable) {
						buf.append("[" + finger.getNodeID() + "] ");
					}
					System.out.println(buf.toString());
					System.out.println("|---------------------------------------------------------------|");
				}
				
			} else if (input.hasNext("demo")) {
				input.next();
				Player tmpPlayer = new Player();
				ChordImpl tmpNode = StartChord.init(bIp, bPort, tmpPlayer);	
				tmpPlayer.setNode(tmpNode);
				player.add(tmpPlayer);
				
				for (int i = 0; i < 5; i++) {
					String tmpPort = (2313 + i) + "";
					System.out.print("|");
					tmpPlayer = new Player();
					tmpNode = Chord.addChord(ip, tmpPort, bIp, bPort, tmpPlayer);
					tmpPlayer.setNode(tmpNode);
					player.add(tmpPlayer);
				}
				System.out.println("!");
			} else if (input.hasNext("start")) {
				input.next();
				if (!player.isEmpty()) {
//					player.get(0).getNode().getID()
					byte[] id = new byte[20];
					id[0] = (byte) 255;
					ID chordID = new ID(id);
//					ID chordID = ID.valueOf(player.get(0).getNode().getID().toBigInteger().add(new BigInteger("10000000000000000000000")));
					System.out.println(chordID);
					
					player.get(0).getNode().retrieve(chordID);
				}
			} else {
				input.next();
			}
			
		}

		input.close();
		
	}
	
	private static void initFleet(List<Player> player) {
		
	}

}
