package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public enum EntityType {
	NOTHING, ALLY_ROBOT, ENEMY_ROBOT, RADAR, TRAP, AMADEUSIUM, FISH;

	static EntityType valueOf(final int id) {
		return values()[id + 1];
	}
}