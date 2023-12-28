package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class Deserializer {


	public static final String NULL = "null";

	public static <T> T deserialiseObject(final Class<T> clazz, final String s) {
		if(clazz == Poisson.class) {

			final String[] ar = s.split("\\|\\|");
			return (T) new Poisson(
				Integer.valueOf(ar[0].substring(3)),
				deserialiseObject(Vecteur.class, ar[1]),
				Boolean.valueOf(ar[2]),
				Double.valueOf(ar[3]),
				Double.valueOf(ar[4]),
				Double.valueOf(ar[5]),
				Double.valueOf(ar[6]),
				Double.valueOf(ar[7]),
				Double.valueOf(ar[8]),
				Integer.valueOf(ar[9]),
				Integer.valueOf(ar[10]),
				deserialiseObject(Vecteur.class, ar[11]),
				deserialiseObject(Vecteur.class, ar[12].substring(0, ar[12].length() - 1))
			);

		} else if(clazz == Vecteur.class) {
			if(NULL.equals(s)) {
				return null;
			}
			final String[] ar = s.split("\\|");
			return (T) new Vecteur(Double.valueOf(ar[0].substring(3)), Double.valueOf(ar[1].substring(0, ar[1].length() - 1)));
		}
		throw new RuntimeException("Type d'objet serializé non géré");
	}

}
