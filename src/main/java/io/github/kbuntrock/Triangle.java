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
		return pointInside(p.milieuRectangle());
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
		final var det = (p1.x * (p2.y - p3.y)) + (p2.x * (p3.y - p1.y) + (p3.x * (p1.y - p2.y)));
		return Math.abs(det / 2.0d);
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
