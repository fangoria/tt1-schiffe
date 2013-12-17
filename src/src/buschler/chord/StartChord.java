package buschler.chord;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class StartChord {

	public static void main(String[] args) {
		ChordImpl chordA;

		NotifyCallback nc = new NotifyCallbackImpl();
		byte[] a = new byte[20];

		new Random().nextBytes(a);
		a[0] = 0;
		
		PropertiesLoader.loadPropertyFile();
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL localURL = null;
		try {
			localURL = new URL(protocol + "://141.22.26.43:7080/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		chordA = new ChordImpl();
		try {
			chordA.setCallback(nc);
			chordA.setID(new ID(a));
			chordA.create(localURL);
		} catch (ServiceException e) {
			throw new RuntimeException(" Could not create DHT ! ", e);
		}
		
		
System.out.println();
		
		Scanner scan = new Scanner(System.in);
		while (true) {
			
			if (scan.hasNext()) {
				String s = scan.next();
				if (s.equals("a")) {
					Node[] fingerTable = chordA.getFingerTable().toArray(new Node[chordA.getFingerTable().size()]);
					fingerTable = FingerTableSort.sort(fingerTable, chordA.getID());
					for (int i = 0; i < fingerTable.length; i++) {
						System.out.println(fingerTable[i].getNodeID());
					}								
				} else if (s.equals("b")) {
					chordA.broadcast(chordA.getID(), false);
				} else {
					break;
				}
				
			}		
		}
		scan.close();
		System.out.println("ende");
		return;
		

	}

}
