package mazeoblig;

import Player.Player;
import simulator.PositionInMaze;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MazeServerInterface extends Remote {

	void moveTo (Player player, PositionInMaze position) throws RemoteException;
	void registerPlayer (GameInterface game) throws RemoteException;
	void unregisterPlayer (Player player) throws RemoteException;
}
