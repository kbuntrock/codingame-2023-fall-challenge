package io.github.kbuntrock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kévin Buntrock
 */
public class Team {

	int score;
	List<Robot> robots = new ArrayList<>();

	Map<Integer, Robot> robotsById = new HashMap<>();

	Set<Integer> savedScans = new HashSet<>();

	Set<Integer> scans = new HashSet<>();

	void readScore(final EScanner in) {
		score = in.nextInt();
		robots = new ArrayList<>();
	}

	void readScans(final EScanner in) {
		final int myScanCount = in.nextInt();
		for(int i = 0; i < myScanCount; i++) {
			final int scanId = in.nextInt();
			savedScans.add(scanId);
			scans.remove(scanId);
		}
	}

	public void resetRobots() {
		robots.clear();
	}
}
