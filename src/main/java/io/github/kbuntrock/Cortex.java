package io.github.kbuntrock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kévin Buntrock
 */
public class Cortex {

	private static final List<Integer> anglesATester = new ArrayList<>();

	int profondeurSauvegarde = 500;

	int[][] habitat = new int[][]{{2500, 5000}, {5000, 7500}, {7500, 10000}};

	Board board;

	int especeRecherchee[] = new int[]{2, 2};

	final Radio[] previousRadio = new Radio[]{null, null};

	final Poisson[] poissonSuivi = new Poisson[]{null, null};

	final boolean[] peutChangerHabitat = new boolean[]{false, false};

	static {
		anglesATester.add(0);
		for(int i = 10; i < 180; i += 10) {
			anglesATester.add(-i);
			anglesATester.add(i);
		}
		anglesATester.add(180);
	}


	public Cortex(final Board board) {
		this.board = board;
	}

	public int trouverHabitatPlusProfondNonScanne() {
		int habitatPlusProfond = -1;
		for(final Poisson poisson : board.poissonsById.values().stream().filter(p -> !p.horsTerrain).collect(Collectors.toList())) {
			boolean scanned = board.myTeam.savedScans.contains(poisson.id);
			if(!scanned) {
				for(final Robot robot : board.myTeam.robots) {
					scanned = robot.scans.contains(poisson.id);
					if(scanned) {
						break;
					}
				}
			}
			if(!scanned && habitatPlusProfond < poisson.espece) {
				habitatPlusProfond = poisson.espece;
			}
		}
		return habitatPlusProfond;
	}

