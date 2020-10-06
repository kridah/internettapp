package mazeoblig;

import Player.Player;
import simulator.PositionInMaze;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Game extends UnicastRemoteObject implements GameInterface {

	private String SERVER_HOSTNAME;
	private int SERVER_PORTNUMBER;
	private BoxMazeInterface boxMazeInterface;
	private MazeServerInterface mazeServerInterface;

	private Box[][] maze;
	private Player player;
	private Map<String, Player> Players = new HashMap<String, Player>();

	public Game() throws RemoteException {
		getServerDetails();
		setup();
		getMazeFromServer();
		Players.put(player.getUuid(), player);
	}

	private void getServerDetails() {
		SERVER_HOSTNAME = RMIServer.getHostName();
		SERVER_PORTNUMBER = RMIServer.getRMIPort();
	}

	private void setup() {
		int tries = 0;
		while (tries < 3) {
			try {
				tries++;
				Registry registry = LocateRegistry.getRegistry(SERVER_HOSTNAME, SERVER_PORTNUMBER);

				boxMazeInterface = (BoxMazeInterface) registry.lookup(RMIServer.boxMaze);
				mazeServerInterface = (MazeServerInterface) registry.lookup(RMIServer.mazeServer);
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Avslutter");
	}

	private void getMazeFromServer() {
		int tries = 0;
		while (tries < 3) {
			try {
				maze = boxMazeInterface.getMaze();
				return;
			} catch (RemoteException e) {
				System.out.println("Kunne ikke henter maze");
				e.printStackTrace();
			}
		}
	}

	public void moveTo(PositionInMaze position) {
		if (boxMazeInterface == null || position == null || player.getPosition().equals(position))
			return;
		int tries = 0;
		// skal kunne sjekke alle fire retninger
		while (tries < 3) {
			try {
				tries++;
				if (validateNextMove(player.getPosition(), position)) {
					mazeServerInterface.moveTo(player, position);
					Players.get(player.getUuid()).setPosition(position);
				} else {
					return;
				}
				return;
			} catch (RemoteException re) {
				System.out.println(re.getMessage());
			}
		}
	}

	private boolean validateNextMove(PositionInMaze current, PositionInMaze next) {
		int xCurrent = current.getXpos();
		int yCurrent = current.getYpos();
		int xNext = next.getXpos();
		int yNext = next.getYpos();

		// Next position cannot be out of bounds
		if (xNext < 0 || yNext < 0)
			return false;

		// One step at the time
		boolean xDifference =  xCurrent - xNext == 1 || xCurrent - xNext == -1;
		boolean yDifference =  yCurrent - yNext == 1 || yCurrent - yNext == -1;
		if (!(xDifference^yDifference))
			return false;

		if (xCurrent - xNext == 0) {
			if (yCurrent - yNext == 1) {
				return maze[xCurrent][yCurrent].getUp() != null;
			} else {
				return maze[xCurrent][yCurrent].getDown() != null;
			}
		} else {
			if (yCurrent - yNext == 0) {
				if (xCurrent - xNext == 0) {
					return maze[xCurrent][yCurrent].getLeft() != null;
				} else {
					return maze[xCurrent][xNext].getRight() != null;
				}
			}
			return false;	// move failed
		}
	}

	@Override
	public void updatePlayerPosition(Map<String, Player> playerMap) throws RemoteException {
		Players = playerMap;
		player = Players.get(player.getUuid());
	}

	@Override
	public void setPlayer(Player player) throws RemoteException {
		this.player = player;
	}

	public Box[][] getMaze() {
		return maze;
	}

	public Player getPlayer() {
		return player;
	}

	public Map<String, Player> getMazeClients() { return Players; }
}
