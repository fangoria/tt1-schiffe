package buschler.chord;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Main {

	static final String URL1 = "ocsocket://localhost:4245/";
	private static final int NODECOUNT = 10;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesLoader.loadPropertyFile();
		byte[] Z = new byte[20];
		Arrays.fill(Z, Byte.MIN_VALUE);

		ChordImpl[] ci = new ChordImpl[NODECOUNT];
		ci[0] = addChord(new ID(Z), (7081));
		System.out.print(0 + " ");
		System.out.println(new ID(Z));
		for (int i = 1; i < NODECOUNT; i++) {
			new Random().nextBytes(Z);
			ci[i] = addChord(new ID(Z), (i + 7081));
			System.out.print(i + " ");
			System.out.println(new ID(Z));
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
				} else if (s.equals("b")) {
					int b = Integer.parseInt(scan.next());
					ci[b].broadcast(ci[b].getID(), false);
				} else {
					break;
				}
				
			}		
		}
		scan.close();
		System.out.println("ende");
		return;
	}
	
	public static ChordImpl addChord(ID id, int port) {
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
			bootstrapURL = new URL(protocol + "://141.22.26.43:8080/");
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
