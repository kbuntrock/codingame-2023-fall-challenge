package io.github.kbuntrock;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kévin Buntrock
 */
public abstract class Robot extends Entity {

	public static final int HIT_RANGE = 200;

	final int emergency;
	final int battery;

	Action action;

	Set<Integer> scans = new HashSet<>();

	Robot(final EScanner in) {
		super(in);
		pos = new Vecteur(in);
		emergency = in.nextInt();
		battery = in.nextInt();
	}

	Direction directionEntity(final Entity entity) {
		if(pos.x < entity.pos.x && pos.y < entity.pos.y) {
			return Direction.BR;
		} else if(pos.x < entity.pos.x && pos.y >= entity.pos.y) {
			return Direction.TR;
		} else if(pos.x >= entity.pos.x && pos.y < entity.pos.y) {
			return Direction.BL;
		} else if(pos.x >= entity.pos.x && pos.y >= entity.pos.y) {
			return Direction.TL;
		}
		throw new RuntimeException("Direction non gérée");
	}

	public boolean collide(final Monstre ugly, final Vecteur viseeInitiale) {

		// Au cas où on vise en dehors de la carte, on corrige les coordonnées pour le calcul de collision
		final Vecteur visee = viseeInitiale.adaptToMap();
		// Check instant collision
		if(ugly.pos.aPortee(visee, HIT_RANGE + Monstre.EAT_RANGE)) {
			return true;
		}
		final Vecteur vitesse = visee.minus(pos);

		// Both units are motionless
		if(vitesse.isZero() && ugly.vitesse.isZero()) {
			return false;
		}

		// Change referencial
		final double x = ugly.pos.x;
		final double y = ugly.pos.y;
		final double ux = pos.x;
		final double uy = pos.y;

		final double x2 = x - ux;
		final double y2 = y - uy;
		final double r2 = Monstre.EAT_RANGE + HIT_RANGE;
		final double vx2 = ugly.vitesse.x - vitesse.x;
		final double vy2 = ugly.vitesse.y - vitesse.y;

		// Resolving: sqrt((x + t*vx)^2 + (y + t*vy)^2) = radius <=> t^2*(vx^2 + vy^2) + t*2*(x*vx + y*vy) + x^2 + y^2 - radius^2 = 0
		// at^2 + bt + c = 0;
		// a = vx^2 + vy^2
		// b = 2*(x*vx + y*vy)
		// c = x^2 + y^2 - radius^2

		final double a = vx2 * vx2 + vy2 * vy2;

		if(a <= 0.0) {
			return false;
		}

		final double b = 2.0 * (x2 * vx2 + y2 * vy2);
		final double c = x2 * x2 + y2 * y2 - r2 * r2;
		final double delta = b * b - 4.0 * a * c;

		if(delta < 0.0) {
			return false;
		}

		final double t = (-b - Math.sqrt(delta)) / (2.0 * a);

		if(t <= 0.0) {
			return false;
		}

		if(t > 1.0) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Robot{" +
			", battery=" + battery +
			", id=" + id +
			", pos=" + pos +
			'}';
	}
}
