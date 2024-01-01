package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class Action {

	final String command;
	final Vecteur pos;

	final boolean light;
	String message;

	private Action(final String command, final Vecteur pos, final boolean light) {
		this.command = command;
		this.pos = pos;
		this.light = light;
	}

	static Action none() {
		return new Action("WAIT", null, false);
	}

	static Action move(final Vecteur pos, final boolean light) {
		return new Action("MOVE", pos, light);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(command);
		if(pos != null) {
			builder.append(' ').append(pos.toAction());
		}
		builder.append(' ').append(light ? "1" : "0");
		if(message != null) {
			builder.append(' ').append(message);
		}
		return builder.toString();
	}
}
