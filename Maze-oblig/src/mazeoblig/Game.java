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
	private MazeServerInterface mazeServer;

	private Box[][] maze;
	Player user;
	private Map<String, Player> players = new HashMap<String, Player>();

	public Game() throws RemoteException {
		getServerDetails();
		setup();
		getMazeFromServer();
		login();
		players.put("1234", user);
	}

	private void getServerDetails() {
		if (SERVER_HOSTNAME == null)
			SERVER_HOSTNAME = RMIServer.getHostName();
		if (SERVER_PORTNUMBER == 0)
			SERVER_PORTNUMBER = RMIServer.getRMIPort();
	}

	private void setup() {
		int tries = 0;

		while (tries < 3) {
			try {
				tries++;
				Registry registry = LocateRegistry.getRegistry(SERVER_HOSTNAME, SERVER_PORTNUMBER);

				boxMazeInterface = (BoxMazeInterface) registry.lookup(RMIServer.MazeName);
				mazeServer = (MazeServerInterface) registry.lookup(RMIServer.mazeServerName);
				return;
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
				System.err.println("Fant ikke objektet på serveren");
				System.exit(0);
			}
		}
		System.out.println("Avslutter");
		System.exit(1);
	}

	private void getMazeFromServer() {
		if (boxMazeInterface == null)
			return;
		int tries = 0;
		while (tries < 3) {
			try {
				tries++;
				maze = boxMazeInterface.getMaze();
				return;
			} catch (RemoteException e) {
				System.out.println("Kunne ikke henter maze");
				e.printStackTrace();
			}
		}
		System.out.println("Avslutter");
		System.exit(1);
	}

	public void moveTo(PositionInMaze position) {
		if (boxMazeInterface == null || position == null || user == null || user.getPosition().equals(position))
			return;
		int tries = 0;
		while (tries < 3) {
			try {
				tries++;
				if (validateNextMove(user.getPosition(), position)) {
					mazeServer.moveTo(user, position);
					players.get(user.getUuid()).setPosition(position);
				} else {
					return;
				}
				return;
			} catch (RemoteException re) {
				System.out.println(re.getMessage());
			}
		}
		System.out.println("Avslutter");
		System.exit(1);
	}

	private boolean validateNextMove(PositionInMaze current, PositionInMaze next) {
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
				return maze[currentXpos][currentYpos].getUp() != null;
			} else {
				return maze[currentXpos][currentYpos].getDown() != null;
			}
		} else {
			if (currentYpos - nextYpos == 0) {
				if (currentXpos - nextXpos == 0) {
					return maze[currentXpos][currentYpos].getLeft() != null;
				} else {
					return maze[currentXpos][nextXpos].getRight() != null;
				}
			}
			return false;    // move failed
		}
	}

	@Override
	public void updatePlayerPosition(HashMap<String, Player> playerMap) throws RemoteException {
		players = playerMap;
		user = players.get(user.getUuid());
	}

	private void login() {
		if (boxMazeInterface == null)
			return;

		int tries = 0;
		while (tries < 3) {
			try {
				tries++;
 				mazeServer.registerPlayer("Ola Nordmann", this);
				System.out.println(user.toString());
				return;
			} catch (RemoteException e) {
				e.getLocalizedMessage();        // kunne ikke logge inn/koble til
			}
		}
	}

	public void logout() {
		if (user == null)
			return;

		int tries = 0;

		while (tries < 3) {
			try {
				tries++;
				mazeServer.unregisterPlayer(user);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}


	}

	@Override
	public void setPlayer(Player user) throws RemoteException {
		this.user = user;
	}

	public Box[][] getMaze() {
		return maze;
	}

	public Player getPlayer() {
		return user;
	}

	public Map<String, Player> getMazeClients() {
		return players;
	}
}
