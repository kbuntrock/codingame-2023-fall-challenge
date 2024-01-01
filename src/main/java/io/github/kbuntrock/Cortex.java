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
 * - N'imposer la chasse que d'un seul poisson d'expèce 2 par robot.
 *
 * @author Kévin Buntrock
 */
public class Cortex {

	private static final List<Integer> anglesATester = new ArrayList<>();

	int profondeurSauvegarde = 500;

	Board board;

	int especeRecherchee[] = new int[]{2, 2};

	final Poisson[] poissonSuivi = new Poisson[]{null, null};

	final boolean[] bloqueRetour = new boolean[]{false, false};

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
			final Robot robot = board.myTeam.robots.get(i);
			poissonSuivi[i] = null;

			if(robot.emergency == 1) {
				robot.action = Action.none();
				robot.action.message = "Crisis";
				continue;
			}

			//				final boolean light =
//					robot.battery >= 5 && (robot.pos.y > 2500) && (IO.turn == 4 || IO.turn == 7 || (IO.turn > 7 && IO.turn % 3 == 0));
			final boolean light = turnLightOn(robot, poissonsNonScannes);

			if(IO.turn < 3) {
				// Descente controlée pour le robot excentré
				if(robot.pos.x > 5000 && robot.pos.x < 7900) {
					robot.action = Action.move(robot.pos.add(new Vecteur(2, 5).adapt(600)), light);
					robot.action.message = "Down ctrld";
					continue;
				} else if(robot.pos.x < 5000 && robot.pos.x > 2100) {
					robot.action = Action.move(robot.pos.add(new Vecteur(-2, 5).adapt(600)), light);
					robot.action.message = "Down ctrlg";
					continue;
				}
			}

