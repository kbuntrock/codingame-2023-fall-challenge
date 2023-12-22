package io.github.kbuntrock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author Kévin Buntrock
 */
public class EScanner {

	private List<Object> buffer;
	private final List<Object> global;
	private final List<Object> turn;

	private Scanner in;

	private boolean testMode = false;

	private int index = 0;

	public EScanner(final Scanner in) {
		this.in = in;
		global = new ArrayList<>();
		turn = new ArrayList<>();
		buffer = global;
	}

	public void switchToTurnBuffer() {
		buffer = turn;
		index = 0;
	}

	public void resetCurrentBuffer() {
		if(!testMode) {
			buffer.clear();
		}
	}

	public EScanner(final String inputs) {
		final String[] arrays = inputs.split("->")[1].split(";");
		global = Arrays.stream(arrays[0].split(":")).map(x -> Integer.valueOf(x)).collect(Collectors.toList());
		turn = Arrays.stream(arrays[1].split(":")).map(x -> {
			try {
				return Integer.valueOf(x);
			} catch(final NumberFormatException ex) {
				// Rien à faire;
			}
			return x;
		}).collect(Collectors.toList());
		buffer = global;
		testMode = true;
	}

	public int nextInt() {
		if(!testMode) {
			final int val = in.nextInt();
			buffer.add(val);
			return val;
		}
		final int val = (int) buffer.get(index);
		index++;
		return val;
	}

	public String next() {
		if(!testMode) {
			final String val = in.next();
			buffer.add(val);
			return val;
		}
		final String val = (String) buffer.get(index);
		index++;
		return val;
	}

	public void export() {
		final StringBuilder sb = new StringBuilder();
		sb.append(global.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(":")));
		sb.append(";");
		sb.append(turn.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(":")));
		IO.export(sb.toString());
	}
}
