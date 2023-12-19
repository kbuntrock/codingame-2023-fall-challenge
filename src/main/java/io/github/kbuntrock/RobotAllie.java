package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class RobotAllie extends Robot {

	RobotAllie(final EScanner in) {
		super(in);
		type = EntityType.ALLY_ROBOT;
	}
}
