package simulator;

import mazeoblig.Game;

import java.rmi.ConnectIOException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerSimulator implements Runnable {

	private static int NUMBER_OF_CLIENTS = 200;

	public static void main(String[] args) {
		//ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_CLIENTS);
		ExecutorService executor = Executors.newCachedThreadPool();        // dynamisk antall tråder

		for (int i = 0; i < NUMBER_OF_CLIENTS; i++) {
			executor.execute(new PlayerSimulator());
		}
	}

	@Override
	public void run() {
		try {
			int fails = 0;
			Game game = new Game();
			VirtualUser user = new VirtualUser(game.getMaze(), game.getPlayer());

			Thread.sleep(500);

			// Henter veien ut av labyrinten
			PositionInMaze[] position = user.getFirstIterationLoop();
			for (int i = 0; i < position.length; i++) {
				Thread.sleep(500);
				game.moveTo(position[i]);

				if (game.getPlayer().getPosition() != position[i]) {
					fails++;
				} else {
					fails = 0;
				}

				if (fails == 10) {
					game.moveTo(game.getPlayer().getPosition().right());
					user = new VirtualUser(game.getMaze(), game.getPlayer());
					position = user.getFirstIterationLoop();
					i = 0;
					continue;
				} else if (fails == 11) {
					game.moveTo(game.getPlayer().getPosition().left());
					user = new VirtualUser(game.getMaze(), game.getPlayer());
					position = user.getFirstIterationLoop();
					i = 0;
					continue;
				} else if (fails == 12) {
					game.moveTo(game.getPlayer().getPosition().up());
					user = new VirtualUser(game.getMaze(), game.getPlayer());
					position = user.getFirstIterationLoop();
					i = 0;
					continue;
				} else if (fails == 13) {
					game.moveTo(game.getPlayer().getPosition().down());
					user = new VirtualUser(game.getMaze(), game.getPlayer());
					position = user.getFirstIterationLoop();
					i = 0;
					continue;
				} else {
					fails = 9;
				}
			}

			position = user.getIterationLoop();

			for (PositionInMaze positionInMaze : position) {
				Thread.sleep(500);
				game.moveTo(positionInMaze);
			}

			game.logout();

		} catch (RemoteException re) {
			System.err.println(re.getMessage());
		} catch (InterruptedException ie) {
			System.err.println("Tråden ble avbrutt");
			ie.printStackTrace();
		}
	}
}
