package mazeoblig;

import Player.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface GameInterface extends Remote {
	public void updatePlayerPosition(Map<String, Player> map) throws RemoteException;
	public void setPlayer(Player player) throws RemoteException;
}
