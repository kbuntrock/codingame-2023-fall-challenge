package io.github.kbuntrock;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kévin Buntrock
 */
public class LastTurnData {

	String serialized;

	Map<Integer, Poisson> poissonById;

	public LastTurnData() {
		poissonById = new HashMap<>();
	}

	public LastTurnData(final Board board, final Cortex cortex) {
		final StringBuilder sb = new StringBuilder();
		poissonById = new HashMap<>();
		for(final Poisson p : board.poissonsById.values()) {
			poissonById.put(p.id, p.copy());
			sb.append(p.serialize());
		}
		serialized = sb.toString();
	}

	public LastTurnData(final String serialized) {
		poissonById = new HashMap<>();
		if(!"null".equals(serialized)) {
			final String[] psArray = serialized.split("\\$P");
			for(int i = 1; i < psArray.length; i++) {
				final Poisson poisson = Deserializer.deserialiseObject(Poisson.class, "$P" + psArray[i]);
				poissonById.put(poisson.id, poisson);
			}
		}
	}
}