			// Exploration
			if((i == 0 ? (scoreRbt1 > board.myTeam.score) : (scoreRbt2 > board.myTeam.score)) &&
				(scoreEquipe >= winningScore || (!board.opponentTeam.getScans().isEmpty() && scoreAdversaire >= winningScore))) {
				// Remonter pour la victoire
				robot.action = Action.move(new Vecteur(robot.pos.x, robot.pos.y - 600), light);
				robot.action.message = "Home FW";
				bloqueRetour[i] = true;
				if(!board.opponentTeam.getScans().isEmpty() && scoreAdversaire >= winningScore) {
					robot.action.message = "Home FWE";
				}
			} else {

				if(bloqueRetour[i]) {
					if(i == 0 ? (scoreRbt1 > board.myTeam.score) : (scoreRbt2 > board.myTeam.score)) {
						// Remonter
						robot.action = Action.move(new Vecteur(robot.pos.x, robot.pos.y - 600), light);
						robot.action.message = "Home B";
					} else {
						bloqueRetour[i] = false;
						// On débloque les autres expèces
						especeRecherchee[i] = 0;
					}
				}
				if(!bloqueRetour[i]) {
					// Avant toute chose, on regarde si on peut chasser un poisson
					final Poisson poissonAChasser = poissonAChasser(robot);
					if(poissonAChasser != null) {
						IO.info(robot.id + " -> Poisson " + poissonAChasser.id + " pris en chasse ");
						chasserPoisson(robot, poissonAChasser, light);
//						final Vecteur vecteurVisee = new Vecteur(robot.pos, poissonAChasser.milieuRectangle()).adapt(600);
//						robot.action = Action.move(robot.pos.add(vecteurVisee), light);
//						robot.action.message = "zook zook " + poissonAChasser.id;
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
							bloqueRetour[i] = true;
						}
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

	private boolean tousPoissonsEspeceScannes(final int espece) {
		long compteur = board.myTeam.getSavedScans().values().stream().filter(p -> p.espece == espece).count();
		compteur += board.myTeam.getScans().values().stream().filter(p -> p.espece == espece).count();
		return compteur == 4;
	}

	private List<Poisson> listPoissonsNonScannes() {
		return board.poissonsById.values().stream().filter(p ->
				!p.horsTerrain
					&& !board.myTeam.getSavedScans().keySet().contains(p.id) && !board.myTeam.getScans().keySet().contains(p.id))
			.collect(Collectors.toList());
	}

	private Poisson trouverPoissonPlusProche(final Robot robot, final int especeRechercheeMin, final Poisson exclusion,
		final List<Poisson> listPoissons) {
		IO.info("Espece recherche min pour robot " + especeRechercheeMin + " -> " + robot.id);
		// Trouve les poissons non encore scannés de l'espèce demandée
		final List<Poisson> poissons = listPoissons.stream().filter(p ->
				(especeRechercheeMin == -1 || especeRechercheeMin <= p.espece) && (exclusion == null || exclusion.id != p.id))
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

		for(final Poisson p : board.poissonsById.values().stream().filter(p ->
				!board.opponentTeam.getSavedScans().containsKey(p.id) && !board.opponentTeam.getScans().containsKey(p.id)).
			collect(Collectors.toList())) {

			final boolean poissonAGauche = p.currentMaxX <= robot.pos.x;
			final boolean poissonADroite = robot.pos.x <= p.currentMinX;
			if(!poissonAGauche && !poissonADroite) {
				// On ne fait rien dans le cas très particulier où on est sur la même ligne
				continue;
			}
			boolean poissonChasseable = poissonAGauche ? p.milieuRectangle().x < 1300 : p.milieuRectangle().x > 8700;
			final Rectangle zoneDeChasseRectangle = poissonAGauche ?
				new Rectangle(new Vecteur(0, robot.pos.y - 2000),
					new Vecteur(robot.pos.x, robot.pos.y - 2000),
					new Vecteur(robot.pos.x, robot.pos.y + 2000),
					new Vecteur(0, robot.pos.y + 2000)) :
				new Rectangle(new Vecteur(robot.pos.x, robot.pos.y - 2000),
					new Vecteur(10000, robot.pos.y - 2000),
					new Vecteur(10000, robot.pos.y + 2000),
					new Vecteur(robot.pos.x, robot.pos.y + 2000));
			if(poissonChasseable) {
				// Pas de robot ennemi entre notre robot et le bord où chasser le poisson
				poissonChasseable =
					robotsEnnemis.stream().filter(re -> zoneDeChasseRectangle.pointInside(re.pos)).count() == 0;
			}

//			IO.info(
//				robot.id + " -> Poisson " + p.id + " (" + p.pos + ") chassable ? " + poissonChasseable + " car zone sans ennemie : "
//					+ zoneDeChasseRectangle);
			if(poissonChasseable) {
				poissonChasseable = zoneDeChasseRectangle.poissonInside(p);
			}
			if(poissonChasseable) {
				// Déterminer si poisson dans un rectangle délimitant la zone de chasse
				final Triangle zoneDeChasseTriangle = poissonAGauche ?
					new Triangle(new Vecteur(0, robot.pos.y - 1000),
						new Vecteur(robot.pos.x, robot.pos.y),
						new Vecteur(0, robot.pos.y + 1000)) :
					new Triangle(new Vecteur(10000, robot.pos.y - 1000),
						new Vecteur(robot.pos.x, robot.pos.y),
						new Vecteur(10000, robot.pos.y + 1000));
				poissonChasseable = zoneDeChasseTriangle.poissonInside(p);
				IO.info(
					robot.id + " -> Poisson " + p.id + " (" + p.pos + ") chassable ? " + poissonChasseable + " pour zone : "
						+ zoneDeChasseTriangle);

			}
			if(poissonChasseable) {
				// Pas de robot ennemi à moins de "x" de la zone du poisson
				final boolean localisationPrecise = p.pos != null;
				poissonChasseable =
					robotsEnnemis.stream().filter(re -> re.pos.intDistance(p.milieuRectangle()) < (localisationPrecise ? 2000 : 1000))
						.count() == 0;
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

	public void chasserPoisson(final Robot robot, final Poisson poisson, final boolean light) {
		boolean gauche = false;
		Vecteur localisation = poisson.pos;

		if(localisation == null) {
			localisation = poisson.milieuRectangle();
		}
		if(localisation.x - 5000 < 0) {
			gauche = true;
		}
		final Vecteur visee = new Vecteur(robot.pos, new Vecteur(localisation.x + (gauche ? 300 : -300), localisation.y)).adapt(600);
		robot.action = Action.move(robot.pos.add(visee), light);
		robot.action.message = "zou p" + poisson.id;

//		final Vecteur vecteurVisee = new Vecteur(robot.pos, poissonAChasser.milieuRectangle()).adapt(600);
//						robot.action = Action.move(robot.pos.add(vecteurVisee), light);
	}


}