	public void explorerHabitat() {

		for(int i = 0; i < 2; i++) {
			final int fi = i;
			final Robot robot = board.myTeam.robots.get(i);

//			if(paniqueMode(robot)) {
//				continue;
//			}

			final int profondeurMinimale = habitat[especeRecherchee[i]][0];
			final int profondeurMaximale = habitat[especeRecherchee[i]][1];
			if(robot.pos.y - profondeurSauvegarde < 0) {
				if(!peutChangerHabitat[i]) {
					// Descendre
					final boolean light = IO.turn == 5 || IO.turn == 10;
					robot.action = Action.move(new Vecteur(robot.pos.x, robot.pos.y + 600), light);
					robot.action.message = "Going down for " + especeRecherchee[i];
				} else {

					if(robot.pos.y < 500) {
						// Changement d'habitat forcé
						// on vérifie si on a bien tout exploré (on a possiblement lâché des scans)
						final int habitatRestant = trouverHabitatPlusProfondNonScanne();
						if(habitatRestant >= 0) {
							especeRecherchee[i] = habitatRestant;
							peutChangerHabitat[i] = false;
							robot.action = Action.move(new Vecteur(robot.pos.x, robot.pos.y + 600), false);
							robot.action.message = "Re-going down for " + especeRecherchee[i];
							continue;
						}
					}
					// Remonter
					robot.action = Action.move(new Vecteur(robot.pos.x, robot.pos.y - 600), false);
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

				final Poisson poissonATrouver = trouverPoissonPlusProche(robot, especeRecherchee[i], poissonSuivi[1 - i]);

				final boolean light = IO.turn % 5 == 0 && IO.turn != 0;

				poissonSuivi[i] = poissonATrouver;
				if(poissonATrouver != null) {

					final Vecteur vecteurVisee = new Vecteur(robot.pos, poissonATrouver.milieuRectangle()).adapt(600);
					robot.action = Action.move(robot.pos.add(vecteurVisee), light);
					robot.action.message = "P" + poissonATrouver.id + "-" + especeRecherchee[i];
					IO.info("Poisson recherché pour robot " + robot.id + " : " + poissonATrouver.id);
				} else {
					// Remonter
					robot.action = Action.move(new Vecteur(robot.pos.x, robot.pos.y - 600), light);
					robot.action.message = "On remonte des profondeurs";
				}
			}
			esquiverMonstre(robot, robot.action.pos);

		}

	}

	private Poisson trouverPoissonPlusProche(final Robot robot, final int especeRecherchee, final Poisson exclusion) {
		final Robot autreRobot = board.myTeam.getOtherRobot(robot);
		// Trouve les poissons non encore scannés de l'espèce demandée
		final List<Poisson> poissons = board.poissonsById.values().stream().filter(p ->
				p.espece == especeRecherchee
					&& !p.horsTerrain
					&& !board.myTeam.savedScans.contains(p.id) && !robot.scans.contains(p.id) && !autreRobot.scans.contains(p.id)
					&& (exclusion == null || exclusion.id != p.id))
			.collect(Collectors.toList());
		if(poissons.isEmpty()) {
			return null;
		}
		double minSqrDistance = Double.MAX_VALUE;
		Poisson result = null;
		for(final Poisson p : poissons) {
			final double sqrDistance = robot.pos.squareDistance(p.milieuRectangle());
			if(sqrDistance < minSqrDistance) {
				minSqrDistance = sqrDistance;
				result = p;
			}
		}
		return result;
	}

	// TODO : à supprimer quand l'esquive sera au point
	private boolean paniqueMode(final Robot robot) {
		final List<Monstre> monstresAPortee = new ArrayList<>();
		for(final Monstre monstre : board.monstresById.values()) {
			if(monstre.pos != null && (robot.pos.intDistance(monstre.pos.add(monstre.vitesse)) < 1200
				|| robot.pos.intDistance(monstre.pos) < 1200)) {
				monstresAPortee.add(monstre);
			}
		}
		if(!monstresAPortee.isEmpty()) {
			final Vecteur v = new Vecteur(monstresAPortee.get(0).pos, robot.pos).adapt(1000);
			robot.action = Action.move(robot.pos.add(v), false);
			robot.action.message = "Oh my gosh";
			return true;
		}

		return false;
	}

	private void esquiverMonstre(final Robot robot, final Vecteur visee) {

		final Vecteur vitesse = visee.minus(robot.pos);
		final double longueur = vitesse.longueur();

		Vecteur newVisee = null;
		int newAngle = 0;

		for(final Integer angle : anglesATester) {
			newVisee = collisionAvecMonstre(robot, visee, longueur, angle);
			if(newVisee != null) {
				newAngle = angle;
				break;
			}
		}
		if(newVisee == null) {
			robot.action = Action.none();
			robot.action.message = "No escape";
		} else if(newAngle == 0) {
			IO.info("Pas de correction pour robot " + robot.id);
		} else {
			IO.info("Correction d'angle " + newAngle + " pour robot " + robot.id);
			IO.info("Visee initiale " + visee + " corrigée en " + newVisee);
			final Action oldAction = robot.action;
			robot.action = Action.move(newVisee, robot.action.light);
			robot.action.message = oldAction.message + " - " + newAngle;
		}
	}

	private Vecteur collisionAvecMonstre(final Robot robot, final Vecteur visee, final double longueur, final int angle) {

		Vecteur newVisee = visee;

		if(angle != 0) { // angle != 0

			// x3 = AB * Cos(angle) + x1
			// y3 = AB * Sin(angle) + y1
//			final double x3 = (longueur * Math.cos(angle)) + robot.pos.x;
//			final double y3 = (longueur * Math.sin(angle)) + robot.pos.y;

			//10° × π/180
			final double rad = angle * Math.PI / 180;
			final Vecteur vecteur = visee.minus(robot.pos);
			final double x3 = (vecteur.x * Math.cos(rad)) - (vecteur.y * Math.sin(rad));
			final double y3 = (vecteur.y * Math.cos(rad)) + (vecteur.x * Math.sin(rad));
			newVisee = new Vecteur(x3, y3).add(robot.pos);
		}
		// Bloc de vérification
		// TODO : à supprimer lorsqu'au point
		final int newLongueur = (int) newVisee.minus(robot.pos).longueur();
		if(newLongueur > 600) {
			throw new RuntimeException("Nouvelle visee impossible pour angle " + angle + " -> " + newLongueur);
		}

		for(final Monstre monstre : board.monstresById.values().stream().filter(m -> m.pos != null).collect(Collectors.toList())) {
			if(robot.collide(monstre, newVisee)) {
				// Collision avec un monstre
				// Position null
				return null;
			}
		}
		// On a un potentiel gagnant qu'on remonte
		return newVisee;
	}


	public Poisson trouverPoissonNonScannePlusProche() {
		final Robot robot = board.myTeam.robots.get(0);
		Poisson plusProche = null;
		double minDistance = Double.MAX_VALUE;
		for(final Poisson poisson : board.poissonsById.values()) {
			if(poisson.visible() && !board.myTeam.savedScans.contains(poisson.id) && !robot.scans.contains(poisson.id)) {
				final Vecteur prochainePosition = poisson.prochainePosition;
				IO.info("Poisson " + poisson.id + " : " + poisson.pos.toString() + " v : " + poisson.vitesse.toString() + " -> "
					+ prochainePosition);
				final double distance = robot.pos.squareDistance(prochainePosition);
				if(minDistance > distance) {
					minDistance = distance;
					plusProche = poisson;
				}
			}
		}
		IO.info("Poisson plus proche : " + (plusProche == null ? "aucun" : plusProche.id));
		return plusProche;
	}

	public Vecteur calculerMouvement(final Poisson poisson) {
		final Vecteur visee = poisson.pos.add(poisson.vitesse);
		IO.info("Future position poisson : " + visee);
		final Robot robot = board.myTeam.robots.get(0);

		final Vecteur vecteur = new Vecteur(robot.pos, visee);
		return robot.pos.add(vecteur.adapt(600));
	}

	public List<Poisson> trouverPoissonsAPortee(final Vecteur position, final int distanceMin, final int distanceMax) {
		final double sqDistanceMax = distanceMax * distanceMax;
		final double sqDistanceMin = distanceMin * distanceMin;
		final List<Poisson> poissonsAPortee = new ArrayList<>();
		for(final Poisson poisson : board.poissonsById.values()) {
			if(!board.myTeam.savedScans.contains(poisson.id)) {
				final Vecteur prochainePosition = poisson.prochainePosition;
				final double distancePoisson = position.squareDistance(prochainePosition);
				if(distancePoisson > sqDistanceMin && distancePoisson <= sqDistanceMax) {
					poissonsAPortee.add(poisson);
				}
			}
		}
		return poissonsAPortee;
	}


}
