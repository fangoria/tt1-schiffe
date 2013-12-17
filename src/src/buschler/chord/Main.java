package buschler.chord;

import java.net.MalformedURLException;
import java.util.Random;
import java.util.Scanner;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Main {

	static final String URL1 = "ocsocket://localhost:4245/";
	private static final int NODECOUNT = 8;

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		PropertiesLoader.loadPropertyFile();
		byte[] z = new byte[20];

		ChordImpl[] ci = new ChordImpl[NODECOUNT];
		for (int i = 1; i < NODECOUNT; i++) {
			new Random().nextBytes(z);
//			z[0] = (byte)(i * 10);
			ci[i] = addChord(new ID(z), (i + 7080), (i - 1 + 7080));
			System.out.println(new ID(z));
		}
		
		System.out.println();
		
		Scanner scan = new Scanner(System.in);
		while (true) {
			
			if (scan.hasNext()) {
				String s = scan.next();
				if (s.equals("f")) {
					int y = 0;
					for (int i = 1; i < ci.length; i++) {
						y += ci[i].getFingerTable().size();
					}									
					System.out.print(y);
				} else if (s.equals("a")) {
						int b = Integer.parseInt(scan.next());
						if (b < ci.length && b > 0) {
							Node[] fingerTable = ci[b].getFingerTable().toArray(new Node[ci[b].getFingerTable().size()]);
							fingerTable = FingerTableSort.sort(fingerTable, ci[b].getID());
							for (int i = 0; i < fingerTable.length; i++) {
								System.out.println(fingerTable[i].getNodeID());
							}							
						}
				} else if (s.equals("b")) {
				
					int b = Integer.parseInt(scan.next());
					if (b < ci.length && b > 0) {
						ci[b].broadcast(ci[b].getID(), false);
					}
				} else {
					break;
				}
				
			}		
		}
		scan.close();
		System.out.println("ende");
		return;
	}
	
	public static ChordImpl addChord(ID id, int port, int bootPort) {
		ChordImpl chord = new ChordImpl(); 
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		NotifyCallback nc = new NotifyCallbackImpl();
		URL localURL = null;
		try {
			localURL = new URL(protocol + "://141.22.26.43:" + port + "/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		URL bootstrapURL = null;
		try {
			bootstrapURL = new URL(protocol + "://141.22.26.43:" + bootPort + "/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		chord = new ChordImpl();
		try {
			chord.setCallback(nc);
			chord.setID(id);
			chord.join(localURL, bootstrapURL);
		} catch (ServiceException e) {
			throw new RuntimeException(" Could not join DHT ! ", e);
		}
		
		return chord;
		
	}

}
