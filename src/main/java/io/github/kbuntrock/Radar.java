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
