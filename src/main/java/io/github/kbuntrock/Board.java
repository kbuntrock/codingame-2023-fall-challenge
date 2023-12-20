package io.github.kbuntrock;

import java.util.Collection;
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
	private Cell[][] cells;
	int myRadarCooldown;
	int myTrapCooldown;
	Map<Integer, Poisson> poissonsById = new HashMap<>();
	Collection<Coord> myRadarPos;
	Collection<Coord> myTrapPos;
	Radar radar = new Radar(this);

	Board() {
		width = 10000;
		height = 10000;
	}

	Board(final EScanner in) {
//		width = in.nextInt();
//		height = in.nextInt();
		width = 10000;
		height = 10000;

		//	int creatureCount = in.nextInt();
//        for (int i = 0; i < creatureCount; i++) {
//		int creatureId = in.nextInt();
//		int color = in.nextInt();
//		int type = in.nextInt();
//	}
		final int creatureCount = in.nextInt();
		for(int i = 0; i < creatureCount; i++) {
			final Poisson poisson = new Poisson(in);
			poissonsById.put(poisson.id, poisson);
//			if(entity.type == EntityType.ALLY_ROBOT) {
//				myTeam.robots.add(entity);
//			} else if(entity.type == EntityType.ENEMY_ROBOT) {
//				opponentTeam.robots.add(entity);
//			} else if(entity.type == EntityType.RADAR) {
//				myRadarPos.add(entity.pos);
//			} else if(entity.type == EntityType.TRAP) {
//				myTrapPos.add(entity.pos);
//			}
		}
	}

	void update(final EScanner in) {
		// Reset poissons
		poissonsById.values().stream().forEach(Poisson::resetPosition);

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
			poisson.setPosition(in);
			poisson.setVitesse(in);
		}
		radar.init(in);
	}

	// Update autres ligues
//	void update(final Scanner in) {
//		// Read new data
//		myTeam.readScore(in);
//		opponentTeam.readScore(in);
//		cells = new Cell[height][width];
//		for(int y = 0; y < height; y++) {
//			for(int x = 0; x < width; x++) {
//				cells[y][x] = new Cell(in);
//			}
//		}
//		final int entityCount = in.nextInt();
//		myRadarCooldown = in.nextInt();
//		myTrapCooldown = in.nextInt();
//		entitiesById = new HashMap<>();
//		myRadarPos = new ArrayList<>();
//		myTrapPos = new ArrayList<>();
//		for(int i = 0; i < entityCount; i++) {
//			final Entity entity = new Entity(in);
//			entitiesById.put(entity.id, entity);
//			if(entity.type == EntityType.ALLY_ROBOT) {
//				myTeam.robots.add(entity);
//			} else if(entity.type == EntityType.ENEMY_ROBOT) {
//				opponentTeam.robots.add(entity);
//			} else if(entity.type == EntityType.RADAR) {
//				myRadarPos.add(entity.pos);
//			} else if(entity.type == EntityType.TRAP) {
//				myTrapPos.add(entity.pos);
//			}
//		}
//	}

	boolean cellExist(final Coord pos) {
		return (pos.x >= 0) && (pos.y >= 0) && (pos.x < width) && (pos.y < height);
	}

	Cell getCell(final Coord pos) {
		return cells[pos.y][pos.x];
	}
}