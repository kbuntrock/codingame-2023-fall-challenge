package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public enum Direction {
	T(new Vecteur(0, 600)),
	TL(new Vecteur(-424, -424)),
	TR(new Vecteur(424, -424)),
	BR(new Vecteur(424, 424)),
	BL(new Vecteur(-424, 424));


	final Vecteur direction;

	Direction(final Vecteur direction) {
		this.direction = direction;
	}

	public boolean isOppositeXDirection(final Direction other) {
		if(TR == other || BR == other) {
			return this == TL || this == BL;
		}
		return this == TR || this == BR;
	}
}
