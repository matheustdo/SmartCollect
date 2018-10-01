package model;

import java.io.Serializable;

/**
 * @author Matheus Teles
 */
public class Dumpster implements Serializable {	
	private static final long serialVersionUID = 5955008319878533125L;
	
	private int idNumber;
	private int regionIdNumber;
	private double trashQuantity, maxCapacity;
	private DumpsterType type;
	
	/**
	 * Constructs a new Dumpster
	 * @param idNumber Dumpster id.
	 */
	public Dumpster(int idNumber) {
		this.idNumber = idNumber;
		this.regionIdNumber = -1;
		this.trashQuantity = -1;
		this.maxCapacity = -1;
		this.type = DumpsterType.UNKNOW;
	}
	
	/**
	 * Constructs a new Dumpster
	 * @param idNumber Dumpster id.
	 * @param regionIdNumber Dumpster region id.
	 * @param trashQuantity Dumpster trash quantity.
	 * @param maxCapacity Dumpster max capacity.
	 * @param type Dumpster type.
	 */
	public Dumpster(int idNumber, int regionIdNumber, double trashQuantity, double maxCapacity, DumpsterType type) { 
		this.idNumber = idNumber;
		this.regionIdNumber = regionIdNumber;
		this.trashQuantity = trashQuantity;
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
	 * Returns dumpster id number.
	 * @return Dumpster id number.
	 */
	public int getIdNumber() {
		return idNumber;		
	}
	
	/**
	 * Returns dumpster region id.
	 * @return Dumpster region id.
	 */
	public int getRegionIdNumber() {
		return regionIdNumber;
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
	 * Returns dumpster type.
	 * @return Dumpster type.
	 */
	public DumpsterType getType() {
		return type;
	}
	
	/**
	 * Returns dumpsters type name.
	 * @return Dumpsters type name.
	 */
	public String getTypeName() {
		if(type.equals(DumpsterType.CAN)) {
			return "Can";
		} else if(type.equals(DumpsterType.STATION)) {
			return "Station";
		} else {
			return "Unknow";
		}
	}
	
	/**
	 * Returns trash percentage.
	 * @return Trash percentage.
	 */
	public double getTrashPercentage() {
		if(maxCapacity == -1) {
			return -1;
		} else {
			return (trashQuantity*100)/maxCapacity;
		}
	}
	
	/**
	 * Returns dumpster id and trash quantity.
	 * @return Dumpster id and trash quantity.
	 */
	public String toString() {
		return Integer.toString(idNumber) + " " + Double.toString(trashQuantity);
	}
}
