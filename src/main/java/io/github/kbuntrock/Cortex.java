package io.github.kbuntrock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Idées :
 * - Trianguler avec la capture des robots adverses
 * - Chasser les poissons hors de la carte
 * - mieux utiliser le fait de compter les points pour remonter
 * - mieux calculer quand allumer la lumière
 * - trouver le prochain poisson à capturer pour un combo
 *
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
		if(habitatPlusProfond == -1) {
			// On s'autorise à rechercher des poissons déjà scannés par l'autre robot, au cas ou ce dernier les relache
			for(final Poisson poisson : board.poissonsById.values().stream().filter(p -> !p.horsTerrain).collect(Collectors.toList())) {
				final boolean scanned = board.myTeam.getSavedScans().keySet().contains(poisson.id);
				if(!scanned && habitatPlusProfond < poisson.espece) {
					habitatPlusProfond = poisson.espece;
				}
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

		final List<Poisson> poissonsNonScannes = listPoissonsNonScannes();

		for(int i = 0; i < 2; i++) {
			final int fi = i;
			final Robot robot = board.myTeam.robots.get(i);

			// Avant toute chose, on regarde si on peut chasser un poisson
			final Poisson poissonAChasser = poissonAChasser(robot);
			if(poissonAChasser != null) {
				IO.info(robot.id + " -> Poisson " + poissonAChasser.id + " pris en chasse ");
				final Vecteur vecteurVisee = new Vecteur(robot.pos, poissonAChasser.milieuRectangle()).adapt(600);
				final boolean light = turnLightOn(robot, poissonsNonScannes);
				robot.action = Action.move(robot.pos.add(vecteurVisee), light);
				robot.action.message = "zook zook " + poissonAChasser.id;
				continue;
			}

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

//				final boolean light =
//					robot.battery >= 5 && (robot.pos.y > 2500) && (IO.turn == 4 || IO.turn == 7 || (IO.turn > 7 && IO.turn % 3 == 0));
				final boolean light = turnLightOn(robot, poissonsNonScannes);

				if((i == 0 ? (scoreRbt1 > board.myTeam.score) : (scoreRbt2 > board.myTeam.score)) && scoreEquipe >= winningScore) {
					// Remonter pour la victoire
					robot.action = Action.move(new Vecteur(robot.pos.x, robot.pos.y - 600), light);
					robot.action.message = "Home FW";
				} else {
					final Poisson poissonATrouver = trouverPoissonPlusProche(robot, especeRecherchee[i], poissonSuivi[1 - i],
						poissonsNonScannes);

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

	private List<Poisson> listPoissonsNonScannes() {
		return board.poissonsById.values().stream().filter(p ->
				!p.horsTerrain
					&& !board.myTeam.getSavedScans().keySet().contains(p.id) && !board.myTeam.getScans().keySet().contains(p.id))
			.collect(Collectors.toList());
	}

	private Poisson trouverPoissonPlusProche(final Robot robot, final int especeRecherchee, final Poisson exclusion,
		final List<Poisson> listPoissons) {
		// Trouve les poissons non encore scannés de l'espèce demandée
		final List<Poisson> poissons = listPoissons.stream().filter(p ->
				(especeRecherchee == -1 || p.espece == especeRecherchee) && (exclusion == null || exclusion.id != p.id))
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

	private boolean turnLightOn(final Robot robot, final List<Poisson> poissonsNonScannes) {

		if(robot.battery < 5 || IO.lastTurnLightOn.getOrDefault(robot.id, -1) == IO.turn - 1) {
			return false;
		}

		for(final Poisson p : poissonsNonScannes) {
			final double distance = robot.pos.intDistance(p.milieuRectangle());
			if(distance >= 500 && distance <= 2500) {
				IO.info("robot " + robot.id + " turn light on for fish " + p.id);
				return true;
			}
		}
		return false;
	}

	private Poisson poissonAChasser(final Robot robot) {

		final List<Robot> robotsEnnemis = board.opponentTeam.robots;
		final List<Poisson> poissonsChasseables = new ArrayList<>();

		for(final Poisson p : board.poissonsById.values()) {
			final boolean poissonAGauche = p.currentMaxX < robot.pos.x;
			final boolean poissonADroite = p.currentMinX > robot.pos.x;
			if(!poissonAGauche && !poissonADroite) {
				// On ne fait rien dans le cas très particulier où on est sur la même ligne
				continue;
			}
			// Pas de robot ennemi entre notre robot et le bord où chasser le poisson
			// TODO : assouplir en effectuant une vérification en y pour rendre l'algo plus efficasse
			boolean poissonChasseable =
				robotsEnnemis.stream().filter(re -> poissonAGauche ? re.pos.x <= robot.pos.x : re.pos.x >= robot.pos.x).count() == 0;
			if(poissonChasseable) {
				// Déterminer si poisson dans un rectangle délimitant la zone de chasse
				final Rectangle zoneDeChasse = poissonAGauche ?
					new Rectangle(new Vecteur(0, robot.pos.y - 2000),
						new Vecteur(robot.pos.x, robot.pos.y - 2000),
						new Vecteur(robot.pos.x, robot.pos.y + 2000),
						new Vecteur(0, robot.pos.y + 2000)) :
					new Rectangle(new Vecteur(robot.pos.x, robot.pos.y - 2000),
						new Vecteur(10000, robot.pos.y - 2000),
						new Vecteur(10000, robot.pos.y + 2000),
						new Vecteur(robot.pos.x, robot.pos.y + 2000));
				poissonChasseable = zoneDeChasse.poissonInside(p);
				IO.info(
					robot.id + " -> Poisson " + p.id + "(" + p.pos + ") chassable pour zone : " + zoneDeChasse + " ? " + poissonChasseable);

			}
			if(poissonChasseable) {
				// Pas de robot ennemi à moins de "x" de la zone du poisson
				poissonChasseable =
					robotsEnnemis.stream().filter(re -> re.pos.intDistance(p.milieuRectangle()) < 2000).count() == 0;
				IO.info(robot.id + " -> Poisson " + p.id + " chassable après repérage ennemis? " + poissonChasseable);
			}
			if(poissonChasseable) {
				poissonsChasseables.add(p);
			}
		}
		if(poissonsChasseables.isEmpty()) {
			return null;
		} else {
			return trouverPoissonPlusProche(robot, -1, null, poissonsChasseables);
		}
	}


}
