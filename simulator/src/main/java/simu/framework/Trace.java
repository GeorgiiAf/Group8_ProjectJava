package simu.framework;

/**
 * Provides tracing functionality for logging messages with different levels of severity.
 */
public class Trace {

	/**
	 * Enum representing different levels of trace severity.
	 */
	public enum Level {
		INFO, WAR, ERR
	}

	private static Level traceLevel;

	/**
	 * Sets the trace level. Only messages with a severity equal to or higher than this level will be logged.
	 *
	 * @param lvl the trace level to set
	 */
	public static void setTraceLevel(Level lvl) {
		traceLevel = lvl;
	}

	/**
	 * Logs a message if its severity level is equal to or higher than the current trace level.
	 *
	 * @param lvl the severity level of the message
	 * @param txt the message to log
	 */
	public static void out(Level lvl, String txt) {
		if (lvl.ordinal() >= traceLevel.ordinal()) {
			System.out.println(txt);
		}
	}
}