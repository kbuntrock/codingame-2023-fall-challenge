package io.github.kbuntrock;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author Kévin Buntrock
 */
public class Vecteur {

	public static final Vecteur ZERO = new Vecteur(0, 0);

	double x;

	double y;

	Vecteur(final EScanner in) {
		this(in.nextInt(), in.nextInt());
	}

	public Vecteur(final Vecteur depart, final Vecteur arrivee) {
		final Vecteur coord = arrivee.minus(depart);
		x = coord.x;
		y = coord.y;
	}

	public Vecteur(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public double longueur() {
		return Math.sqrt(Vecteur.ZERO.squareDistance(this));
	}

	Vecteur add(final Vecteur vecteur) {
		return new Vecteur(x + vecteur.x, y + vecteur.y);
	}

	Vecteur adaptToMap() {
		return new Vecteur(Math.min(Math.max(x, 0), 10000), Math.min(Math.max(y, 0), 10000));
	}

	Vecteur minus(final Vecteur other) {
		return new Vecteur(x - other.x, y - other.y);
	}

	double squareDistance(final Vecteur other) {
		return ((x - other.x) * (x - other.x)) + ((y - other.y) * (y - other.y));
	}

	int intDistance(final Vecteur other) {
		return BigDecimal.valueOf(squareDistance(other)).sqrt(MathContext.DECIMAL32).setScale(0, RoundingMode.HALF_DOWN).intValue();
	}

	public Vecteur adapt(final double distance) {

		final double squaredLenght = (x * x) + (y * y);
		final BigDecimal xAxis = BigDecimal.valueOf(x * distance / Math.sqrt(squaredLenght)).setScale(0, RoundingMode.HALF_DOWN);
		final BigDecimal yAxis = BigDecimal.valueOf(y * distance / Math.sqrt(squaredLenght)).setScale(0, RoundingMode.HALF_DOWN);

		return new Vecteur(xAxis.intValue(), yAxis.intValue());
	}

	public boolean aPortee(final Vecteur v, final double range) {
		return (v.x - x) * (v.x - x) + (v.y - y) * (v.y - y) <= range * range;
	}

	public boolean isZero() {
		return x == 0 && y == 0;
	}

	/**
	 * Intersection de deux cercles
	 */
	Vecteur[] intersection(final int r1, final Vecteur c2, final int r2) {

		// https://fypandroid.wordpress.com/2011/07/03/how-to-calculate-the-intersection-of-two-circles-java/

//		Let’s say you’re trying to find the intersection points of the circles C1 and C2 where C1 has it’s center point at
//		(-9, 1) and has a radius of 7, and C2’s center lies at (5, -5) and has a radius of 18.
		//d = √(|-9 – 5| + |1 – -5|)
		final BigDecimal d = new BigDecimal(Math.pow(x - c2.x, 2) + Math.pow(y - c2.y, 2)).sqrt(MathContext.DECIMAL64);

		if(d.subtract(BigDecimal.valueOf(r1 + r2)).longValue() > 0) {
			// Il n'y a pas d'intersection entre les deux cercles.
			return null;
		}

		//d1 = (r1^2 – r2^2 + d^2) / 2*d
		//   = (7^2 – 18^2 + 15.23^2) / 2*15.23
		final BigDecimal r1pow2 = BigDecimal.valueOf(r1).pow(2);
		final BigDecimal d1 = r1pow2.subtract(BigDecimal.valueOf(r2).pow(2)).add(d.pow(2))
			.divide(d.multiply(new BigDecimal(2)), MathContext.DECIMAL64);

		// Now we solve ‘h’, which is 1/2 * ‘a’
		//    h = √(r1^2 – d1^2)
		final BigDecimal h = r1pow2.subtract(d1.pow(2)).sqrt(MathContext.DECIMAL64);

		// To find point P3(x3,y3) which is the intersection of line ‘d’ and ‘a’ we use the following formula:
		// x3 = x1 + (d1 * (x2 – x1)) / d
		// = -9 + (-1.41 * (5 – -9)) / 15.23
		final BigDecimal x3 = d1.multiply(BigDecimal.valueOf(c2.x - x)).divide(d, MathContext.DECIMAL64).add(new BigDecimal(x));

		// y3 = y1 + (d1 * (y2 – y1)) / d
		//  = 1 + (-1.41 * (-5 – 1)) / 15.23
		final BigDecimal y3 = d1.multiply(BigDecimal.valueOf(c2.y - y)).divide(d, MathContext.DECIMAL64).add(new BigDecimal(y));

		final BigDecimal ix1 = x3.add(h.multiply(BigDecimal.valueOf(c2.y - y)).divide(d, MathContext.DECIMAL64));
		final BigDecimal iy1 = y3.subtract(h.multiply(BigDecimal.valueOf(c2.x - x)).divide(d, MathContext.DECIMAL64));

		final BigDecimal ix2 = x3.subtract(h.multiply(BigDecimal.valueOf(c2.y - y)).divide(d, MathContext.DECIMAL64));
		final BigDecimal iy2 = y3.add(h.multiply(BigDecimal.valueOf(c2.x - x)).divide(d, MathContext.DECIMAL64));

		final Vecteur[] intersections = new Vecteur[2];
		intersections[0] = new Vecteur(ix1.setScale(0, RoundingMode.HALF_DOWN).intValue(),
			iy1.setScale(0, RoundingMode.HALF_DOWN).intValue());
		intersections[1] = new Vecteur(ix2.setScale(0, RoundingMode.HALF_DOWN).intValue(),
			iy2.setScale(0, RoundingMode.HALF_DOWN).intValue());

		return intersections;
	}

	@Override
	public boolean equals(final Object object) {
		if(this == object) {
			return true;
		}
		if(object == null || getClass() != object.getClass()) {
			return false;
		}
		final Vecteur vecteur = (Vecteur) object;
		return Double.compare(x, vecteur.x) == 0 && Double.compare(y, vecteur.y) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return x + " " + y;
	}

	public String toAction() {
		return BigDecimal.valueOf(x).setScale(0, RoundingMode.HALF_DOWN).intValue() + " " + BigDecimal.valueOf(y)
			.setScale(0, RoundingMode.HALF_DOWN).intValue();
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
