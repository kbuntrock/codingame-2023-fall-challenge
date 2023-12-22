package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class Poisson extends Entity {

	final int couleur;
	final int espece;

	Coord vitesse;

	Coord prochainePosition;

	Poisson(final int id, final Coord pos, final Coord vitesse, final int couleur, final int espece) {
		super(id, pos, EntityType.POISSON);
		this.couleur = couleur;
		this.espece = espece;
		this.vitesse = vitesse;
		setProchainePosition();
	}

	void setPosition(final EScanner in) {
		pos = new Coord(in);
	}

	void setVitesse(final EScanner in) {
		vitesse = new Coord(in);
		setProchainePosition();
	}

	void setVitesse(final Coord vitesse) {
		this.vitesse = vitesse;
		setProchainePosition();
	}

	void setProchainePosition() {
		if(pos != null) {
			prochainePosition = pos.add(vitesse);
		}
	}

	public boolean visible() {
		return pos != null;
	}

	public void resetPosition() {
		pos = null;
		vitesse = null;
	}
}
