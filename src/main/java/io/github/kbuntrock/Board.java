package io.github.kbuntrock;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kévin Buntrock
 */
public class Board {

	// Given at startup
	final int width;
	final int height;

	// Updated each turn
	final Team myTeam = new Team();
	final Team opponentTeam = new Team();
	Map<Integer, Poisson> poissonsById = new HashMap<>();
	Map<Integer, Monstre> monstresById = new HashMap<>();
	Radar radar = new Radar(this);

	Board() {
		width = 10000;
		height = 10000;
	}

	Board(final EScanner in) {
		width = 10000;
		height = 10000;

		initCreatures(in);
	}

	private void initCreatures(final EScanner in) {
		final int creatureCount = in.nextInt();
		for(int i = 0; i < creatureCount; i++) {
			final int id = in.nextInt();
			final int couleur = in.nextInt();
			final int espece = in.nextInt();
			if(espece == -1) {
				final Monstre monstre = new Monstre(id, null, null, couleur, espece);
				monstresById.put(monstre.id, monstre);
			} else {
				final Poisson poisson = new Poisson(id, null, null, couleur, espece);
				poissonsById.put(poisson.id, poisson);
			}
		}
	}

	void update(final EScanner in) {
		// Reset poissons
		poissonsById.values().stream().forEach(Poisson::resetPosition);
		//monstresById.values().stream().forEach(Poisson::resetPosition);

		// Read new data
		myTeam.readScore(in);
		opponentTeam.readScore(in);
		myTeam.readScans(in);
		opponentTeam.readScans(in);
		final int myDroneCount = in.nextInt();
		myTeam.resetRobots();
		for(int i = 0; i < myDroneCount; i++) {
			final RobotAllie robot = new RobotAllie(in);
			myTeam.robots.add(robot);
			myTeam.robotsById.put(robot.id, robot);
		}
		final int foeDroneCount = in.nextInt();
		opponentTeam.resetRobots();
		for(int i = 0; i < foeDroneCount; i++) {
			final RobotEnnemi robot = new RobotEnnemi(in);
			opponentTeam.robots.add(robot);
			opponentTeam.robotsById.put(robot.id, robot);
		}
		final int droneScanCount = in.nextInt();
		for(int i = 0; i < droneScanCount; i++) {
			final int droneId = in.nextInt();
			final int creatureId = in.nextInt();
			final Robot robot = myTeam.robotsById.get(droneId);
			if(robot != null) {
				robot.scans.add(creatureId);
				myTeam.scans.add(creatureId);
			} else {
				opponentTeam.robotsById.get(droneId).scans.add(creatureId);
			}

		}
		final int visibleCreatureCount = in.nextInt();
		for(int i = 0; i < visibleCreatureCount; i++) {
			final int creatureId = in.nextInt();
			final Poisson poisson = poissonsById.get(creatureId);
			if(poisson != null) {
				poisson.setPosition(in);
				poisson.setVitesse(in);
			} else {
				final Monstre monstre = monstresById.get(creatureId);
				monstre.setPosition(in);
				monstre.setVitesse(in);
				monstre.lastSeen = IO.turn;
				IO.info(
					"Vitesse monstre " + monstre.id + " calculee : " + monstre.vitesse + " - next position : "
						+ monstre.prochainePosition + " - pos actuelle : " + monstre.pos);
			}
		}
		updateMonstresNonVisibles();
		radar.init(in);
	}

	private void updateMonstresNonVisibles() {
		monstresById.values().stream().filter(m -> m.lastSeen < IO.turn).forEach(m -> {
			if(m.vitesse.x + m.vitesse.y != 0) {
				final Vecteur v = new Vecteur(m.vitesse.x, m.vitesse.y);
				v.adapt(Monstre.VITESSE_NON_AGRESSIVE);
				m.pos = m.pos.add(v, 2500, 10000);
				m.setVitesse(new Coord(v.x, v.y));
				IO.info(
					"On pense que le monstre " + m.id + " est en " + m.pos + " - vitesse " + m.vitesse + " - next pos "
						+ m.prochainePosition);
			}
		});
	}

	// TODO : à garder si on veut tenter de deviner la mise à jour de la direction d'un monstre via un autre drone
//	private Coord calculerVitesseMonstre(final Monstre monstre) {
//		Robot plusProcheRobot = null;
//		int minDistance = Integer.MAX_VALUE;
//		for(final Robot robot : myTeam.robots) {
//			final int distance = robot.pos.squareDistance(monstre.pos);
//			if(minDistance < distance) {
//				minDistance = distance;
//				plusProcheRobot = robot;
//			}
//		}
//		for(final Robot robot : opponentTeam.robots) {
//			final int distance = robot.pos.squareDistance(monstre.pos);
//			if(minDistance < distance) {
//				minDistance = distance;
//				plusProcheRobot = robot;
//			}
//		}
//		if(minDistance <= 640000) {
//			IO.info("monstre " + monstre.id + " dans un faisceau lumineux");
//			// On est en présence d'un robot dans un faisceau lumineux
//			return plusProcheRobot.pos.minus(monstre.pos);
//		}
//		// Le monstre n'est pas dans un faisceau lumineux, sa vitesse ne change pas
//		return monstre.vitesse;
//	}

}