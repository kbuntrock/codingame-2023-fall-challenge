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

	Board board;

	int especeRecherchee[] = new int[]{2, 2};

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
			boolean scanned = board.myTeam.getSavedScans().keySet().contains(poisson.id);
			if(!scanned) {
				scanned = board.myTeam.getScans().keySet().contains(poisson.id);
			}
			if(!scanned && habitatPlusProfond < poisson.espece) {
				habitatPlusProfond = poisson.espece;
			}
		}
		return habitatPlusProfond;
	}

	public void explorerHabitat() {

		final int scoreRbt1 = board.myTeam.hypotheticalScore(board.myTeam.robots.get(0).getScans().values(), board.opponentTeam);
		final int scoreRbt2 = board.myTeam.hypotheticalScore(board.myTeam.robots.get(1).getScans().values(), board.opponentTeam);
		final int scoreEquipe = board.myTeam.hypotheticalScore(board.myTeam.getScans().values(), board.opponentTeam);
		final int scoreAdversaire = board.opponentTeam.hypotheticalScore(board.opponentTeam.getScans().values(), board.myTeam);
		final int winningScore = board.getWinningScore();

		for(int i = 0; i < 2; i++) {
			final int fi = i;
			final Robot robot = board.myTeam.robots.get(i);

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

				final boolean light =
					robot.battery >= 5 && (robot.pos.y > 2500) && (IO.turn == 4 || IO.turn == 7 || (IO.turn > 7 && IO.turn % 3 == 0));

				if(scoreRbt1 > board.myTeam.score && scoreEquipe >= winningScore) {
					// Remonter pour la victoire
					robot.action = Action.move(new Vecteur(robot.pos.x, robot.pos.y - 600), light);
					robot.action.message = "Home FW";
				} else {
					final Poisson poissonATrouver = trouverPoissonPlusProche(robot, especeRecherchee[i], poissonSuivi[1 - i]);

					poissonSuivi[i] = poissonATrouver;
					if(poissonATrouver != null) {

						final Vecteur vecteurVisee = new Vecteur(robot.pos, poissonATrouver.milieuRectangle()).adapt(600);
						robot.action = Action.move(robot.pos.add(vecteurVisee), light);
						robot.action.message = "P" + poissonATrouver.id + "-" + especeRecherchee[i];
						IO.info("Poisson recherché pour robot " + robot.id + " : " + poissonATrouver.id);
					} else {
						// Remonter
						robot.action = Action.move(new Vecteur(robot.pos.x, robot.pos.y - 600), light);
						robot.action.message = "Home";
					}
				}

			}
			esquiverMonstre(robot, robot.action.pos);

		}

		IO.info("Score robot 1 si remonte immédiatement : " + scoreRbt1);
		IO.info("Score robot 2 si remonte immédiatement : " + scoreRbt2);
		IO.info("Score équipe si remonte immédiatement : " + scoreEquipe);
		IO.info("Score autre équipe si remonte immédiatement : " + scoreAdversaire);
		IO.info("Winning score : " + winningScore);
	}

	private Poisson trouverPoissonPlusProche(final Robot robot, final int especeRecherchee, final Poisson exclusion) {
		// Trouve les poissons non encore scannés de l'espèce demandée
		final List<Poisson> poissons = board.poissonsById.values().stream().filter(p ->
				p.espece == especeRecherchee
					&& !p.horsTerrain
					&& !board.myTeam.getSavedScans().keySet().contains(p.id) && !board.myTeam.getScans().keySet().contains(p.id)
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


}
