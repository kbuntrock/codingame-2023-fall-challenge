package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class CollisionUtils {

	public static final int UGLY_EAT_RANGE = 300;
	public static final int DRONE_HIT_RANGE = 200;

	private static boolean inRange(final Vecteur v1, final Vecteur v2, final double range) {
		return (v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y) <= range * range;
	}

//	public static boolean collide(final Coord coord, final Monstre monstre) {
//		// Check instant collision
//		if(inRange(monstre.pos, coord, DRONE_HIT_RANGE + UGLY_EAT_RANGE)) {
//			return true;
//		}
//
//		// Both units are motionless
//		if(drone.getSpeed().isZero() && ugly.getSpeed().isZero()) {
//			return Collision.NONE;
//		}
//
//		// Change referencial
//		final double x = ugly.getPos().getX();
//		final double y = ugly.getPos().getY();
//		final double ux = drone.getPos().getX();
//		final double uy = drone.getPos().getY();
//
//		final double x2 = x - ux;
//		final double y2 = y - uy;
//		final double r2 = UGLY_EAT_RANGE + DRONE_HIT_RANGE;
//		final double vx2 = ugly.getSpeed().getX() - drone.getSpeed().getX();
//		final double vy2 = ugly.getSpeed().getY() - drone.getSpeed().getY();
//
//		final double a = vx2 * vx2 + vy2 * vy2;
//
//		if(a <= 0.0) {
//			return false;
//		}
//
//		final double b = 2.0 * (x2 * vx2 + y2 * vy2);
//		final double c = x2 * x2 + y2 * y2 - r2 * r2;
//		final double delta = b * b - 4.0 * a * c;
//
//		if(delta < 0.0) {
//			return false;
//		}
//
//		final double t = (-b - Math.sqrt(delta)) / (2.0 * a);
//
//		if(t <= 0.0) {
//			return false;
//		}
//
//		if(t > 1.0) {
//			return false;
//		}
//		return new Collision(t, ugly, drone);
//	}

}
