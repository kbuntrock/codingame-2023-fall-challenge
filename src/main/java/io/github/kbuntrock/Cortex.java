package io.github.kbuntrock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Kévin Buntrock
 */
public class Cortex {

	int profondeurSauvegarde = 500;

	int[][] habitat = new int[][]{{2500, 5000}, {5000, 7500}, {7500, 10000}};

	Board board;

	int especeRecherchee[] = new int[]{2, 2};

	final Radio[] previousRadio = new Radio[]{null, null};

	final boolean[] peutChangerHabitat = new boolean[]{false, false};


	public Cortex(final Board board) {
		this.board = board;
	}

	public void explorerHabitat() {

		for(int i = 0; i < 2; i++) {
			final int fi = i;
			final Robot robot = board.myTeam.robots.get(i);

			final int profondeurMinimale = habitat[especeRecherchee[i]][0];
			final int profondeurMaximale = habitat[especeRecherchee[i]][1];
			if(robot.pos.y - profondeurSauvegarde < 0) {
				if(!peutChangerHabitat[i]) {
					// Descendre
					robot.action = Action.move(new Coord(robot.pos.x, robot.pos.y + 600), false);
					robot.action.message = "Going down for " + especeRecherchee[i];
				} else {
					// Remonter
					robot.action = Action.move(new Coord(robot.pos.x, robot.pos.y - 600), false);
					robot.action.message = "Going up";
					// On change d'espèce pour notre prochaine plongée
					if(peutChangerHabitat[i] && especeRecherchee[i] > 0) {
						especeRecherchee[i]--;
						peutChangerHabitat[i] = false;
						robot.action.message = "pro espece : " + especeRecherchee[i];
					}
				}
			} else {
				// Exploration
				peutChangerHabitat[i] = true;
				final List<Radio> radios = board.radar.filtered(robot.id, especeRecherchee[i]);
				boolean light = false;
				Coord visee = null;
				if(!radios.isEmpty()) {

					if(previousRadio[i] != null) {
						final Optional<Radio> opt = radios.stream().filter(r -> r.poisson.id == previousRadio[fi].poisson.id).findAny();
						if(opt.isEmpty()) {
							previousRadio[i] = null;
						} else {
							final Radio currentRadio = opt.get();
							visee = robot.pos.add(currentRadio.direction.direction, profondeurMinimale, profondeurMaximale);
							if(previousRadio[i].direction != currentRadio.direction) {
								// Changement de direction soudain.
								// On allume la lumière pour voir si on peut le rattraper ?
								light = true;
							}
						}
					}
					if(previousRadio[i] == null) {
						// On essaie de trouver une direction oposée à l'autre robot
						final Direction directionAutreRobot = robot.directionEntity(board.myTeam.robots.get(1 - i));
						final Optional<Radio> optRadio = radios.stream().filter(
							r -> directionAutreRobot.isOppositeXDirection(r.direction)).findAny();
						if(optRadio.isPresent()) {
							IO.info("Une radio correspond à la direction opposée au robot " + i + " : " + optRadio.get() + " par rapport à "
								+ directionAutreRobot);
						}
						final Radio currentRadio = optRadio.isPresent() ? optRadio.get() : radios.get(0);
						visee = robot.pos.add(currentRadio.direction.direction, profondeurMinimale, profondeurMaximale);
						previousRadio[i] = currentRadio;
					}
					robot.action = Action.move(visee, light);
					robot.action.message = "Exploration " + especeRecherchee[i];
				} else {
					// Remonter
					robot.action = Action.move(new Coord(robot.pos.x, robot.pos.y - 600), false);
					robot.action.message = "On remonte des profondeurs";
				}
			}
		}

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