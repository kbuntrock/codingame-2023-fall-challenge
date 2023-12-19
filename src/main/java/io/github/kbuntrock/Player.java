package io.github.kbuntrock;

import java.util.Map.Entry;
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

			// Insert your strategy here
//			for(final Robot robot : board.myTeam.robots) {
//				IO.info("Batterie : " + robot.battery);
//				robot.action = Action.none();
//				robot.action.message = "Message custom";
//			}

			boolean light = false;

			//IO.info("Mes scans : " + board.myTeam.savedScans.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(", ")));
			IO.info("Etat robot : " + board.myTeam.robots.get(0).toString());
			final Poisson plusProche = cortex.trouverPoissonNonScannePlusProche();
			IO.info("Poisson le plus proche : " + (plusProche == null ? "Aucun visible" : plusProche.pos.toString()));
			if(plusProche != null) {
				final Coord visee = cortex.calculerMouvement(plusProche);
				if(board.myTeam.robots.get(0).battery >= 5) {
					final var poissonsAPortee = cortex.trouverPoissonsAPortee(visee, 800, 2000);
					IO.info("Il y a " + poissonsAPortee.size() + " poissons à portée.");
					if(!poissonsAPortee.isEmpty()) {
						light = true;
					}
				}
				IO.info("Position robot actuelle : " + board.myTeam.robots.get(0).pos.toString());
				IO.info("Position robot attendue : " + visee.toString());
				board.myTeam.robots.get(0).action = Action.move(visee, light);
			} else {
				IO.info("Hot Map : ");
				int maxPoissons = 0;
				Direction choisie = null;
				for(final Entry<Direction, Integer> entry : board.hotMap.entrySet()) {
					IO.info(entry.getKey() + " : " + entry.getValue() + " poissons");
					if(entry.getValue() > maxPoissons) {
						choisie = entry.getKey();
						maxPoissons = entry.getValue();
					}
				}
				if(choisie == null) {
					choisie = Direction.T;
				}
				final Robot robot = board.myTeam.robots.get(0);
				final Coord visee = robot.pos.add(choisie.direction);
				board.myTeam.robots.get(0).action = Action.move(visee, light);
			}

			// Send your actions for this turn
			for(final Robot robot : board.myTeam.robots) {
				System.out.println(robot.action);
			}
		}
	}
}