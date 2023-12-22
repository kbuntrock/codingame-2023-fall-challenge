package io.github.kbuntrock;

import org.junit.jupiter.api.Test;

/**
 * @author Kévin Buntrock
 */
public class VecteurTest {

	@Test
	public void test() {

		final Coord depart = new Coord(1, 1);
		final Coord arrivee = new Coord(6, 11);
		final Vecteur vecteur = new Vecteur(depart, arrivee);

		final Coord newDepart = new Coord(0, 0);
		final Coord newArrivee = newDepart.add(vecteur.adapt(11.18));
//		final Coord newArrivee = newDepart.add(vecteur.adapt(600));
		System.out.println(newArrivee);
	}

	@Test
	public void test2() {

//		INFO T1 : Future position poisson : 2312 3765
//		INFO T1 : Position robot actuelle : 3333 500
//		INFO T1 : Poisson le plus proche : 2453 3906
//		INFO T1 : Position robot attendue : 0 0

		final Coord depart = new Coord(3333, 500);
		final Coord arrivee = new Coord(2312, 3765);

		final Vecteur vecteur = new Vecteur(depart, arrivee);
		final Vecteur vecteurAdapte = vecteur.adapt(360000);
		final Coord newArrivee = depart.add(vecteurAdapte);
//		final Coord newArrivee = depart.add(vecteur);
		System.out.println(newArrivee);
	}

}
