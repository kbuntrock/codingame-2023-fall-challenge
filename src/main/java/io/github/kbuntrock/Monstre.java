package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class Monstre extends Poisson {

	public static final int EAT_RANGE = 300;

	public static int VITESSE_MAX = 540;
	public static int VITESSE_NON_AGRESSIVE = 270;

	int lastSeen = Integer.MAX_VALUE;

	public Monstre(final int id, final Vecteur pos, final Vecteur vitesse, final int couleur, final int espece) {
		super(id, pos, vitesse, couleur, espece);
		type = EntityType.MONSTRE;
	}

	public void resetPositionIfTooOld() {
		// Reset si perdu de vue plus de 3 tours avant
		if(IO.turn - lastSeen > 3) {
			pos = null;
			vitesse = null;
			prochainePosition = null;
			lastSeen = Integer.MAX_VALUE;
		}

	}
}
