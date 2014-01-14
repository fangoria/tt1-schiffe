package buschler.chord;

import java.net.MalformedURLException;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Chord {

	private static ChordImpl firstNode;

	public static ChordImpl addChord(String ip, String port, String bootIp,
			String bootPort, NotifyCallback cb) {
		ChordImpl chord = new ChordImpl();
		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		URL localURL = null;
		try {
			localURL = new URL(protocol + "://" + ip + ":" + port + "/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		URL bootstrapURL = null;
		try {
			bootstrapURL = new URL(protocol + "://" + bootIp + ":" + bootPort
					+ "/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		try {
			chord.setCallback(cb);
			chord.join(localURL, bootstrapURL);
		} catch (ServiceException e) {
			throw new RuntimeException(" Could not join DHT ! ", e);
		}

		return chord;

	}

	public static ChordImpl init(String ip, String port, NotifyCallback cb) {

		if (firstNode == null) {

			// PropertiesLoader.loadPropertyFile();
			String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
			URL localURL = null;
			try {
				localURL = new URL(protocol + "://" + ip + ":" + port + "/");
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
			firstNode = new ChordImpl();
			try {
				firstNode.setCallback(cb);
				firstNode.create(localURL);
			} catch (ServiceException e) {
				throw new RuntimeException(" Could not create DHT ! ", e);
			}
		}

		return firstNode;

	}

}
