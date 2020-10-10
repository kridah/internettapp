package mazeoblig;

import Player.Player;
import simulator.PositionInMaze;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MazeServer extends UnicastRemoteObject implements MazeServerInterface {

	HashMap<String, GameInterface> clients;
	HashMap<String, Player> players;
	Box[][] boxmaze;
	Lock lock = new ReentrantLock();		// trådlås
	Vector<String> errorList;				// Vektor = array med dynamisk størrelse

	public MazeServer(Box[][] boxFromServer) throws RemoteException {
		this.clients = new HashMap<String, GameInterface>();
		this.players = new HashMap<String, Player>();
		this.boxmaze = boxFromServer;

		// oppdater spillere med endringer
		int refreshrate = 500;
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lock.lock();
				HashMap<String, GameInterface> temp = (HashMap<String, GameInterface>) clients.clone();
				lock.unlock();
				errorList = new Vector<String>();

				temp.forEach((key, p) -> {
					try {
						GameInterface gi = (GameInterface) p;
						gi.updatePlayerPosition(players);
					} catch (RemoteException remoteException) {
						System.err.println("Klarte ikke flytte spiller " + p.toString());
						errorList.add(key);
						remoteException.printStackTrace();
					} catch (ConcurrentModificationException cme) {
						cme.getMessage();
					}
				});
				lock.lock();
				errorList.forEach(error -> {
					errorList.add(error);
					System.out.println("Kunne ikke levere melding til " + error);
				});
				lock.unlock();
			}
		};
		Timer timer = new Timer(refreshrate, actionListener);
		timer.setRepeats(true);
		timer.start();
	}

	@Override
	public void moveTo(Player p, PositionInMaze nextPosition) throws RemoteException {
		if (p == null || p.getPosition().equals(nextPosition)) {
			return;
		}
		if (validateNextMove(p.getPosition(), nextPosition)) {
			lock.lock();        // Låser oppgaven til tråd
			try {
				players.get(p.getUuid()).setPosition(nextPosition);
			} finally {
				lock.unlock();
			}
			System.out.println("Player " + p.getUuid() + " moved from " + p.getPosition() + " to " + nextPosition);
		} else {
			System.out.println("Spiller " + p.getUuid() + " kunne ikke utføre trekket");
			unregisterPlayer(p);
		}
	}

	/* Sjekker at neste trekk er mulig å gjennomføre. Ie at det ikke er en vegg */
	public boolean validateNextMove(PositionInMaze current, PositionInMaze next) {
		int currentXpos = current.getXpos();
		int currentYpos = current.getYpos();
		int nextXpos = next.getXpos();
		int nextYpos = next.getYpos();

		// Next position cannot be out of bounds
		if (nextYpos < 0 || nextXpos < 0) {
			return false;
		}

		// One step at the time
		boolean xDifference = currentXpos - nextXpos == 1 || currentXpos - nextXpos == -1;
		boolean yDifference = currentYpos - nextYpos == 1 || currentYpos - nextYpos == -1;
		if (xDifference == yDifference) {
			return false;
		}

		if (currentXpos - nextXpos == 0) {
			if (currentYpos - nextYpos == 1) {
				return boxmaze[currentXpos][currentYpos].getUp() != null;
			} else {
				return boxmaze[currentXpos][currentYpos].getDown() != null;
			}
		} else {
			if (currentYpos - nextYpos == 0) {
				if (currentXpos - nextXpos == 1) {
					return boxmaze[currentXpos][currentYpos].getLeft() != null;
				} else {
					return boxmaze[currentXpos][currentYpos].getRight() != null;
				}
			}
			System.out.println("Validerer trekk fra" + current + " til " + next);

			return false;    // trekk feilet
		}
	}

	@Override
	public void registerPlayer(GameInterface game) throws RemoteException {
		if (game != null && !clients.containsValue(game)) {
			Player player = new Player(new PositionInMaze(
					randomCoordinate(),
					randomCoordinate()
			));

			lock.lock();
				game.setPlayer(player);
				players.put(player.getUuid(), player);
				clients.put(player.getUuid(), game);
				System.out.println("Spiller #" + clients.size() + ", " + player.getUuid() + " koblet til");
			lock.unlock();
		} else {
			System.out.println("Kunne ikke registrere spiller på tjener");
		}
	}

	@Override
	public void unregisterPlayer(Player player) throws RemoteException {
		lock.lock();
		try {
			assert player != null;
			if (clients.containsKey(player.getUuid())) {
				clients.remove(player.getUuid());
				players.remove(player.getUuid());
				System.out.println("Spiller " + player.getUuid() + " koblet fra");
			}
		} finally {
			lock.unlock();
		}
	}

	// Setter spiller på en tilfeldig plass i labyrinten
	private int randomCoordinate() {
		Random r = new Random();
		return r.nextInt(boxmaze[0].length - 2) + 1;
	}
}
