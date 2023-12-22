package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class Coord {

	public static final int POSITIF = 424;
	public static final int NEGATIF = -424;

	final int x;
	final int y;

	Coord(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	Coord(final EScanner in) {
		this(in.nextInt(), in.nextInt());
	}

	Coord add(final Coord other) {
		return new Coord(x + other.x, y + other.y);
	}

	Coord add(final Vecteur vecteur) {
		return add(vecteur, 0, 10000);
	}

	Coord add(final Vecteur vecteur, final int profondeurMinimale, final int profondeurMaximale) {
		final double x = Math.max(0, Math.min(this.x + vecteur.x, 10000));
		final double y = Math.max(profondeurMinimale,
			Math.min(this.y + vecteur.y, profondeurMaximale));
		return new Coord((int) x, (int) y);
	}

	Coord minus(final Coord other) {
		return new Coord(x - other.x, y - other.y);
	}

	// Manhattan distance (for 4 directions maps)
	// see: https://en.wikipedia.org/wiki/Taxicab_geometry
	int distance(final Coord other) {
		return abs(x - other.x) + abs(y - other.y);
	}

	int squareDistance(final Coord other) {
		return ((x - other.x) * (x - other.x)) + ((y - other.y) * (y - other.y));
	}

	Coord positionIntermediaire(final Coord depart, final Coord arrivee, final int distance) {
		if(arrivee.squareDistance(depart) < distance) {
			return arrivee;
		} else {
			final Coord vecteur = arrivee.minus(depart);
		}

		return null;
	}

	public static int abs(final int a) {
		return (a < 0) ? -a : a;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + x;
		result = PRIME * result + y;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		final Coord other = (Coord) obj;
		return (x == other.x) && (y == other.y);
	}

	@Override
	public String toString() {
		return x + " " + y;
	}
}
