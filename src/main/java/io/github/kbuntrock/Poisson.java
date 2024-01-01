package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class Poisson extends Entity implements Transferable<Poisson> {

	boolean horsTerrain = true;

	final double absoluteMinY;
	final double absoluteMaxY;

	double currentMinY;
	double currentMaxY;

	double currentMinX;
	double currentMaxX;

	final int couleur;
	final int espece;

	Vecteur vitesse;

	Vecteur prochainePosition;

	Poisson(final int id, final Vecteur pos, final Vecteur vitesse, final int couleur, final int espece) {
		super(id, pos, EntityType.POISSON);
		this.couleur = couleur;
		this.espece = espece;
		this.vitesse = vitesse;
		absoluteMinY = espece == 0 ? 2500 : espece == 1 ? 5000 : 7500;
		absoluteMaxY = espece == 0 ? 5000 : espece == 1 ? 7500 : 10000;
		currentMinY = absoluteMinY;
		currentMaxY = absoluteMaxY;
		setProchainePosition();
	}

	public Poisson(final int id, final Vecteur pos, final boolean horsTerrain, final double absoluteMinY,
		final double absoluteMaxY,
		final double currentMinY, final double currentMaxY, final double currentMinX, final double currentMaxX, final int couleur,
		final int espece, final Vecteur vitesse,
		final Vecteur prochainePosition) {
		super(id, pos, EntityType.POISSON);
		this.horsTerrain = horsTerrain;
		this.absoluteMinY = absoluteMinY;
		this.absoluteMaxY = absoluteMaxY;
		this.currentMinY = currentMinY;
		this.currentMaxY = currentMaxY;
		this.currentMinX = currentMinX;
		this.currentMaxX = currentMaxX;
		this.couleur = couleur;
		this.espece = espece;
		this.vitesse = vitesse;
		this.prochainePosition = prochainePosition;
	}

	public void resetCurrentMinMax() {
		currentMinX = 0;
		currentMaxX = 10000;
		currentMinY = absoluteMinY;
		currentMaxY = absoluteMaxY;
	}

	public Vecteur milieuRectangle() {
		final Vecteur hg = new Vecteur(currentMinX, currentMinY);
		final Vecteur bd = new Vecteur(currentMaxX, currentMaxY);
		return new Vecteur((hg.x + bd.x) / 2, (hg.y + bd.y) / 2);
	}

	void setPosition(final EScanner in) {
		pos = new Vecteur(in);
		currentMinX = pos.x;
		currentMaxX = pos.x;
		currentMinY = pos.y;
		currentMaxY = pos.y;
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
		horsTerrain = true;
	}

	public Poisson copy() {
		return new Poisson(id, pos, horsTerrain, absoluteMinY, absoluteMaxY, currentMinY, currentMaxY, currentMinX, currentMaxX, couleur,
			espece, vitesse, prochainePosition);
	}

	@Override
	public String serialize() {
		return "$P["
			+ id
			+ "||" + (pos == null ? "null" : pos.serialize())
			+ "||" + horsTerrain
			+ "||" + (int) absoluteMinY + "||" + (int) absoluteMaxY
			+ "||" + (int) currentMinY + "||" + (int) currentMaxY
			+ "||" + (int) currentMinX + "||" + (int) currentMaxX
			+ "||" + couleur + "||" + espece
			+ "||" + (vitesse == null ? "null" : vitesse.serialize())
			+ "||" + (prochainePosition == null ? "null" : prochainePosition.serialize())
			+ "]";
	}


}
