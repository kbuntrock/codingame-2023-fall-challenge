package io.github.kbuntrock;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kévin Buntrock
 */
public class IO {

	static int turn = -1;

	public static Map<Integer, Integer> lastTurnLightOn = new HashMap<>();

	public static LastTurnData lastTurnData = new LastTurnData();

	public static void incrementTurn() {
		turn++;
	}

	public static void info(final String message) {
		log("INFO", message);
	}

	public static void warn(final String message) {
		log("WARN", message);
	}

	private static void log(final String prefix, final String message) {
		System.err.println(prefix + " T" + turn + " : " + message);
	}

	public static void export(final String export) {
		System.err.println("T" + turn + "->" + export);
	}

}
