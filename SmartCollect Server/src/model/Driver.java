package model;

/**
 * @author Matheus Teles
 */
public class Driver {
	private int id;
	private int pos;
	private String route;
	private String status;
	
	/**
	 * Constructs a new Driver.
	 * @param id Driver id.
	 * @param pos Driver position.
	 * @param route Driver route.
	 */
	public Driver(int id, int pos, String route, String status) {
		this.id = id;
		this.pos = pos;
		this.route = route;
		this.status = status;
	}

	/**
	 * Get the driver id.
	 * @return Driver id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Get the driver position.
	 * @return Driver position.
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * Get the driver route.
	 * @return Driver route.
	 */
	public String getRoute() {
		return route;
	}
	
	/**
	 * Get the driver status.
	 * @return Driver status.
	 */
	public String getStatus() {
		return status;
	}
}
