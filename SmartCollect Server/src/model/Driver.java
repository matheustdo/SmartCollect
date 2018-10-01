package model;

/**
 * @author Matheus Teles
 */
public class Driver {
	private int id;
	private int pos;
	private String route;
	
	/**
	 * Constructs a new Driver.
	 * @param id Driver id.
	 * @param pos Driver position.
	 * @param route Driver route.
	 */
	public Driver(int id, int pos, String route) {
		this.id = id;
		this.pos = pos;
		this.route = route;
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
}
