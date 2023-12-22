package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public enum Direction {
	T(new Vecteur(0, 600)),
	TL(new Vecteur(Coord.NEGATIF, Coord.NEGATIF)),
	TR(new Vecteur(Coord.POSITIF, Coord.NEGATIF)),
	BR(new Vecteur(Coord.POSITIF, Coord.POSITIF)),
	BL(new Vecteur(Coord.NEGATIF, Coord.POSITIF));


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
