package buschler.chord;

import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		NotifyCallback nc = new NotifyCallbackImpl();
		ChordImpl chord = new ChordImpl();
		chord.setCallback(nc);
		
	}
}
