package model;

import java.io.Serializable;

public class Dumpster implements Serializable {	
	private static final long serialVersionUID = 5955008319878533125L;
	private double trashQuantity, maxCapacity;
	private String address;	
	private DumpsterType type;
	
	public Dumpster() { 
		this.trashQuantity = 0;
		this.maxCapacity = 500;
		this.address = "";
		this.type = DumpsterType.CAN;
	}
	
	public Dumpster(double maxCapacity, String address, DumpsterType type) { 
		this.trashQuantity = 0;
		this.maxCapacity = maxCapacity;
		this.address = address;
		this.type = type;
	}
	
	public void increaseTrash(double increasement) throws NoSpaceException {
		if (trashQuantity + increasement <= maxCapacity) {
			trashQuantity += increasement;
		} else {
			throw new NoSpaceException();
		}
	}
	
	public void setEmpty() {
		this.trashQuantity = 0;
	}
	
	public void setMaxCapacity(double maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
	
	public double getTrashLevel() {
		return (100*trashQuantity)/maxCapacity;
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
