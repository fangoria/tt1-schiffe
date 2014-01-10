package buschler.chord;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uniba.wiai.lspi.chord.data.ID;

public class Fleet {
	private BigInteger idEnd;
	private BigInteger idStart;
	private BigInteger fieldSize;
	private static BigInteger idMax;
	private List<Shot> shots;
	private int numberOfHits;
	private int numberOfMisses;
	private int totalShots;
	private radar[] fleetDeployment;
	private int I; // Anzahl Felder
	private int S; // Anzahl Schiffe

	static {
		byte[] max = new byte[20];
		Arrays.fill(max, (byte) 255);
		idMax = new BigInteger(max);
	}

	enum radar {
		HIT, MISS, UNKNOWN;
	}

	public Fleet(ID idEnd, ID idStart, int I, int S) {
		this.I = I;
		this.setS(S);
		this.idEnd = idEnd.toBigInteger();
		this.idStart = idStart.toBigInteger().add(new BigInteger("1"));
		shots = new ArrayList<Shot>();
		fleetDeployment = new radar[I];
		Arrays.fill(fleetDeployment, radar.UNKNOWN);
		calculateFieldSize();
	}

	public Fleet(ID idEnd, ID idStart) {
		this(idEnd, idStart, 100, 10);
	}

	public BigInteger getIdEnd() {
		return idEnd;
	}

	public BigInteger getIdStart() {
		return idStart;
	}

	public void setIdStart(BigInteger idStart) {
		this.idStart = idStart;
	}

	public int getNumberOfHits() {
		return numberOfHits;
	}

	public int getNumberOfMisses() {
		return numberOfMisses;
	}

	public int getTotalShots() {
		return totalShots;
	}

	public radar getFleetDeployment(int id) {
		return fleetDeployment[id - 1];
	}

	public void setFleetDeployment(int id, radar state) {
		this.fleetDeployment[id - 1] = state;
	}

	public int getS() {
		return S;
	}

	public void setS(int s) {
		S = s;
	}

	public int getI() {
		return I;
	}

	public void setI(int i) {
		I = i;
	}

	public void registerShot(Shot shot) {
		shots.add(shot);
		totalShots++;
		if (shot.isHit()) {
			numberOfHits++;
		} else {
			numberOfMisses++;
		}
	}

	public boolean handleAttackOnMightyAmarda(ID target) {
		int field = calculateFieldFromID(target);
		if (fleetDeployment[field] == radar.HIT) {
			fleetDeployment[field] = radar.UNKNOWN;
			return true;
		} else {
			return false;
		}
	}

	public ID calculateIDFromField(Integer field) {
		BigInteger tmp = new BigInteger(fieldSize.toByteArray());
		BigInteger halfSize;
		if (field == this.I) {
			tmp.multiply(new BigInteger("" + (this.I - 1)));
			halfSize = calculateDistance().subtract(tmp).divide(new BigInteger("2"));
//			halfSize = new BigInteger(tmp.toByteArray()).divide(new BigInteger("2"));
			tmp.add(halfSize);			
		} else {
			tmp.multiply(new BigInteger(field.toString()));
			halfSize = new BigInteger(fieldSize.toByteArray());
			halfSize.divide(new BigInteger("2"));
			tmp.subtract(halfSize);
		}
		tmp.add(idStart);
		if (idMax.compareTo(tmp) == 1) {
			tmp.subtract(idMax);
		}
		return new ID(tmp.toByteArray());
	}

	private void calculateFieldSize() {
		fieldSize = calculateDistance().divide(new BigInteger("" + this.I));
	}

	private BigInteger calculateDistance() {
		BigInteger dist;
		if (idEnd.compareTo(idStart) == 1) { // ende größer
			dist = new BigInteger(idEnd.toString());
		} else { // start größer; wrap around
			dist = new BigInteger(idMax.toString());
			dist.add(idEnd);
		}
		return dist.subtract(idStart);
	}

	private void calculateFleetDeployment() {

	}

	private int calculateFieldFromID(ID id) {
		return 0;
	}

}
