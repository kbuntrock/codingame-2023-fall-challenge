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

	//final Coord pos;
	//final EntityType item;


	public Entity(final int id, final Coord pos, final EntityType type) {
		this.id = id;
		this.pos = pos;
		this.type = type;
	}

	Entity(final EScanner in) {
		id = in.nextInt();
		//type = EntityType.CREATURE;
		//type = EntityType.valueOf(in.nextInt());
		//pos = new Coord(in);
		//item = EntityType.valueOf(in.nextInt());
	}

	//boolean isAlive() {
//		return !DEAD_POS.equals(pos);
//	}
}