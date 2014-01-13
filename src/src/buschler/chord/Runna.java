package buschler.chord;

public class Runna implements Runnable {

	private Player player;
	
	public Runna(Player player) {
		this.player = player;
	}
	
	@Override
	public void run() {
		this.player.fire();
	}

}
