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

	}

	@Override
	public void initFleets() {
		int i = 0;
		super.initFleets();

		System.out.println();
		System.out.println("vvvvvvvvvv initFleets vvvvvvvvvv");
		System.out.println("Anzahl Flotten    : " + (ocean.size() + 1));
		System.out.println("My Fleet: " + mightyArmada.getIdStart() + " -> "
				+ mightyArmada.getIdEnd());
		for (Fleet fleet : ocean) {
			System.out.println("Fleet[" + ++i + "]: " + fleet.getIdStart()
					+ " -> " + fleet.getIdEnd());
		}
		System.out.println("^^^^^^^^^^ initFleets ^^^^^^^^^^");
		System.out.println();

	}

	@Override
	public void fire() {
		System.out.println("vvvvvvvvvv fire vvvvvvvvvv");
		System.out.println("Node " + getNode().getID() + " is firing!");
		System.out.println("^^^^^^^^^^ fire ^^^^^^^^^^");
		super.fire();
	}

	@Override
	protected void newEnemyFleetDetected(ID newRabbit) {
		super.newEnemyFleetDetected(newRabbit);

		System.out.println("vvvvvvvvvv newEnemyFleetDetected vvvvvvvvvv");
		System.out.println(mightyArmada.getIdEnd());
		System.out.println(newRabbit);
		System.out.println(mightyArmada.getIdStart() + " -> "
				+ mightyArmada.getIdEnd());
		for (Fleet f : ocean) {
			System.out.println(f.getIdStart() + " -> " + f.getIdEnd());
		}
		System.out.println("^^^^^^^^^^ newEnemyFleetDetected ^^^^^^^^^^");
	}

}
