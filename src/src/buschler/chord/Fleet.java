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

//	static {
//		byte[] max = new byte[20];
//		Arrays.fill(max, (byte) 255);
//		idMax = new BigInteger(max);
//		System.out.println("lalala");
//		System.out.println("big: " + idMax);
//		System.out.println("id:  " + new ID(idMax.toByteArray()));
//	}

	enum radar {
		HIT, MISS, UNKNOWN;
	}

	public Fleet(ID idEnd, ID idStart, int I, int S) {
		byte[] max = new byte[20];
		Arrays.fill(max, (byte) Byte.MAX_VALUE);
		idMax = new BigInteger(max);
		
		this.I = I;
		this.setS(S);
		this.idEnd = normalizeID(idEnd.toBigInteger());
		this.idStart = idStart.toBigInteger().add(new BigInteger("1"));
		this.idStart = normalizeID(this.idStart);
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

	public void incrementNumberOfHits(){
		this.numberOfHits++;
	}
	
	public void incrementNumberOfMisses(){
		this.numberOfMisses++;
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
		int field = calculateFieldFromID(target) - 1;
//		int field = 20;
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
			tmp = tmp.multiply(new BigInteger("" + (this.I - 1)));
			halfSize = calculateDistance().subtract(tmp).divide(new BigInteger("2"));
//			halfSize = new BigInteger(tmp.toByteArray()).divide(new BigInteger("2"));
			tmp = tmp.add(halfSize);			
		} else {
			tmp = tmp.multiply(new BigInteger(field.toString()));
			halfSize = new BigInteger(fieldSize.toByteArray());
			halfSize = halfSize.divide(new BigInteger("2"));
			tmp = tmp.subtract(halfSize);
		}
		tmp = tmp.add(idStart);
		if (idMax.compareTo(tmp) == 1) {
			tmp = tmp.subtract(idMax);
		}
		
		byte[] id = new byte[20];
		
		if (tmp.toByteArray().length > 20) {
			System.arraycopy(tmp.toByteArray(), 1, id, 0, id.length);
		} else {
			id = tmp.toByteArray(); 
		}
		
		System.out.println("The fieldsize is " + fieldSize);
		System.out.println("Here we go " + new ID(id));
		return new ID(id);
		
		
//		byte[] leadingZero = new byte[20];
//		if (tmp.toByteArray().length < 20) {
//			System.arraycopy(tmp.toByteArray(), 0, leadingZero, 1, tmp.toByteArray().length);
//		} else {
//			leadingZero = tmp.toByteArray(); 
//		}
//		System.out.println("ID: " + new ID(leadingZero));
//		return new ID(leadingZero);
	}

	public int calculateFieldFromID(ID id) {
		BigInteger tmp = id.toBigInteger();
		int result;
		if (idStart.compareTo(tmp) == 1) {//wrap around
			tmp = tmp.add(idMax).subtract(idStart);
		} else {
			tmp = tmp.subtract(idStart);
		}
//		System.out.println("idstart:" + idStart);
//		System.out.println("idend:" + idEnd);
//		System.out.println("fieldsize:" + fieldSize);
//		System.out.println("id: " + id.toBigInteger());
//		System.out.println(tmp.toString());
		tmp = tmp.divide(fieldSize);
//		System.out.println(tmp.toString());
		result = Integer.valueOf(tmp.toString());
		if (result >= this.I) {
			result = this.I - 1;
		}
//		System.out.println("result: " + result);
		return result + 1;
	}
	
	private void calculateFieldSize() {
		fieldSize = calculateDistance().divide(new BigInteger("" + this.I));
		System.out.println("fieldsize:        " + fieldSize);
	}

	private BigInteger calculateDistance() {
		BigInteger dist;
		BigInteger tmpEnd = addLeadingZero(idEnd);
		BigInteger tmpStart = addLeadingZero(idStart);
		System.out.println("leadingzeroend:   " + tmpEnd);
		System.out.println("leadingzerostart: " + tmpStart);
		ID tmpStartId = new ID(idStart.toByteArray());
		ID tmpEndId = new ID(idEnd.toByteArray());
		if (tmpEndId.compareTo(tmpStartId) == 1) { // ende größer
//		if (idEnd.compareTo(idStart) == 1) { // ende größer
			System.out.println("kein wrap");
			dist = new BigInteger(tmpEnd.toString());
		} else { // start größer; wrap around
			System.out.println("wrap");
			dist = new BigInteger(idMax.toString());
			dist = dist.add(tmpEnd);
			System.out.println("distmittendrin:   " + dist);
		}
		dist = dist.subtract(tmpStart);
		return dist;
	}

	private void calculateFleetDeployment() {

	}

	private BigInteger normalizeID(BigInteger id) {
		byte[] tmp = new byte[20];
		
		if (id.toByteArray().length > 20) {
			System.arraycopy(id.toByteArray(), 1, tmp, 0, tmp.length);
		} else {
			tmp = id.toByteArray(); 
		}
		
		return new BigInteger(tmp);
	}
	
	private BigInteger addLeadingZero(BigInteger id) {
		byte[] tmp = new byte[21];
		
		if (id.toByteArray().length < 21) {
			System.arraycopy(id.toByteArray(), 0, tmp, 1, id.toByteArray().length);
		} else {
			tmp = id.toByteArray(); 
		}
		
		return new BigInteger(tmp);
	}

}
