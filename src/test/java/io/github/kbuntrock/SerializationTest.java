package io.github.kbuntrock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Kévin Buntrock
 */
public class SerializationTest {

	@Test
	public void test() {
		final Vecteur v = new Vecteur(5, -27);
		final String s = v.serialize();
		final Vecteur v2 = Deserializer.deserialiseObject(Vecteur.class, s);
		System.out.println("");
	}

	@Test
	public void test2() {

		final Poisson p = new Poisson(1, new Vecteur(2, 3),
			true, 4, 5, 6, 7, 8, 9, 10, 11,
			new Vecteur(12, 13), new Vecteur(-14, -15));
		final String s = p.serialize();
		final Poisson ps = Deserializer.deserialiseObject(Poisson.class, s);
		Assertions.assertEquals(p, ps);
		System.out.println("");
	}

}
