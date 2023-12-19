package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public class Action {

	final String command;
	final Coord pos;
	final EntityType item;

	final boolean light;
	String message;

	private Action(final String command, final Coord pos, final EntityType item, final boolean light) {
		this.command = command;
		this.pos = pos;
		this.item = item;
		this.light = light;
	}

	static Action none() {
		return new Action("WAIT", null, null, false);
	}

	static Action move(final Coord pos, final boolean light) {
		return new Action("MOVE", pos, null, light);
	}

	static Action dig(final Coord pos) {
		return new Action("DIG", pos, null, false);
	}

	static Action request(final EntityType item) {
		return new Action("REQUEST", null, item, false);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(command);
		if(pos != null) {
			builder.append(' ').append(pos);
		}
		builder.append(' ').append(light ? "1" : "0");
		if(item != null) {
			builder.append(' ').append(item);
		}
		if(message != null) {
			builder.append(' ').append(message);
		}
		return builder.toString();
	}
}
