package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class Monstre extends Poisson {

	public static int VITESSE_MAX = 540;
	public static int VITESSE_NON_AGRESSIVE = 270;

	Coord lastKnownPos;

	Coord lastKnownVitesse;

	int lastSeen = Integer.MAX_VALUE;

	public Monstre(final int id, final Coord pos, final Coord vitesse, final int couleur, final int espece) {
		super(id, pos, vitesse, couleur, espece);
		type = EntityType.MONSTRE;
	}
}
