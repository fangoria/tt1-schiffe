package buschler.chord;

import java.util.ArrayList;
import java.util.List;

import buschler.chord.Fleet.radar;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Player implements NotifyCallback {

	private ChordImpl node;
	private List<Fleet> ocean;
	private Fleet mightyArmada;

	private static int COUNT = 0;
	
	public Player() {

	}

	@Override
	public void retrieved(ID target) {
		COUNT++;
//		System.out.println(target);
//		System.out.println(node);
		// node.broadcast(target, false);
		System.out.println("vvvvvvvvvv retrieved vvvvvvvvvv");
		System.out.println("COUNT = " + COUNT);
		System.out.println("Someone is shooting at me on field " + target);
		System.out.println("He has " + ocean.get(0).getNumberOfHits() + " hits");
		System.out.println("^^^^^^^^^^ retrieved ^^^^^^^^^^");
		node.broadcast(target, handleAttack(target));

		if (!victory()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fire();
		} else {
			System.err.println("Jemand hat gewonnen!");
			for (int i = 0; i < ocean.size(); i++) {
				System.out.println("Fleet[" + i + "] has " + ocean.get(i).getNumberOfHits() + " hits and "
						+ ocean.get(i).getNumberOfMisses() + " misses.");
			}
		}
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		System.out.println("souce: " + source + "   target: " + target + "   hit: " + hit);
		handleAttackOnEnemy(source, target, hit);
	}

	public ChordImpl getNode() {
		return node;
	}

	public void setNode(ChordImpl node) {
		this.node = node;
	}

	public void initFleets() {
		mightyArmada = new Fleet(node.getID(), node.getPredecessorID());
		ocean = new ArrayList<Fleet>();

		// Set ship to first field, only for debug
		mightyArmada.setFleetDeployment(1, radar.HIT);

		// Set 10 ships to random fields
		int i = 0;
		while (i < mightyArmada.getS()) {
			int rnd = (int) (Math.random() * mightyArmada.getI()) + 1;
			if (mightyArmada.getFleetDeployment(rnd) == radar.UNKNOWN) {
				mightyArmada.setFleetDeployment(rnd, radar.HIT);
				i++;
			}
		}

		Node[] fingerTable = node.getFingerTable().toArray(new Node[node.getFingerTable().size()]);
		fingerTable = FingerTableSort.sort(fingerTable, node.getID());
		ID startNode = node.getID();
		ID endNode;
		for (i = 0; i < fingerTable.length; i++) {
			endNode = fingerTable[i].getNodeID();
			ocean.add(new Fleet(endNode, startNode));
			startNode = endNode;
		}
		if (!checkParticipant(node.getPredecessorID())) {
			ocean.add(new Fleet(node.getPredecessorID(), startNode));
		}

		// Debug
		System.out.println();
		System.out.println("vvvvvvvvvv initFleets vvvvvvvvvv");
		System.out.println("Anzahl Fingertable: " + fingerTable.length);
		System.out.println("Anzahl Flotten    : " + (ocean.size() + 1));
		for (Fleet fleet : ocean) {
			System.out.println("Fleet[" + i + "]: " + fleet.getIdEnd());
		}
		System.out.println("^^^^^^^^^^ initFleets ^^^^^^^^^^");
		System.out.println();

	}

	private boolean checkParticipant(ID id) {
		boolean result = false;
		for (int i = 0; i < ocean.size() && result != true; i++) {
			// result = (ocean.get(i).getIdEnd().compareTo(id.toBigInteger()) ==
			// 0);
			result = (ocean.get(i).getIdEnd().compareTo(id) == 0);
		}
		return result;
	}

	private void newEnemyFleetDetected(ID newRabbit) {
		// NOT YET
	}

	private boolean handleAttack(ID target) {
		return mightyArmada.handleAttackOnMightyAmarda(target);
	}

	private void handleAttackOnEnemy(ID source, ID target, Boolean hit) {
		for (int i = 0; i < ocean.size(); i++) {
			if (ocean.get(i).getIdEnd().compareTo(source) == 0) {
				int field = ocean.get(i).calculateFieldFromID(target);
				if (hit) {
					if (ocean.get(i).getFleetDeployment(field) != radar.HIT) {
						ocean.get(i).incrementNumberOfHits();
						ocean.get(i).setFleetDeployment(field, radar.HIT);
					}
				} else {
					if (ocean.get(i).getFleetDeployment(field) != radar.HIT) {
						if (ocean.get(i).getFleetDeployment(field) != radar.MISS) {
							ocean.get(i).incrementNumberOfMisses();
							ocean.get(i).setFleetDeployment(field, radar.MISS);
						}
					}
				}
			}
		}
	}

	public void fire() {
		Fleet target = null;
		for (Fleet rabbitFleet : ocean) {
			if (target == null || target.getNumberOfHits() < rabbitFleet.getNumberOfHits()) {
				target = rabbitFleet;
			}
		}

		for (int i = 1; i <= target.getI(); i++) {
			if (target.getFleetDeployment(i) == radar.UNKNOWN) {
				node.retrieve(target.calculateIDFromField(i));
				System.out.println("vvvvvvvvvv fire vvvvvvvvvv");
				System.out.println("Node                " + getNode().getID());
				System.out.println("is shooting at Node " + target.calculateIDFromField(i));
				System.out.println("to field            " + target.calculateIDFromField(i) + " [" + i + "]");
				System.out.println("noh: " + target.getNumberOfHits());
				System.out.println("^^^^^^^^^^ fire ^^^^^^^^^^");
				break;
			}
		}
	}

	private boolean victory() {
		boolean isEnd = false;

		for (Fleet fleet : ocean) {
			if (fleet.getNumberOfHits() == 10) {
				isEnd = true;
			}
		}

		return isEnd;
	}
}
