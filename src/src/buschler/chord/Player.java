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
	private boolean shooted = false;
	
	@Override
	public void retrieved(ID target) {
		System.out.println("vvvvvvvvvv retrieved vvvvvvvvvv");
		System.out.println(getNode().getID());
		System.out.println("Someone is shooting at me on field " + target + " [" + mightyArmada.calculateFieldFromID(target) + "]");
//		System.out.println("I have " + mightyArmada.getNumberOfHits() + " hits and " + mightyArmada.getNumberOfMisses() + " misses.");
		for (Fleet fleet : ocean) {
			System.out.println("Fleet " + fleet.getIdEnd() + " has " + fleet.getNumberOfHits() + " hits.");
		}
		node.broadcast(target, handleAttack(target));		
		System.out.println("^^^^^^^^^^ retrieved ^^^^^^^^^^");		
		
		if (!victory()) {
//			try {
//				Thread.sleep(200);					
//				
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			fire();
		} else {
			System.err.println("Jemand hat gewonnen! Bei transactionID: " + node.getTransactionID());

		}
		
	}

	public Fleet getMightyArmada() {
		return mightyArmada;
	}
	
	public List<Fleet> getOcean() {
		return ocean;
	}
	
	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		if (!checkParticipant(source)) {
			newEnemyFleetDetected(source);
		}
		handleAttackOnEnemy(source, target, hit);
		if (shooted) {
			if (victory()) {
				System.out.println("We have gewonnen! " + mightyArmada.getIdEnd());
			}
			shooted = false;
		}
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
//		mightyArmada.setFleetDeployment(1, radar.HIT);

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
//		System.out.println();
//		System.out.println("vvvvvvvvvv initFleets vvvvvvvvvv");
//		System.out.println("Anzahl Fingertable: " + fingerTable.length);
//		System.out.println("Anzahl Flotten    : " + (ocean.size() + 1));
//		i = 0;
//		System.out.println("My Fleet: " + mightyArmada.getIdStart() + " -> " + mightyArmada.getIdEnd());
//		for (Fleet fleet : ocean) {
//			System.out.println("Fleet[" + ++i + "]: " + fleet.getIdStart() + " -> " + fleet.getIdEnd());
//		}
//		System.out.println("^^^^^^^^^^ initFleets ^^^^^^^^^^");
//		System.out.println();

	}

	/**
	 * 
	 * @param id
	 * @return true if he knows the id
	 */
	private boolean checkParticipant(ID id) {
		boolean result = false;
		for (Fleet fleet : ocean) {
			result = (fleet.getIdEnd().compareTo(id) == 0);
			if (result) {
				break;
			}
		}
		return result;
	}

	private void newEnemyFleetDetected(ID newRabbit) {
		List<Fleet> tmpOcean = new ArrayList<Fleet>();
		tmpOcean.addAll(ocean);
		tmpOcean.add(mightyArmada);
		
		Fleet start = null;
		Fleet end = null;
		
//		System.out.println("vvvvvvvvvv newEnemyFleetDetected vvvvvvvvvv");
//		System.out.println(mightyArmada.getIdEnd());
//		System.out.println(newRabbit);
		System.out.print("OCEAN: " + ocean.size());
		
		for (int i = 0; i < tmpOcean.size(); i++) {
			if (tmpOcean.get(i).getIdEnd().compareTo(newRabbit) == 1) {
				if (end == null || tmpOcean.get(i).getIdEnd().compareTo(end.getIdEnd()) == -1) {
					end = tmpOcean.get(i); 					
				}
			} else if (tmpOcean.get(i).getIdEnd().compareTo(newRabbit) == -1) {
				if (start == null || tmpOcean.get(i).getIdEnd().compareTo(start.getIdEnd()) == 1) {
					start = tmpOcean.get(i);					
				}
			}
			
		}
		
		if (end == null) {
			for (Fleet fleet : tmpOcean) {
				if (end == null || fleet.getIdEnd().compareTo(end.getIdEnd()) == -1) {
					end = fleet;
				}
			}
		}
		
		if (start == null) {
			for (Fleet fleet : tmpOcean) {
				if (start == null || fleet.getIdEnd().compareTo(start.getIdEnd()) == 1) {
					start = fleet;
				}
			}
		}

		Fleet fleet = new Fleet(newRabbit, start.getIdEnd());
		ocean.add(fleet);
		end.setIdStart(newRabbit);
		end.realignFields();
		
		
//		System.out.println(mightyArmada.getIdStart() + " -> " + mightyArmada.getIdEnd());
//		for (Fleet f : ocean) {
//			System.out.println(f.getIdStart() + " -> " + f.getIdEnd());
//		}
//		
//		if (start != null) System.out.println("Start: " + start.getIdEnd());
//		if (end != null) System.out.println("End: " + end.getIdEnd());
//		
	
		System.out.println(" -> OCEAN: " + ocean.size());
//		System.out.println("^^^^^^^^^^ newEnemyFleetDetected ^^^^^^^^^^");
		
		
	}

	private boolean handleAttack(ID target) {
		return mightyArmada.handleAttackOnMightyAmarda(target);
	}

	private void handleAttackOnEnemy(ID source, ID target, Boolean hit) {
		for (int i = 0; i < ocean.size(); i++) {
			if (ocean.get(i).getIdEnd().compareTo(source) == 0) {
				int field = ocean.get(i).calculateFieldFromID(target);
				ocean.get(i).registerShot(new Shot(target, hit));
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
		ID field = null;
		for (Fleet rabbitFleet : ocean) {
			if (target == null || target.getNumberOfHits() < rabbitFleet.getNumberOfHits()) {
				target = rabbitFleet;
			}
		}
		
		int i;
		for (i = 1; i <= target.getI(); i++) {
			if (target.getFleetDeployment(i) == radar.UNKNOWN) {
				target.setFleetDeployment(i, radar.MISS);
				field = target.calculateIDFromField(i);
				break;
			}
		}
		System.out.println("vvvvvvvvvv fire vvvvvvvvvv");
		System.out.println("Node                " + getNode().getID());
		System.out.println("is shooting at Node " + target.getIdEnd());
		System.out.println("to field            " + field + " [" + i + "]");
		System.out.println("noh: " + target.getNumberOfHits());
		System.out.println("^^^^^^^^^^ fire ^^^^^^^^^^");
		shooted = true;
		node.retrieve(field);
	}

	private boolean victory() {
		boolean isEnd = false;

		for (Fleet fleet : ocean) {
			if (fleet.getNumberOfHits() == 10) {
				isEnd = true;
			}
		}
		
		if (!isEnd && mightyArmada.getNumberOfHits() == 10) {
			isEnd = true;
		}

		return isEnd;
	}

}
