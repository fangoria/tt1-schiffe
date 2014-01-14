package buschler.chord;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uniba.wiai.lspi.chord.data.ID;

public class Fleet {
	protected ID fieldSize;
	private ID idEnd;
	private ID idStart;
	private ID idMax;
	private List<Shot> shots;
	private int numberOfHits;
	private int numberOfMisses;
	private int totalShots;
	private radar[] fleetDeployment;
	private int I; // Anzahl Felder
	private int S; // Anzahl Schiffe

	public enum radar {
		HIT, MISS, UNKNOWN;
	}

	/**
	 * 
	 * @param idEnd ID des eigenen Node
	 * @param idStart ID des Predecessors
	 * @param I Spielfelder
	 * @param S Schiffe
	 */
	public Fleet(ID idEnd, ID idStart, int I, int S) {
		byte[] max = new byte[20];
		Arrays.fill(max, (byte) 255);
		idMax = new ID(max);

		this.I = I;
		this.setS(S);
		this.idEnd = idEnd;
		this.idStart = idStart;
		shots = new ArrayList<Shot>();
		fleetDeployment = new radar[I];
		Arrays.fill(fleetDeployment, radar.UNKNOWN);
		calculateFieldSize();
	}

	/**
	 * 
	 * @param idEnd ID des eigenen Node
	 * @param idStart ID des Predecessors
	 */
	public Fleet(ID idEnd, ID idStart) {
		this(idEnd, idStart, 100, 10);
	}

	/**
	 * 
	 * @return ID des eigenen Node
	 */
	public ID getIdEnd() {
		return idEnd;
	}

	/**
	 * 
	 * @return ID des Predecessors
	 */
	public ID getIdStart() {
		return idStart;
	}

	/**
	 * 
	 * @param idStart ID des Predecessors
	 */
	public void setIdStart(ID idStart) {
		this.idStart = idStart;
	}

	/**
	 * 
	 * @return Anzahl an Treffern die diese Flotte erlitten hat
	 */
	public int getNumberOfHits() {
		return numberOfHits;
	}

	/**
	 * 
	 * @return Anzahl an Fehlversuchen auf diese Flotte
	 */
	public int getNumberOfMisses() {
		return numberOfMisses;
	}

	/**
	 * 
	 * @return Anzahl Versuche auf diese Flotte 
	 */
	public int getTotalShots() {
		return totalShots;
	}

	/**
	 * @param field Spielfeldposition
	 * @return Zustand des Feldes (HIT, MISS, UNKNOWN)
	 */
	public radar getFleetDeployment(int field) {
		return fleetDeployment[field - 1];
	}

	/**
	 * 
	 * @param field Spielfeldposition
	 * @param state Zustand des Feldes (HIT, MISS, UNKNOWN)
	 */
	public void setFleetDeployment(int field, radar state) {
		this.fleetDeployment[field - 1] = state;
	}

	/**
	 * 
	 * @return Anzahl Schiffe dieser Flotte 
	 */
	public int getS() {
		return S;
	}

	/**
	 * 
	 * @param s Anzahl Schiffe
	 */
	public void setS(int s) {
		S = s;
	}

	/**
	 * 
	 * @return Spielfeldgröße
	 */
	public int getI() {
		return I;
	}

	/**
	 * 
	 * @param i Spielfeldgröße
	 */
	public void setI(int i) {
		I = i;
	}

	/**
	 * 
	 */
	public void incrementNumberOfHits() {
		this.numberOfHits++;
	}

	/**
	 * 
	 */
	public void incrementNumberOfMisses() {
		this.numberOfMisses++;
	}

	/**
	 * 
	 * @param shot Schuss auf die Flotte 
	 */
	public void registerShot(Shot shot) {
		shots.add(shot);
	}

