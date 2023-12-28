package io.github.kbuntrock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kévin Buntrock
 */
public class Team {

	int score;
	List<Robot> robots = new ArrayList<>();

	Map<Integer, Robot> robotsById = new HashMap<>();

	private final Map<Integer, Poisson> savedScans = new HashMap<>();

	private final Map<Integer, Poisson> scans = new HashMap<>();

	void readScore(final EScanner in) {
		score = in.nextInt();
		robots = new ArrayList<>();
	}

	void readScans(final EScanner in, final Board board) {
		savedScans.clear();
		scans.clear();
		final int myScanCount = in.nextInt();
		for(int i = 0; i < myScanCount; i++) {
			final int scanId = in.nextInt();
			savedScans.put(scanId, board.poissonsById.get(scanId));
		}
	}

	public void addRobotScan(final Poisson poisson) {
		scans.put(poisson.id, poisson);
	}

	public Map<Integer, Poisson> getScans() {
		return scans;
	}

	public Map<Integer, Poisson> getSavedScans() {
		return savedScans;
	}

	public void resetRobots() {
		robots.clear();
	}

	/**
	 * Calcule un score pour une équipe si elle sauvegarde maintenant ses poissons
	 */
	public int hypotheticalScore(final Collection<Poisson> hScans, final Team otherTeam) {
		final int[] compteurEspece = {0, 0, 0};
		final int[] compteurCouleur = {0, 0, 0, 0};
		for(final Poisson p : savedScans.values()) {
			compteurEspece[p.espece]++;
			compteurCouleur[p.couleur]++;
		}
		final int[] compteurEspeceOT = {0, 0, 0};
		final int[] compteurCouleurOT = {0, 0, 0, 0};
		for(final Poisson p : otherTeam.savedScans.values()) {
			compteurEspeceOT[p.espece]++;
			compteurCouleurOT[p.couleur]++;
		}
		int calculatedScore = this.score;
		for(final Poisson p : hScans) {
			calculatedScore += (p.espece + 1) * (otherTeam.savedScans.get(p.id) == null ? 2 : 1);
			if(compteurEspece[p.espece] < 4) {
				compteurEspece[p.espece]++;
				if(compteurEspece[p.espece] == 4) {
					calculatedScore += compteurEspeceOT[p.espece] < 4 ? 8 : 4;
				}
			}
			if(compteurCouleur[p.couleur] < 3) {
				compteurCouleur[p.couleur]++;
				if(compteurCouleur[p.couleur] == 3) {
					calculatedScore += compteurCouleurOT[p.couleur] < 3 ? 6 : 3;
				}
			}
		}
		return calculatedScore;
	}
}
