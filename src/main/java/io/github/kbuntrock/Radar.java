package io.github.kbuntrock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Kévin Buntrock
 */
public class Radar {

	private final Board board;

	Map<Integer, List<Radio>> radios = new HashMap<>();

	public Radar(final Board board) {
		this.board = board;
	}

	public void init(final EScanner in) {
		radios.clear();
		board.poissonsById.values().stream().forEach(Poisson::resetCurrentMinMax);

		final int radarBlipCount = in.nextInt();
		for(int i = 0; i < radarBlipCount; i++) {
			final int robotId = in.nextInt();
			final int creatureId = in.nextInt();
			final String radar = in.next();
			final List<Radio> list = radios.computeIfAbsent(robotId, k -> new ArrayList<>());
			final Poisson p = board.poissonsById.get(creatureId);
			if(p != null) {
				// On peut être en présence du scan d'un monstre. Non géré pour l'instant
				p.horsTerrain = false;
				// "Ancien" poisson (précédente version de ses coordonnées)
				final Poisson ap = IO.lastTurnData.poissonById.get(creatureId);

				list.add(new Radio(Direction.valueOf(radar), p));

				final Direction direction = Direction.valueOf(radar);
				final Robot robot = board.myTeam.robotsById.get(robotId);
				switch(direction) {
					case TL -> {
						p.currentMaxX = Math.min(robot.pos.x, p.currentMaxX);
						p.currentMaxY = Math.min(p.currentMaxY, Math.min(robot.pos.y, p.absoluteMaxY));
					}
					case TR -> {
						p.currentMinX = Math.max(robot.pos.x, p.currentMinX);
						p.currentMaxY = Math.min(p.currentMaxY, Math.min(robot.pos.y, p.absoluteMaxY));
					}
					case BR -> {
						p.currentMinX = Math.max(robot.pos.x, p.currentMinX);
						p.currentMinY = Math.max(p.currentMinY, Math.max(robot.pos.y, p.absoluteMinY));
					}
					case BL -> {
						p.currentMaxX = Math.min(robot.pos.x, p.currentMaxX);
						p.currentMinY = Math.max(p.currentMinY, Math.max(robot.pos.y, p.absoluteMinY));
					}
				}

				if(ap != null) {
					if(p.currentMaxX > ap.currentMaxX) {
						p.currentMaxX = Math.min(Math.min(p.currentMaxX, ap.currentMaxX + 200), 10000);
					}
					if(p.currentMinX < ap.currentMinX) {
						p.currentMinX = Math.max(Math.max(p.currentMinX, ap.currentMinX - 200), 0);
					}
					if(p.currentMaxY > ap.currentMaxY) {
						p.currentMaxY = Math.min(Math.min(p.currentMaxY, ap.currentMaxY + 200), p.absoluteMaxY);
					}
					if(p.currentMinY < ap.currentMinY) {
						p.currentMinY = Math.max(Math.max(p.currentMinY, ap.currentMinY - 200), p.absoluteMinY);
					}
				}

				if(creatureId == 8) {
					final boolean existe = ap != null;
					IO.info("Ancien poisson 8 existe? " + (existe) + " : " + (existe ? (ap.currentMinX + ":" + ap.currentMaxX) : "empty"));
					IO.info("Après correction p 8 : " + p.currentMinX + ":" + p.currentMaxX);
				}
			}

		}
	}

	public List<Radio> filtered(final int droneId, final int espece) {
		return radios.get(droneId).stream().filter(r -> r.poisson.espece == espece
				&& !board.myTeam.savedScans.contains(r.poisson.id)
				&& !board.myTeam.scans.contains(r.poisson.id))
			.collect(Collectors.toList());
	}
}
