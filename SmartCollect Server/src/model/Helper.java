package model;

/**
 * @author Matheus Teles
 */
public class Helper {
	String area,
		   ip;
	int port,
		trashCansQuantity;
	
	/**
	 * Constructs helper.
	 * @param area Helper area.
	 * @param trashCansQuantity Helper trash cans quantity.
	 * @param ip Helper ip.
	 * @param port Helper port.
	 */
	public Helper(String area, int trashCansQuantity, String ip, int port) {
		this.area = area;
		this.trashCansQuantity = trashCansQuantity;
		this.ip = ip;
		this.port = port;
	}

	/**
	 * Gets helper area.
	 * @return Helper area.
	 */
	public String getArea() {
		return area;
	}

	/**
	 * Gets helper ip.
	 * @return Helper ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Gets helper port.
	 * @return Helper port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets trash cans quantity.
	 * @return Trash cans quantity.
	 */
	public int getTrashCansQuantity() {
		return trashCansQuantity;
	}
}
