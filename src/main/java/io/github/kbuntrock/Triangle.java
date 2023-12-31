package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class Triangle {

	Vecteur p1;
	Vecteur p2;
	Vecteur p3;

	public Triangle(final Vecteur p1, final Vecteur p2, final Vecteur p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}

	public boolean poissonInside(final Poisson p) {
		if(p.pos != null) {
			return pointInside(p.pos);
		}
		boolean inside = pointInside(new Vecteur(p.currentMinX, p.currentMinY));
		inside = inside && pointInside(new Vecteur(p.currentMaxX, p.currentMinY));
		inside = inside && pointInside(new Vecteur(p.currentMaxX, p.currentMaxY));
		inside = inside && pointInside(new Vecteur(p.currentMinX, p.currentMaxY));
		return inside;
	}

	/**
	 * https://stackoverflow.com/questions/2049582/how-to-determine-if-a-point-is-in-a-2d-triangle
	 *
	 * @param pt
	 * @return
	 */
	public boolean pointInside(final Vecteur pt) {
		final double aireTriangle = aireTriangle();
		final double aire1 = new Triangle(pt, p2, p3).aireTriangle();
		final double aire2 = new Triangle(pt, p1, p3).aireTriangle();
		final double aire3 = new Triangle(pt, p1, p2).aireTriangle();

		if((aire1 + aire2 + aire3) > aireTriangle) {
			return false;
		} else {
			return true;
		}
	}

	private double aireTriangle() {
		final var det = ((p1.x - p3.x) * (p2.y - p3.y)) - ((p2.x - p3.x) * (p1.y - p3.y));
		return (det / 2.0d);
	}

	@Override
	public String toString() {
		return "Triangle{" +
			"p1=" + p1 +
			", p2=" + p2 +
			", p3=" + p3 +
			'}';
	}
}
