package buschler.chord;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import buschler.chord.Fleet.radar;
import de.uniba.wiai.lspi.chord.com.Broadcast;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Player implements NotifyCallback {

	private ChordImpl node;
	private List<Fleet> ocean;
	private Fleet mightyArmada;

	public Player() {

	}

	@Override
	public void retrieved(ID target) {
		System.out.println(target);
		System.out.println(node);
		System.out.println("angegriffenes feld: " + mightyArmada.calculateFieldFromID(target));
//		node.broadcast(target, false);
		node.broadcast(target, handleAttack(target));
		
		if (ocean.get(0).getNumberOfHits() < 4) {
//			fire();
			
		} else {
			victory();
		}
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		System.out.println("souce: " + source + "   target: " + target
				+ "   hit: " + hit);
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
		int i = 0;
		mightyArmada.setFleetDeployment(1, radar.HIT);
		while (i < mightyArmada.getS()) {
			int rnd = (int) (Math.random() * mightyArmada.getI()) + 1;
			if (mightyArmada.getFleetDeployment(rnd) == radar.UNKNOWN) {
				mightyArmada.setFleetDeployment(rnd, radar.HIT);
				i++;
			}
		}

		Node[] fingerTable = node.getFingerTable().toArray(
				new Node[node.getFingerTable().size()]);
		fingerTable = FingerTableSort.sort(fingerTable, node.getID());
		ID startNode = node.getID();
		ID endNode;
		for (i = 0; i < fingerTable.length; i++) {
			endNode = fingerTable[i].getNodeID();
			System.out.println("endid to save: " + endNode);
			ocean.add(new Fleet(endNode, startNode));
			startNode = endNode;
		}
		if (!checkParticipant(node.getPredecessorID())) {
			ocean.add(new Fleet(node.getPredecessorID(), startNode));
		}
		System.out.println("Fingertable: " + fingerTable.length);
		System.out.println("Anzahl Flotten: " + (ocean.size() + 1));
	}

	private boolean checkParticipant(ID id) {
		boolean result = false;
		for (int i = 0; i < ocean.size() && result != true; i++) {
			result = (ocean.get(i).getIdEnd().compareTo(id.toBigInteger()) == 0);
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
		int j = 0;
		System.out.println("ATTACK ON ENEMY!!!!!!!!!!");
//		if (checkParticipant(source)) {
//			newEnemyFleetDetected(source);
//		}
		for (int i = 0; i < ocean.size(); i++) {
			if (ocean.get(i).getIdEnd().compareTo(source.toBigInteger()) == 0) {
				int field = ocean.get(i).calculateFieldFromID(target);
				System.out.println("field:" + field );
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
			if (target == null
					|| target.getNumberOfHits() < rabbitFleet.getNumberOfHits()) {
				target = rabbitFleet;
				System.out.println("|---------------------------------------------------------|");
				System.out.println("I am             " + getNode().getID());
				System.out.println("I am shooting at " + new ID(target.getIdEnd().toByteArray()));
				System.out.println("noh: " + target.getNumberOfHits());
				System.out.println("|---------------------------------------------------------|");
			}
		}
		for (int i = 1; i <= target.getI(); i++) {
			if (target.getFleetDeployment(i) == radar.UNKNOWN) {
				node.retrieve(target.calculateIDFromField(i));
				break;
			}
		}
	}

	private void victory() {
		System.out.println("SIEEEEEEEEEEEEEGGGGGGG!!!!!!!!!!!!!!!!!!!!");
	}
}
