package buschler.chord;

import java.net.MalformedURLException;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class StartChord {
 
	private static ChordImpl firstNode;
	
	//public static void main(String[] args) {
	public static ChordImpl init(String ip, String port, NotifyCallback cb) {
		
		if (firstNode == null) {
			
//			PropertiesLoader.loadPropertyFile();
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
		
		
//		Scanner scan = new Scanner(System.in);
//		while (true) {
//			
//			if (scan.hasNext()) {
//				String s = scan.next();
//				if (s.equals("a")) {
//					Node[] fingerTable = firstNode.getFingerTable().toArray(new Node[firstNode.getFingerTable().size()]);
//					fingerTable = FingerTableSort.sort(fingerTable, firstNode.getID());
//					for (int i = 0; i < fingerTable.length; i++) {
//						System.out.println(fingerTable[i].getNodeID());
//					}								
//				} else if (s.equals("b")) {
//					firstNode.broadcast(firstNode.getID(), false);
//				} else {
//					break;
//				}
//				
//			}		
//		}
//		scan.close();
//		System.out.println("ende");
		return firstNode;
	
	}

}
