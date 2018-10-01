package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Matheus Teles
 */
public class Log {
	private final static DateFormat dateFormat = new SimpleDateFormat("[dd/MM/yyyy] [HH:mm:ss]");
	
	/**
	 * Returns actual system date.
	 * @return System date.
	 */
	private static String time() {
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * Returns server thread info log message.
	 * @param s String log message.
	 * @return Log message.
	 */
	public static String server(String s) {
		return time() + " [SERVER THREAD/INFO]: " + s;
	}
	
	/**
	 * Returns server thread exception log message.
	 * @param s String log message.
	 * @return Log message.
	 */
	public static String serverError(String s) {
		return time() + " [SERVER THREAD/EXCEPTION]: " + s;
	}
}
