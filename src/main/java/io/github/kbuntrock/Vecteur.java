package io.github.kbuntrock;

import java.math.BigDecimal;

/**
 * @author Kévin Buntrock
 */
public class Vecteur {

	BigDecimal x;

	BigDecimal y;

	public Vecteur(final Coord depart, final Coord arrivee) {
		final Coord coord = arrivee.minus(depart);
		x = BigDecimal.valueOf(coord.x);
		y = BigDecimal.valueOf(coord.y);
	}

	public Vecteur(final BigDecimal x, final BigDecimal y) {
		this.x = x;
		this.y = y;
	}

	public Vecteur adapt(final double distance) {

		final long squaredLenght = (x.longValue() * x.longValue()) + (y.longValue() * y.longValue());
		final BigDecimal xAxis = BigDecimal.valueOf(x.longValue() * distance / Math.sqrt(squaredLenght));
		final BigDecimal yAxis = BigDecimal.valueOf(y.longValue() * distance / Math.sqrt(squaredLenght));

		return new Vecteur(xAxis, yAxis);
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
