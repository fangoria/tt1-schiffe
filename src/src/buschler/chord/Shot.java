package buschler.chord;

import de.uniba.wiai.lspi.chord.data.ID;

public class Shot {

	private ID target;
	private boolean hit;
	
	public Shot(ID target, boolean hit) {		
		this.setTarget(target);
		this.setHit(hit);
	}

	public boolean isHit() {
		return hit;
	}

	public void setHit(boolean hit) {
		this.hit = hit;
	}

	public ID getTarget() {
		return target;
	}

	public void setTarget(ID target) {
		this.target = target;
	}
	
	
	
}
