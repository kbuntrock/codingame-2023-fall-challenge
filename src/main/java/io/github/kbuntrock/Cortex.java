package io.github.kbuntrock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kévin Buntrock
 */
public class Cortex {

	Board board;


	public Cortex(final Board board) {
		this.board = board;
	}

	public Poisson trouverPoissonNonScannePlusProche() {
		final Robot robot = board.myTeam.robots.get(0);
		Poisson plusProche = null;
		int minDistance = Integer.MAX_VALUE;
		for(final Poisson poisson : board.poissonsById.values()) {
			if(poisson.visible() && !board.myTeam.savedScans.contains(poisson.id) && !robot.scans.contains(poisson.id)) {
				final Coord prochainePosition = poisson.prochainePosition;
				IO.info("Poisson " + poisson.id + " : " + poisson.pos.toString() + " v : " + poisson.vitesse.toString() + " -> "
					+ prochainePosition);
				final int distance = robot.pos.squareDistance(prochainePosition);
				if(minDistance > distance) {
					minDistance = distance;
					plusProche = poisson;
				}
			}
		}
		IO.info("Poisson plus proche : " + (plusProche == null ? "aucun" : plusProche.id));
		return plusProche;
	}

	public Coord calculerMouvement(final Poisson poisson) {
		final Coord visee = poisson.pos.add(poisson.vitesse);
		IO.info("Future position poisson : " + visee);
		final Robot robot = board.myTeam.robots.get(0);

		final Vecteur vecteur = new Vecteur(robot.pos, visee);
		return robot.pos.add(vecteur.adapt(600));
	}

	public List<Poisson> trouverPoissonsAPortee(final Coord position, final int distanceMin, final int distanceMax) {
		final int sqDistanceMax = distanceMax * distanceMax;
		final int sqDistanceMin = distanceMin * distanceMin;
		final List<Poisson> poissonsAPortee = new ArrayList<>();
		for(final Poisson poisson : board.poissonsById.values()) {
			if(!board.myTeam.savedScans.contains(poisson.id)) {
				final Coord prochainePosition = poisson.prochainePosition;
				final int distancePoisson = position.squareDistance(prochainePosition);
				if(distancePoisson > sqDistanceMin && distancePoisson <= sqDistanceMax) {
					poissonsAPortee.add(poisson);
				}
			}
		}
		return poissonsAPortee;
	}


}
