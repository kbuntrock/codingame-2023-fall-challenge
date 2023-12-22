package io.github.kbuntrock;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Kévin Buntrock
 */
public class Vecteur {

	int x;

	int y;

	public Vecteur(final Coord depart, final Coord arrivee) {
		final Coord coord = arrivee.minus(depart);
		x = coord.x;
		y = coord.y;
	}

	public Vecteur(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public Vecteur adapt(final double distance) {

		final long squaredLenght = (x * x) + (y * y);
		final BigDecimal xAxis = BigDecimal.valueOf(x * distance / Math.sqrt(squaredLenght)).setScale(0, RoundingMode.HALF_DOWN);
		final BigDecimal yAxis = BigDecimal.valueOf(y * distance / Math.sqrt(squaredLenght)).setScale(0, RoundingMode.HALF_DOWN);

		return new Vecteur(xAxis.intValue(), yAxis.intValue());
	}

//	public Vecteur normalize() {
//		if(x.den != 1 || y.den != 1) {
//			throw new RuntimeException("non géré");
//		}
//		final long squaredLenght = (x.num * x.num) + (y.num * y.num);
//		final long sqsqLenght = squaredLenght * squaredLenght;
//		final Fraction xAxis = new Fraction(x.num * sqsqLenght, sqsqLenght * sqsqLenght);
//		final Fraction yAxis = new Fraction(y.num * sqsqLenght, sqsqLenght * sqsqLenght);
//
//		// 78125/244140625 + 156250/244140625
//
//		// 5/ racine(125) + 10/ racine(125) (11,1803398)
//		// 0,2000000 + 0,8000000
//
//		// 625/15625 + 1250/15625
//
//		// 0.04 - 0.08
//		// 0,0016 +
//		return new Vecteur(xAxis, yAxis);
//	}
}
