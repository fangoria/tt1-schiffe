package buschler.chord;

import de.uniba.wiai.lspi.chord.data.ID;

public class NotifyCallbackImpl implements
		de.uniba.wiai.lspi.chord.service.NotifyCallback {

	private Game game;
	
	public NotifyCallbackImpl(Game game) {
		this.game = game;
	}
	
	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		// TODO Auto-generated method stub
		System.out.println("broadcast");
	}

	@Override
	public void retrieved(ID target) {
		// TODO Auto-generated method stub
		System.out.println("retrieve: " + target);
		
	}

}
