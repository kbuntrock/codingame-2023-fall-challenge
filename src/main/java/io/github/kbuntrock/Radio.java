package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class Radio {

	Direction direction;
	Poisson poisson;

	public Radio(final Direction direction, final Poisson poisson) {
		this.direction = direction;
		this.poisson = poisson;
	}

	@Override
	public String toString() {
		return "Radio{" +
			"direction=" + direction +
			", poisson=" + poisson +
			'}';
	}
}
