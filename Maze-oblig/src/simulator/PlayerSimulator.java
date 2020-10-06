package simulator;

import mazeoblig.Box;
import mazeoblig.Game;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerSimulator implements Runnable {

	private static int NUMBER_OF_CLIENTS = 100;

	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_CLIENTS);

		for (int i=0; i < NUMBER_OF_CLIENTS; i++) {
			executor.execute(new PlayerSimulator());
		}
	}

	@Override
	public void run() {
		try {
			int fails = 0;
			Game game = new Game();
			VirtualUser virtualUser = new VirtualUser(game.getMaze(), game.getPlayer());

			// Henter veien ut av labyrinten
			PositionInMaze[] position = virtualUser.getFirstIterationLoop();
			for (int i = 0; i < position.length; i++) {
				Thread.sleep(100);
				game.moveTo(position[i]);

				if (fails == 10) {
					game.moveTo(game.getPlayer().getPosition().right());
					virtualUser = new VirtualUser(game.getMaze(), game.getPlayer());
					position = virtualUser.getFirstIterationLoop();
					i = 0;
					continue;
				} else if (fails == 11) {
					game.moveTo(game.getPlayer().getPosition().left());
					virtualUser = new VirtualUser(game.getMaze(), game.getPlayer());
					position = virtualUser.getFirstIterationLoop();
					i = 0;
					continue;
				} else if (fails == 12) {
					game.moveTo(game.getPlayer().getPosition().up());
					virtualUser = new VirtualUser(game.getMaze(), game.getPlayer());
					position = virtualUser.getFirstIterationLoop();
					i = 0;
					continue;
				} else if (fails == 13) {
					game.moveTo(game.getPlayer().getPosition().down());
					virtualUser = new VirtualUser(game.getMaze(), game.getPlayer());
					position = virtualUser.getFirstIterationLoop();
					i = 0;
					continue;
				} else {
					fails = 9;
					continue;
				}
			}

			position = virtualUser.getIterationLoop();
			for (int i = 0; i < position.length; i++) {
				Thread.sleep(500);
				game.moveTo(position[i]);
			}

		} catch (RemoteException re) {
			System.err.println(re.getMessage());
		} catch (InterruptedException ie) {
			System.err.println(ie.getMessage());
		}
	}


}
