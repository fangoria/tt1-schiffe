package buschler.chord;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uniba.wiai.lspi.chord.data.ID;

public class Fleet {
	private ID idEnd;
	private ID idStart;
	private ID fieldSize;
	private ID idMax;
	private List<Shot> shots;
	private int numberOfHits;
	private int numberOfMisses;
	private int totalShots;
	private radar[] fleetDeployment;
	private int I; // Anzahl Felder
	private int S; // Anzahl Schiffe

	enum radar {
		HIT, MISS, UNKNOWN;
	}

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

	public Fleet(ID idEnd, ID idStart) {
		this(idEnd, idStart, 100, 10);
	}

	public ID getIdEnd() {
		return idEnd;
	}

	public ID getIdStart() {
		return idStart;
	}

	public void setIdStart(ID idStart) {
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

	public void incrementNumberOfHits() {
		this.numberOfHits++;
	}

	public void incrementNumberOfMisses() {
		this.numberOfMisses++;
	}

	public void registerShot(Shot shot) {
		shots.add(shot);
	}
	
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

	public ID calculateIDFromField(Integer field) {
		ID fieldID;
		BigInteger tmp = fieldSize.toBigInteger();
		BigInteger halfSize;
		if (field == this.I) {
			tmp = tmp.multiply(new BigInteger("" + (this.I - 1)));
			halfSize = calculateDistance().toBigInteger().subtract(tmp)
					.divide(new BigInteger("2"));
			// halfSize = new BigInteger(tmp.toByteArray()).divide(new
			// BigInteger("2"));
			tmp = tmp.add(halfSize);
		} else {
			tmp = tmp.multiply(new BigInteger(field.toString()));
			halfSize = fieldSize.toBigInteger();
			halfSize = halfSize.divide(new BigInteger("2"));
			tmp = tmp.subtract(halfSize);
		}

		fieldID = new ID((tmp.add(idStart.toBigInteger())).toByteArray());
		fieldID = normalizeID(fieldID);
		// if (idMax.compareTo(fieldID) == 1) {
		// tmp = tmp.subtract(idMax.toBigInteger());
		// fieldID = new ID(tmp.toByteArray());
		// }
		// System.out.println("vvvvvvvvvv calculateIDFromField vvvvvvvvvv");
		// System.out.println("The fieldsize is      " + fieldSize);
		// System.out.println("The ID for field " + field + " is " + fieldID);
		// System.out.println("^^^^^^^^^^ calculateFieldFromID ^^^^^^^^^^");

		return fieldID;
	}

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

//		System.out.println("vvvvvvvvvv calculateFieldFromID vvvvvvvvvv");
//		System.out.println("The fieldsize is " + fieldSize);
//		System.out.println("The field for ID " + id + " is " + (result + 1));
//		System.out.println("^^^^^^^^^^ calculateFieldFromID ^^^^^^^^^^");

		return result + 1;
	}

	public void calculateFieldSize() {
		fieldSize = new ID(
				(calculateDistance().toBigInteger().divide(new BigInteger(""
						+ this.I))).toByteArray());
		fieldSize = normalizeID(fieldSize);
		// System.out.println("vvvvvvvvvv calculateFieldSize vvvvvvvvvv");
		// System.out.println("The fieldsize for this ocean is " + fieldSize);
		// System.out.println("^^^^^^^^^^ calculateFieldSize ^^^^^^^^^^");
	}

	private ID calculateDistance() {
		ID dist;
		// BigInteger tmpEnd = addLeadingZero(idEnd);
		// BigInteger tmpStart = addLeadingZero(idStart);
		// ID tmpStartId = new ID(idStart.toByteArray());
		// ID tmpEndId = new ID(idEnd.toByteArray());
		// if (tmpEndId.compareTo(tmpStartId) == 1) { // ende gr��er
		if (idEnd.compareTo(idStart) == 1) {
			// if (idEnd.compareTo(idStart) == 1) { // ende gr��er
			dist = new ID(
					(idEnd.toBigInteger().subtract(idStart.toBigInteger()))
							.toByteArray());
		} else { // start gr��er; wrap around
			dist = new ID(
					(idMax.toBigInteger().subtract(idStart.toBigInteger())
							.add(idEnd.toBigInteger())).toByteArray());

			// dist = new BigInteger(idMax.toString());
			// dist = dist.add(tmpEnd);
		}

		dist = normalizeID(dist);

		// System.out.println("vvvvvvvvvv calculateDistance vvvvvvvvvv");
		// System.out.println("The distance is " + dist);
		// System.out.println("^^^^^^^^^^ calculateDistance ^^^^^^^^^^");

		return dist;
	}

	// private void calculateFleetDeployment() {
	//
	// }

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

	// private BigInteger addLeadingZero(BigInteger id) {
	// byte[] tmp = new byte[21];
	//
	// if (id.toByteArray().length < 21) {
	// System.arraycopy(id.toByteArray(), 0, tmp, 1, id.toByteArray().length);
	// } else {
	// tmp = id.toByteArray();
	// }
	//
	// return new BigInteger(tmp);
	// }

}
