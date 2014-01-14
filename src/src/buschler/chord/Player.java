package buschler.chord;

import java.util.ArrayList;
import java.util.List;

import buschler.chord.Fleet.radar;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Player implements NotifyCallback {

	protected List<Fleet> ocean;
	protected Fleet mightyArmada;
	protected ChordImpl node;
	protected boolean shooted;

	public Player() {
		shooted = false;
	}

	@Override
	public void retrieved(ID target) {
		node.broadcast(target, handleAttack(target));
		node.setTransactionID(node.getTransactionID() + 1);
		if (!victory()) {
			fire();
		} else {
			System.err.println("Jemand hat gewonnen! Bei TransactionID: "
					+ (node.getTransactionID() - 1));

		}
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		if (!checkParticipant(source)) {
			newEnemyFleetDetected(source);
		}
		handleAttackOnEnemy(source, target, hit);
		if (shooted) {
			if (victory()) {

				System.out.println();

				System.out
						.println("         _,-._                                               			\n"
								+ "        ; ___ :           ,------------------------------.  			\n"
								+ "    ,--' (. .) '--.__    |     Arrrgh! Piraten HoHo!      | 			\n"
								+ "  _;      |||        |   |    Wir haben die Landratten    | 			\n"
								+ " '._,-----''';=.____,'   |   über die Planke geschickt!   | 			\n"
								+ "   /// < o>   |##|       |      HArrRr HARrr HArRrr!      | 			\n"
								+ "   (o        \\`--'       //`-----------------------------' 			\n"
								+ "  ///\\ >>>>  _\\ <<<<    // 							   			\n"
								+ " --._>>>>>>>><<<<<<<<  /  								   			\n"
								+ " ___() >>>[||||]<<<< 									   			\n"
								+ " `--'>>>>>>>><<<<<<< 									   			\n"
								+ "      >>>>>>><<<<<< 										   			\n"
								+ "        >>>>><<<<< 										   			\n"
								+ "          >>><<<			                  .  ;  ; .		   			\n"
								+ "							                   '  .. '                 	\n"
								+ "							     _|_          =- {  } -=       _|_     	\n"
								+ "							    ``|`           .  '' .         `|``    	\n"
								+ "							   ```|``         '  ;  ; '       ``|```   	\n"
								+ "							   `__!__    )'             '(    __!__`   	\n"
								+ "							   :     := },;             ;,{ =:     :   	\n"
								+ "							   '.   .'                       '.   .'   	\n"
								+ "							 ~-=~~-=~~-=~~-=~~-=~~-=~~-=~~-=~~-=~~-=~~ 	\n");

				System.out.println("We have gewonnen! "
						+ mightyArmada.getIdEnd() + " bei TransactionID "
						+ (node.getTransactionID() - 1));
			}
			shooted = false;
		}
	}

	/**
	 * 
	 * @return Die eigene Flotte
	 */
	public Fleet getMightyArmada() {
		return mightyArmada;
	}

	/**
	 * 
	 * @return Alle Gegner
	 */
	public List<Fleet> getOcean() {
		return ocean;
	}

	/**
	 * 
	 * @return Den Node des Spielers
	 */
	public ChordImpl getNode() {
		return node;
	}

	/**
	 * 
	 * @param node
	 */
	public void setNode(ChordImpl node) {
		this.node = node;
	}

	/**
	 * Initialisiert die Flotte des Spielers und die gegnerischen Flotten.
	 */
	public void initFleets() {
		mightyArmada = new Fleet(node.getID(), node.getPredecessorID());
		ocean = new ArrayList<Fleet>();
		int i = 0;

		// Set 10 ships to random fields
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
			ocean.add(new Fleet(endNode, startNode));
			startNode = endNode;
		}
		if (!checkParticipant(node.getPredecessorID())) {
			ocean.add(new Fleet(node.getPredecessorID(), startNode));
		}

	}

	/**
	 * 
	 * @param id
	 * @return Kennt der Spieler die ID bereits, gibt die Funktion <b>true</b>
	 *         zurück, andernfalls <b>falls</b>.
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

	/**
	 * 
	 * @param newRabbit
	 */
	protected void newEnemyFleetDetected(ID newRabbit) {
		List<Fleet> tmpOcean = new ArrayList<Fleet>();
		tmpOcean.addAll(ocean);
		tmpOcean.add(mightyArmada);
		Fleet start = null;
		Fleet end = null;

		for (int i = 0; i < tmpOcean.size(); i++) {
			if (tmpOcean.get(i).getIdEnd().compareTo(newRabbit) == 1) {
				if (end == null
						|| tmpOcean.get(i).getIdEnd().compareTo(end.getIdEnd()) == -1) {
					end = tmpOcean.get(i);
				}
			} else if (tmpOcean.get(i).getIdEnd().compareTo(newRabbit) == -1) {
				if (start == null
						|| tmpOcean.get(i).getIdEnd()
								.compareTo(start.getIdEnd()) == 1) {
					start = tmpOcean.get(i);
				}
			}

		}

		if (end == null) {
			for (Fleet fleet : tmpOcean) {
				if (end == null
						|| fleet.getIdEnd().compareTo(end.getIdEnd()) == -1) {
					end = fleet;
				}
			}
		}

		if (start == null) {
			for (Fleet fleet : tmpOcean) {
				if (start == null
						|| fleet.getIdEnd().compareTo(start.getIdEnd()) == 1) {
					start = fleet;
				}
			}
		}

		Fleet fleet = new Fleet(newRabbit, start.getIdEnd());
		ocean.add(fleet);
		end.setIdStart(newRabbit);
		end.realignFields();
	}

	/**
	 * 
	 * @param target
	 * @return Gibt zurück ob es einen Treffer auf die eigene Flotte gab
	 *         (<b>true</b>) oder nicht (<b>false</b>).
	 */
	private boolean handleAttack(ID target) {
		return mightyArmada.handleAttackOnMightyAmarda(target);
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @param hit
	 */
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

	/**
	 * 
	 */
	public void fire() {
		Fleet target = null;
		ID field = null;
		for (Fleet rabbitFleet : ocean) {
			if (target == null
					|| target.getNumberOfHits() < rabbitFleet.getNumberOfHits()) {
				target = rabbitFleet;
			}
		}

		int i;
		for (i = 1; i <= target.getI(); i++) {
			if (target.getFleetDeployment(i) == radar.UNKNOWN) {
				field = target.calculateIDFromField(i);
				break;
			}
		}
		shooted = true;
		node.retrieve(field);
	}

	/**
	 * 
	 * @return Gibt zurück, ob das Spiel zu Ende ist (<b>true</b>) oder nicht
	 *         (<b>false</b>).
	 */
	protected boolean victory() {
		boolean isEnd = false;

		for (Fleet fleet : ocean) {
			if (fleet.getNumberOfHits() >= fleet.getS()) {
				isEnd = true;
			}
		}

		if (!isEnd && mightyArmada.getNumberOfHits() >= mightyArmada.getS()) {
			isEnd = true;
		}

		return isEnd;
	}

}
