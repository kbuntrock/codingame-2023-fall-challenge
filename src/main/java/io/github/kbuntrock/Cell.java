package io.github.kbuntrock;

import java.util.Scanner;

/**
 * @author Kévin Buntrock
 */
public class Cell {

	boolean known;
	int ore;
	boolean hole;

	Cell(final boolean known, final int ore, final boolean hole) {
		this.known = known;
		this.ore = ore;
		this.hole = hole;
	}

	Cell(final Scanner in) {
		final String oreStr = in.next();
		if(oreStr.charAt(0) == '?') {
			known = false;
			ore = 0;
		} else {
			known = true;
			ore = Integer.parseInt(oreStr);
		}
		final String holeStr = in.next();
		hole = (holeStr.charAt(0) != '0');
	}
}

