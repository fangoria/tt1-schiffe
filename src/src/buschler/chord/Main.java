package buschler.chord;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Scanner;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Main {

	static final String URL1 = "ocrmi://localhost:4245/";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ChordImpl chordA;

		NotifyCallback nc = new NotifyCallbackImpl();
		byte[] A = new byte[20];
		byte[] Z = new byte[20];

		Arrays.fill(A, Byte.MAX_VALUE);
		
		PropertiesLoader.loadPropertyFile();
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL localURL = null;
		try {
			localURL = new URL(protocol + "://localhost:8080/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		chordA = new ChordImpl();
		try {
			chordA.setCallback(nc);
			chordA.setID(new ID(A));
			chordA.create(localURL);
		} catch (ServiceException e) {
			throw new RuntimeException(" Could not create DHT ! ", e);
		}

		byte[] bla = new byte[20];
		Arrays.fill(bla, Byte.MIN_VALUE);
		bla[19] = Byte.MAX_VALUE;
		ID id = new ID(bla);

		ChordImpl[] ci = new ChordImpl[20];
		for (int i = 1; i < 20; i++) {
			System.out.print(i + " ");
			Arrays.fill(Z, Byte.MIN_VALUE);
			Z[i] = Byte.MAX_VALUE;
			ci[i] = addChord(new ID(Z), (i + 8081));

			if (i == 10) {
				ci[i].insert(id, "Tomate");
			}
		}
		
		System.out.println();
		
//		System.out.println(chordA.getPredecessorID());
		System.out.println(chordA.getFingerTable().size());
		System.out.println(chordA.retrieve(id));
		System.out.println(chordA.getFingerTable());
//		System.out.println(ci.retrieve(id));

		
		
//		 chordA.broadcast(new ID(Z), true);
//		 System.out.println("Broadcast 1");
//		 ci[10].broadcast(new ID(A), true);
//		 System.out.println("Broadcast 2");
//		 ci[12].broadcast(new ID(A), true);
//		 System.out.println("Broadcast 3");
		 
		
		Scanner scan = new Scanner(System.in);
		while (true) {
			
			if (scan.hasNext()) {
				scan.next();
				for (int i = 1; i < ci.length; i++) {
					System.out.print(ci[i].getFingerTable().size() + " ");
				}				
			}		
		}
	}
	
	public static ChordImpl addChord(ID id, int port) {
		ChordImpl chord = new ChordImpl(); 
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		NotifyCallback nc = new NotifyCallbackImpl();
		URL localURL = null;
		try {
			localURL = new URL(protocol + "://localhost:" + port + "/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		URL bootstrapURL = null;
		try {
			bootstrapURL = new URL(protocol + "://localhost:8080/");
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
