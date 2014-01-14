package buschler.chord;

import de.uniba.wiai.lspi.chord.data.ID;

public class FleetDebug extends Fleet {

	/**
	 * 
	 * @param idEnd
	 * @param idStart
	 */
	public FleetDebug(ID idEnd, ID idStart) {
		super(idEnd, idStart);
	}

	/**
	 * 
	 * @param idEnd
	 * @param idStart
	 * @param I
	 * @param S
	 */
	public FleetDebug(ID idEnd, ID idStart, int I, int S) {
		super(idEnd, idStart, I, S);
	}

	@Override
	public ID calculateIDFromField(Integer field) {
		ID fieldID = super.calculateIDFromField(field);
		System.out.println("vvvvvvvvvv calculateIDFromField vvvvvvvvvv");
		System.out.println("The fieldsize is      " + fieldSize);
		System.out.println("The ID for field " + field + " is " + fieldID);
		System.out.println("^^^^^^^^^^ calculateFieldFromID ^^^^^^^^^^");

		return fieldID;
	}

	@Override
	public int calculateFieldFromID(ID id) {
		int field = super.calculateFieldFromID(id);
		System.out.println("vvvvvvvvvv calculateFieldFromID vvvvvvvvvv");
		System.out.println("The fieldsize is " + fieldSize);
		System.out.println("The field for ID " + id + " is " + (field));
		System.out.println("^^^^^^^^^^ calculateFieldFromID ^^^^^^^^^^");
		return field;
	}

	@Override
	public void calculateFieldSize() {
		super.calculateFieldSize();
		System.out.println("vvvvvvvvvv calculateFieldSize vvvvvvvvvv");
		System.out.println("The fieldsize for this ocean is " + fieldSize);
		System.out.println("^^^^^^^^^^ calculateFieldSize ^^^^^^^^^^");
	}

	@Override
	protected ID calculateDistance() {
		ID dist = super.calculateDistance();
		System.out.println("vvvvvvvvvv calculateDistance vvvvvvvvvv");
		System.out.println("The distance is " + dist);
		System.out.println("^^^^^^^^^^ calculateDistance ^^^^^^^^^^");
		return dist;

	}

}
