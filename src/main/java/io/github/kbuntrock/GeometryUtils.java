package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class GeometryUtils {

	// https://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection
//	def intersect(Circle(P, R), Rectangle(A, B, C, D)):
//	S = Circle(P, R)
//    return (pointInRectangle(P, Rectangle(A, B, C, D)) or
//	intersectCircle(S, (A, B)) or
//	intersectCircle(S, (B, C)) or
//	intersectCircle(S, (C, D)) or
//	intersectCircle(S, (D, A)))

	public static boolean pointInRectangle(final Vecteur point, final Rectangle rectangle) {
		// 0 ≤ AP·AB ≤ AB·AB and 0 ≤ AP·AD ≤ AD·AD
		throw new RuntimeException("Not implemented");
	}

	public static boolean intersectCircle(final Cercle cercle, final Vecteur pt1, final Vecteur pt2) {
		throw new RuntimeException("Not implemented");
	}
}
