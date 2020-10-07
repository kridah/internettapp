package mazeoblig;

import Player.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface GameInterface extends Remote {
	 void updatePlayerPosition(HashMap<String, Player> map) throws RemoteException;
	 void setPlayer(Player player) throws RemoteException;
}
