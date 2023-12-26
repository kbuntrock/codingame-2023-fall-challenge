package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class Poisson extends Entity {

	final int couleur;
	final int espece;

	Vecteur vitesse;

	Vecteur prochainePosition;

	Poisson(final int id, final Vecteur pos, final Vecteur vitesse, final int couleur, final int espece) {
		super(id, pos, EntityType.POISSON);
		this.couleur = couleur;
		this.espece = espece;
		this.vitesse = vitesse;
		setProchainePosition();
	}

	void setPosition(final EScanner in) {
		pos = new Vecteur(in);
	}

	void setVitesse(final EScanner in) {
		vitesse = new Vecteur(in);
		setProchainePosition();
	}

	void setVitesse(final Vecteur vitesse) {
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
