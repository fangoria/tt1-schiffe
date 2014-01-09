package buschler.chord;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Player implements NotifyCallback {

	private ChordImpl node;
	
	public Player() {

	}
	
	@Override
	public void retrieved(ID target) {
		System.out.println("Die Schweine!!! Sie haben Kenny getötet!!!!!!");
		System.out.println(target);
		System.out.println(node);
		node.broadcast(target, true);
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		// TODO Auto-generated method stub
		
	}

	public ChordImpl getNode() {
		return node;
	}

	public void setNode(ChordImpl node) {
		this.node = node;
	}

}
