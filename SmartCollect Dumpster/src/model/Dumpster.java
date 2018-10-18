package model;

import java.io.Serializable;

/**
 * @author Matheus Teles
 */
public class Dumpster implements Serializable {	
	private static final long serialVersionUID = 5955008319878533125L;
	
	private int idNumber;
	private double trashQuantity, maxCapacity;
	private DumpsterType type;
	
	/**
	 * Constructs a new Dumpster
	 * @param idNumber Dumpster id.
	 * @param trashQuantity Dumpster trash quantity.
	 * @param maxCapacity Dumpster max capacity.
	 * @param type Dumpster type.
	 */
	public Dumpster(int idNumber, double maxCapacity, DumpsterType type) { 
		this.idNumber = idNumber;
		this.trashQuantity = 0;
		this.maxCapacity = maxCapacity;
		this.type = type;
	}
	
	/**
	 * Set dumpster trash quantity.
	 * @param trashQuantity New trash quantity.
	 */
	public void setTrashQuantity(double trashQuantity) {
		this.trashQuantity = trashQuantity;
	}
	
	/**
	 * Returns trash quantity.
	 * @return dumpster trash quantity.
	 */
	public double getTrashQuantity() {
		return trashQuantity;		
	}
	
	/**
	 * Returns dumpster max capacity.
	 * @return Dumpster max capacity.
	 */
	public double getMaxCapacity() {
		return maxCapacity;
	}
	
	/**
	 * Returns dumpster id, dumpster max capacity and type.
	 * @return Dumpster id, dumpster max capacity and type.
	 */
	public String toString() {
		return Integer.toString(idNumber) + " " + Double.toString(maxCapacity) + " " + type;
	}
	
	/**
	 * Returns dumpster id and trash quantity.
	 * @return Dumpster id and trash quantity.
	 */
	public String toStringShort() {
		return Integer.toString(idNumber) + " " + Double.toString(trashQuantity);
	}
}
