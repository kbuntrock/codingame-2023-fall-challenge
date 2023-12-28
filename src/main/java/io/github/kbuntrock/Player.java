package io.github.kbuntrock;

import java.util.Scanner;


public class Player {

	public static void main(final String[] args) {
		new Player().run();
	}

	EScanner in = new EScanner(new Scanner(System.in));

	void run() {

		// Parse initial conditions
		final Board board = new Board(in);
		in.switchToTurnBuffer();

		final Cortex cortex = new Cortex(board);

		while(true) {
			IO.incrementTurn();
			in.resetCurrentBuffer();

			// Parse current state of the game
			board.update(in);

			// Exporte l'état du jeu pour les tests
			in.export();

			cortex.explorerHabitat();

			IO.lastTurnData = new LastTurnData(board, cortex);

			// Send your actions for this turn
			for(final Robot robot : board.myTeam.robots) {
				System.out.println(robot.action);
			}
		}
	}
}