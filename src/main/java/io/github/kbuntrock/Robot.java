package io.github.kbuntrock;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kévin Buntrock
 */
public abstract class Robot extends Entity {

	final int emergency;
	final int battery;

	Action action;

	Set<Integer> scans = new HashSet<>();

	Robot(final EScanner in) {
		super(in);
		pos = new Coord(in);
		emergency = in.nextInt();
		battery = in.nextInt();
	}

	@Override
	public String toString() {
		return "Robot{" +
			", battery=" + battery +
			", id=" + id +
			", pos=" + pos +
			'}';
	}
}
