package mazeoblig;

import Player.Player;
import simulator.PositionInMaze;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	Box[][] boxmaze;
	Lock lock = new ReentrantLock();
	//Game game;

	public MazeServer(Box[][] boxFromServer) throws RemoteException {
		this.clients = new HashMap<String, GameInterface>();
		this.players = new HashMap<String, Player>();
		this.boxmaze = boxFromServer;

		// opppdater spillere med endringer
		int refreshrate = 1000;
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lock.lock();
				HashMap<String, GameInterface> temp = (HashMap<String, GameInterface>) clients.clone();
				lock.unlock();

				temp.forEach((key, p) -> {
					try {
						GameInterface gi = (GameInterface) p;
						gi.updatePlayerPosition(players);
						//((GameInterface) player).updatePlayerPosition(players);
					} catch (RemoteException remoteException) {
						//System.err.println("Klarte ikke flytte spiller " + p.getPlayer().getUuid());
						remoteException.printStackTrace();
					}
				});
//				lock.lock();
//				System.out.println("Kunne ikke levere melding til alle spillere");
//				System.out.println("---------------------------\nPlayers currently connected: "+ clients.size());
//				lock.unlock();
			}
		};
		Timer timer = new Timer(refreshrate, actionListener);
		timer.setRepeats(true);
		timer.start();
	}

	@Override
	public void moveTo(Player player, PositionInMaze nextPosition) throws RemoteException {
		if (player == null || player.getPosition().equals(nextPosition))
			return;
		if (validateNextMove(player.getPosition(), nextPosition)) {
			lock.lock();        // Låser oppgaven til tråd
			try {
				players.get(player.getUuid()).setPosition(nextPosition);
			} finally {
				lock.unlock();
			}
			System.out.println("bm.moveTo: Player " + player.getUuid() + " moved from " + player.getPosition() + " to " + nextPosition);
		} else {
			System.out.println("Spiller " + player.getUuid() + " kunne ikke utføre trekket");
		}
	}

	public boolean validateNextMove(PositionInMaze current, PositionInMaze next) {
		int currentXpos = current.getXpos();
		int currentYpos = current.getYpos();
		int nextXpos = next.getXpos();
		int nextYpos = next.getYpos();

		// Next position cannot be out of bounds
		if (nextXpos < 0 || nextYpos < 0)
			return false;

		// One step at the time
		boolean xDifference = currentXpos - nextXpos == 1 || currentXpos - nextXpos == -1;
		boolean yDifference = currentYpos - nextYpos == 1 || currentYpos - nextYpos == -1;
		if (!(xDifference ^ yDifference))
			return false;

		if (currentXpos - nextXpos == 0) {
			if (currentYpos - nextYpos == 1) {
				return boxmaze[currentXpos][currentYpos].getUp() != null;
			} else {
				return boxmaze[currentXpos][currentYpos].getDown() != null;
			}
		} else {
			if (currentYpos - nextYpos == 0) {
				if (currentXpos - nextXpos == 0) {
					return boxmaze[currentXpos][currentYpos].getLeft() != null;
				} else {
					return boxmaze[currentXpos][nextXpos].getRight() != null;
				}
			}
			return false;    // move failed
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
		return r.nextInt(boxmaze[0].length - 2) + 1;
	}
}
