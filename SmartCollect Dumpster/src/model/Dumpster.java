package model;

import java.io.Serializable;

public class Dumpster implements Serializable {	
	private static final long serialVersionUID = 5955008319878533125L;
	
	private int idNumber;
	private int regionIdNumber;
	private double trashQuantity, maxCapacity;
	private DumpsterType type;
	
	public Dumpster(int idNumber) {
		this.idNumber = idNumber;
		this.regionIdNumber = -1;
		this.trashQuantity = -1;
		this.maxCapacity = -1;
		this.type = DumpsterType.UNKNOW;
	}
	
	public Dumpster(int idNumber, int regionIdNumber, double maxCapacity, DumpsterType type) { 
		this.idNumber = idNumber;
		this.regionIdNumber = regionIdNumber;
		this.trashQuantity = 0;
		this.maxCapacity = maxCapacity;
		this.type = type;
	}
	
	public void setTrashQuantity(double trashQuantity) {
		this.trashQuantity = trashQuantity;
	}
	
	public int getIdNumber() {
		return idNumber;		
	}
	
	public int getRegionIdNumber() {
		return regionIdNumber;
	}

	public double getTrashQuantity() {
		return trashQuantity;		
	}
	
	public double getMaxCapacity() {
		return maxCapacity;
	}
	
	public DumpsterType getType() {
		return type;
	}
	
	public String getTypeName() {
		if(type.equals(DumpsterType.CAN)) {
			return "Can";
		} else if(type.equals(DumpsterType.STATION)) {
			return "Station";
		} else {
			return "Unknow";
		}
	}
	
	public double getTrashPercentage() {
		if(maxCapacity == -1) {
			return -1;
		} else {
			return (trashQuantity*100)/maxCapacity;
		}
	}
	
	public String toString() {
		return Integer.toString(idNumber) + " " + Double.toString(trashQuantity);
	}
}
