package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public abstract class Entity {

	// Updated every turn
	final int id;

	Vecteur pos;
	EntityType type;


	public Entity(final int id, final Vecteur pos, final EntityType type) {
		this.id = id;
		this.pos = pos;
		this.type = type;
	}

	Entity(final EScanner in) {
		id = in.nextInt();
	}

}