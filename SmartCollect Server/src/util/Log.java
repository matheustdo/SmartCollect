package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	private final static DateFormat dateFormat = new SimpleDateFormat("[dd/MM/yyyy] [HH:mm:ss]");
	
	private static String time() {
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String server(String s) {
		return time() + " [SERVER THREAD/INFO]: " + s;
	}
	
	public static String serverError(String s) {
		return time() + " [SERVER THREAD/EXCEPTION]: " + s;
	}
}
