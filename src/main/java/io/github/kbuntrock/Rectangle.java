package io.github.kbuntrock;

/**
 * Pour savoir si un point est dans un triangle :
 * https://www.gamedev.net/forums/topic.asp?topic_id=295943
 *
 * // A floating point
 * struct fPoint
 * {
 * float x;
 * float y;
 * };
 *
 * IsPointInTri() -
 * * Used by IsPointInQuad(). Function takes the point and the triangle's
 * * vertices. It finds the area of the passed triangle (v1 v2 v3), and then the
 * * areas of the three triangles (pt v2 v3), (pt v1 v3), and (pt v1 v2). If the
 * * sum of these three is greater than the first, then the point is outside of
 * * the triangle.
 *
 * bool IsPointInTri(const fPoint*pt,const fPoint*v1,const fPoint*v2,const fPoint*v3)
 * {
 * float TotalArea=CalcTriArea(v1,v2,v3);
 * float Area1=CalcTriArea(pt,v2,v3);
 * float Area2=CalcTriArea(pt,v1,v3);
 * float Area3=CalcTriArea(pt,v1,v2);
 *
 * if((Area1+Area2+Area3)>TotalArea)
 * return false;
 * else
 * return true;
 * }
 *
 * /* CalcTriArea() -
 * * Find the area of a triangle. This function uses the 1/2 determinant
 * * method. Given three points (x1, y1), (x2, y2), (x3, y3):
 * *             | x1 y1 1 |
 * * Area = .5 * | x2 y2 1 |
 * *             | x3 y3 1 |
 * * From: http://mcraefamily.com/MathHelp/GeometryTriangleAreaDeterminant.htm
 *
 * float CalcTriArea(fPoint*v1,fPoint*v2,fPoint*v3)
 * {
 * float det=0.0f;
 * det=((v1->x-v3->x)*(v2->y-v3->y))-((v2->x-v3->x)*(v1->y-v3->y));
 * return(det/2.0f);
 * }
 *
 * @author Kévin Buntrock
 */
public class Rectangle {

	Vecteur gh;
	Vecteur dh;
	Vecteur db;
	Vecteur gb;

	public Rectangle(final Vecteur gh, final Vecteur dh, final Vecteur db, final Vecteur gb) {
		this.gh = gh;
		this.dh = dh;
		this.db = db;
		this.gb = gb;
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

	public boolean pointInside(final Vecteur pt) {
		return pt.x >= gh.x && pt.x <= dh.x && pt.y >= gh.y && pt.y <= gb.y;
	}

	@Override
	public String toString() {
		return "Rectangle{" +
			"gh=" + gh +
			", dh=" + dh +
			", db=" + db +
			", gb=" + gb +
			'}';
	}
}
