package mazeoblig;

import Player.Player;
import simulator.PositionInMaze;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MazeServer extends UnicastRemoteObject implements MazeServerInterface {

	private static final long serialVersionUID = 1972316214769502922L;
	HashMap<String, GameInterface> clients;
	HashMap<String, Player> players;
	Box box[][];
	Game game;
	Lock lock = new ReentrantLock();

	public MazeServer(Box boxFromServer[][]) throws RemoteException {
		this.clients = new HashMap<String, GameInterface>();
		this.players = new HashMap<String, Player>();
		this.box = boxFromServer;
	}

	@Override
	public void moveTo(Player player, PositionInMaze nextPosition) throws RemoteException {
		if (player == null || player.getPosition().equals(nextPosition))
			return;
		if (game.validateNextMove(player.getPosition(), nextPosition)) {
			lock.lock();        // Låser oppgaven til tråd
			try {
				players.get(player.getUuid()).setPosition(nextPosition);
			} finally {
				lock.unlock();
			}
		} else {
			System.out.println("Spiller " + player.getUuid() + " kunne ikke utføre trekket");
		}
	}

	@Override
	public void registerPlayer(String username, GameInterface game) throws RemoteException {
		if (game != null && !clients.containsValue(game)) {
			Player player = new Player(username,
							new PositionInMaze(
									randomCoordinate(),
									randomCoordinate()
							));
			lock.lock();
			game.setPlayer(player);
			players.put(player.getUuid(), player);
			clients.put(player.getUuid(), game);
			System.out.println("Spiller #" + clients.size() + ", " + player.getUuid() + " koblet til");
			lock.unlock();
		}
	}

	@Override
	public void unregisterPlayer(Player player) throws RemoteException {
		lock.lock();
		try {
			if (player != null & clients.containsKey(player.getUuid())) {
				clients.remove(player.getUuid());
				players.remove(player.getUuid());
				System.out.println("Spiller " + player.getUuid() + " koblet fra");
			}
		} finally {
			lock.unlock();
		}
	}

	private int randomCoordinate() {
		Random r = new Random();
		return r.nextInt(box[0].length - 2) +1;
	}
}
