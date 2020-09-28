package mazeoblig;

import Player.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class Game extends UnicastRemoteObject implements GameInterface {

	private String SERVER_HOSTNAME;
	private int SERVER_PORTNUMBER;

	private Box[][] maze;
	private Player player;

	public Game() throws RemoteException {

	}

	@Override
	public void updatePlayerPosition(Map<String, Player> map) throws RemoteException {

	}

	@Override
	public void setPlayer(Player player) throws RemoteException {

	}

	public Box[][] getMaze() {
		return maze;
	}

	public Player getPlayer() {
		return player;
	}
}
