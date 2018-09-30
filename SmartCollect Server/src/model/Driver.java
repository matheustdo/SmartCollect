package model;

public class Driver {
	private int id;
	private int pos;
	private String route;
	
	public Driver(int id, int pos, String route) {
		this.id = id;
		this.pos = pos;
		this.route = route;
	}

	public int getId() {
		return id;
	}

	public int getPos() {
		return pos;
	}

	public String getRoute() {
		return route;
	}
}
