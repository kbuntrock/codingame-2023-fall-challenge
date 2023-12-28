package io.github.kbuntrock;

import org.junit.jupiter.api.Test;

/**
 * @author Kévin Buntrock
 */
public class VecteurTest {

	@Test
	public void calcul() {
		final Poisson p = new Poisson(1, new Vecteur(0, 0), new Vecteur(0, 0), 0, 0);
		p.currentMinX = 0;
		p.currentMinY = 0;
		p.currentMaxX = 10;
		p.currentMaxY = 10;
		p.milieuRectangle();
	}

	@Test
	public void intersection() {
		final Vecteur c1 = new Vecteur(-9, 1);
		final Vecteur c2 = new Vecteur(5, -5);
		final var intersection = c1.intersection(7, c2, 18);
		System.out.println("");
	}

	@Test
	public void intersection2() {
		final double squareDistance = new Vecteur(746, 7459).squareDistance(new Vecteur(1052, 6997));
		System.out.println(squareDistance);
	}

	@Test
	public void test() {

		final Vecteur depart = new Vecteur(1, 1);
		final Vecteur arrivee = new Vecteur(6, 11);
		final Vecteur vecteur = new Vecteur(depart, arrivee);

		final Vecteur newDepart = new Vecteur(0, 0);
		final Vecteur newArrivee = newDepart.add(vecteur.adapt(11.18));
//		final Vecteur newArrivee = newDepart.add(vecteur.adapt(600));
		System.out.println(newArrivee);
	}

	@Test
	public void test2() {

//		INFO T1 : Future position poisson : 2312 3765
//		INFO T1 : Position robot actuelle : 3333 500
//		INFO T1 : Poisson le plus proche : 2453 3906
//		INFO T1 : Position robot attendue : 0 0

		final Vecteur depart = new Vecteur(3333, 500);
		final Vecteur arrivee = new Vecteur(2312, 3765);

		final Vecteur vecteur = new Vecteur(depart, arrivee);
		final Vecteur vecteurAdapte = vecteur.adapt(360000);
		final Vecteur newArrivee = depart.add(vecteurAdapte);
//		final Vecteur newArrivee = depart.add(vecteur);
		System.out.println(newArrivee);
	}

}
