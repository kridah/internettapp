package simulator;

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
			//VirtualUser virtualUser = new VirtualUser(game.getMaze(), game.getUser());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
