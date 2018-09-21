package model;

import java.io.Serializable;

public class Dumpster implements Serializable {	
	private static final long serialVersionUID = 5955008319878533125L;
	
	private int idNumber;
	private double trashQuantity, maxCapacity;
	private String address;	
	private DumpsterType type;
	
	public Dumpster(int idNumber, double maxCapacity, String address, DumpsterType type) { 
		this.idNumber = idNumber;
		this.trashQuantity = 0;
		this.maxCapacity = maxCapacity;
		this.address = address;
		this.type = type;
	}
	
	public void setTrashQuantity(double trashQuantity) {
		this.trashQuantity = trashQuantity;
	}
	
	public int getIdNumber() {
		return idNumber;		
	}
	
	public double getTrashQuantity() {
		return trashQuantity;		
	}
	
	public double getMaxCapacity() {
		return maxCapacity;
	}
	
	public String getAddress() {
		return address;
	}
	
	public DumpsterType getType() {
		return type;
	}
}
