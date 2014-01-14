package buschler.chord;

import de.uniba.wiai.lspi.chord.data.ID;

public class PlayerDebug extends Player {

	@Override
	public void retrieved(ID target) {
		System.out.println("vvvvvvvvvv retrieved vvvvvvvvvv");
		System.out.println(getNode().getID());
		System.out.println("Someone is shooting at me on field " + target
				+ " [" + mightyArmada.calculateFieldFromID(target) + "]");
		for (Fleet fleet : ocean) {
			System.out.println("Fleet " + fleet.getIdEnd() + " has "
					+ fleet.getNumberOfHits() + " hits.");
		}
		System.out.println("^^^^^^^^^^ retrieved ^^^^^^^^^^");

		super.retrieved(target);

		if (victory()) {
			System.err.println("Jemand hat gewonnen! Bei transactionID: "
					+ node.getTransactionID());

		}

	}

}
