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

	Direction directionEntity(final Entity entity) {
		if(pos.x < entity.pos.x && pos.y < entity.pos.y) {
			return Direction.BR;
		} else if(pos.x < entity.pos.x && pos.y >= entity.pos.y) {
			return Direction.TR;
		} else if(pos.x >= entity.pos.x && pos.y < entity.pos.y) {
			return Direction.BL;
		} else if(pos.x >= entity.pos.x && pos.y >= entity.pos.y) {
			return Direction.TL;
		}
		throw new RuntimeException("Direction non gérée");
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
