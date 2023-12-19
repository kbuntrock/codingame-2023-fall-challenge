package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class RobotEnnemi extends Robot {

	RobotEnnemi(final EScanner in) {
		super(in);
		type = EntityType.ENEMY_ROBOT;
	}
}
