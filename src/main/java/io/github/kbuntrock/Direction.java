package io.github.kbuntrock;

import java.math.BigDecimal;

/**
 * @author Kévin Buntrock
 */
public enum Direction {
	T(new Vecteur(BigDecimal.ZERO, BigDecimal.valueOf(600L))),
	TL(new Vecteur(Coord.NEGATIF, Coord.NEGATIF)),
	TR(new Vecteur(Coord.POSITIF, Coord.NEGATIF)),
	BR(new Vecteur(Coord.POSITIF, Coord.POSITIF)),
	BL(new Vecteur(Coord.NEGATIF, Coord.POSITIF));


	final Vecteur direction;

	Direction(final Vecteur direction) {
		this.direction = direction;
	}
}
