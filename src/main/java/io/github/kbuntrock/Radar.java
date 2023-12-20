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
		final int radarBlipCount = in.nextInt();
		for(int i = 0; i < radarBlipCount; i++) {
			final int droneId = in.nextInt();
			final int creatureId = in.nextInt();
			final String radar = in.next();
			final List<Radio> list = radios.computeIfAbsent(droneId, k -> new ArrayList<>());
			list.add(new Radio(Direction.valueOf(radar), board.poissonsById.get(creatureId)));
		}
	}

	public List<Radio> filtered(final int droneId, final int espece) {
		return radios.get(droneId).stream().filter(r -> r.poisson.espece == espece
				&& !board.myTeam.savedScans.contains(r.poisson.id)
				&& !board.myTeam.scans.contains(r.poisson.id))
			.collect(Collectors.toList());
	}
}
