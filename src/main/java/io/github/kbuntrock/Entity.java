package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public abstract class Entity {

	private static final Coord DEAD_POS = new Coord(-1, -1);

	// Updated every turn
	final int id;

	Coord pos;
	EntityType type;


	public Entity(final int id, final Coord pos, final EntityType type) {
		this.id = id;
		this.pos = pos;
		this.type = type;
	}

	Entity(final EScanner in) {
		id = in.nextInt();
	}

}