	/**
	 * 
	 */
	public void realignFields() {
		calculateFieldSize();
		fleetDeployment = new radar[this.I];
		Arrays.fill(fleetDeployment, radar.UNKNOWN);

		for (Shot shot : shots) {
			int field = calculateFieldFromID(shot.getTarget()) - 1;
			if (shot.isHit()) {
				if (fleetDeployment[field] != radar.HIT) {
					incrementNumberOfHits();
					setFleetDeployment(field, radar.HIT);
				}
			} else {
				if (fleetDeployment[field] != radar.HIT) {
					if (fleetDeployment[field] != radar.MISS) {
						incrementNumberOfMisses();
						setFleetDeployment(field, radar.MISS);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param target ID des Feldes auf das geschossen wurde
	 * @return HIT (<b>true</b>) oder MISS (<b>false</b>)
	 */
	public boolean handleAttackOnMightyAmarda(ID target) {
		int field = calculateFieldFromID(target) - 1;
		if (fleetDeployment[field] == radar.HIT) {
			fleetDeployment[field] = radar.UNKNOWN;
			numberOfHits++;
			return true;
		} else {
			numberOfMisses++;
			return false;
		}
	}

	/**
	 * 
	 * @param field Spielfeldposition
	 * @return ID der Spielfeldposition
	 */
	public ID calculateIDFromField(Integer field) {
		ID fieldID;
		BigInteger tmp = fieldSize.toBigInteger();
		BigInteger halfSize;
		if (field == this.I) {
			tmp = tmp.multiply(new BigInteger("" + (this.I - 1)));
			halfSize = calculateDistance().toBigInteger().subtract(tmp)
					.divide(new BigInteger("2"));
			tmp = tmp.add(halfSize);
		} else {
			tmp = tmp.multiply(new BigInteger(field.toString()));
			halfSize = fieldSize.toBigInteger();
			halfSize = halfSize.divide(new BigInteger("2"));
			tmp = tmp.subtract(halfSize);
		}

		fieldID = new ID((tmp.add(idStart.toBigInteger())).toByteArray());
		fieldID = normalizeID(fieldID);

		return fieldID;
	}

	/**
	 * 
	 * @param id ID der Spielfeldposition
	 * @return Spielfeldposition
	 */
	public int calculateFieldFromID(ID id) {
		ID tmp = id;
		int result;
		if (idStart.compareTo(id) == 1) {// wrap around
			tmp = new ID(
					(tmp.toBigInteger().add(idMax.toBigInteger())
							.subtract(idStart.toBigInteger())).toByteArray());
		} else {
			tmp = new ID(
					(tmp.toBigInteger().subtract(idStart.toBigInteger()))
							.toByteArray());
		}

		tmp = normalizeID(tmp);

		tmp = new ID(
				(tmp.toBigInteger().divide(fieldSize.toBigInteger()))
						.toByteArray());

		tmp = normalizeID(tmp);

		result = Integer.valueOf(tmp.toBigInteger().toString());
		if (result >= this.I) {
			result = this.I - 1;
		}

		return result + 1;
	}

	/**
	 * 
	 */
	public void calculateFieldSize() {
		fieldSize = new ID(
				(calculateDistance().toBigInteger().divide(new BigInteger(""
						+ this.I))).toByteArray());
		fieldSize = normalizeID(fieldSize);
	}

	/**
	 * 
	 * @return Distanz als ID
	 */
	protected ID calculateDistance() {
		ID dist;
		if (idEnd.compareTo(idStart) == 1) {
			dist = new ID(
					(idEnd.toBigInteger().subtract(idStart.toBigInteger()))
							.toByteArray());
		} else {
			dist = new ID(
					(idMax.toBigInteger().subtract(idStart.toBigInteger())
							.add(idEnd.toBigInteger())).toByteArray());

		}

		dist = normalizeID(dist);
		return dist;
	}

	/**
	 * 
	 * @param id ID
	 * @return Normalisierte ID (20 byte)
	 */
	private ID normalizeID(ID id) {
		byte[] tmp = new byte[20];

		if (id.getLength() < 160) {
			System.arraycopy(id.getBytes(), 0, tmp,
					(20 - id.getBytes().length), id.getBytes().length);
		} else if (id.getLength() > 160) {
			System.arraycopy(id.getBytes(), 1, tmp, 0, tmp.length);
		} else {
			tmp = id.getBytes();
		}

		return new ID(tmp);
	}

}
