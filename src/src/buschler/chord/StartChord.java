package buschler.chord;

import java.net.MalformedURLException;
import java.util.Arrays;

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
		byte[] A = new byte[20];

		Arrays.fill(A, Byte.MAX_VALUE);
		
		PropertiesLoader.loadPropertyFile();
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL localURL = null;
		try {
			localURL = new URL(protocol + "://141.22.26.43:8080/");
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

	}

}
