package mazeoblig;

/**
 * <p>Title: </p>
 * RMIServer - En server som kobler seg opp å kjører server-objekter på
 * rmiregistry som startes automagisk.
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * RMIServer starts execution at the standard entry point "public static void main";
 * It creates an instance of itself and continues processing in the constructor.
 */

public class RMIServer {
	private final static int DEFAULT_PORT = 9000;
	private final static String DEFAULT_HOST = "undefined";
	public static int PORT = DEFAULT_PORT;
	public static String MazeName = "Maze";
	public static String mazeServerName = "MazeServer";
	private static String HOST_NAME;
	private static InetAddress myAdress = null;
	private static RMIServer rmi;

	private static BoxMaze maze;
	private static MazeServerInterface mazeServerInterface;

	public RMIServer() throws RemoteException, MalformedURLException,
			NotBoundException, AlreadyBoundException {
		getStaticInfo();
		LocateRegistry.createRegistry(PORT);
		System.out.println("RMIRegistry created on host computer " + HOST_NAME +
				" on port " + PORT);

		/* Legger inn labyrinten */
		maze = new BoxMaze(Maze.DIM);
		System.out.println("Remote implementation object created");
		String urlString = "//" + HOST_NAME + ":" + PORT + "/" + MazeName;
		Naming.rebind(urlString, maze);
		System.out.println("Remote implementation object created for BoxMaze generator");

		mazeServerInterface = new MazeServer(maze.getMaze());
		String mazeServerURL = "//" + HOST_NAME + ":" + PORT + "/" + mazeServerName;
		Naming.rebind(mazeServerURL, mazeServerInterface);
		System.out.println("Remote implementation object created for MazeServer");

		System.out.println("Bindings Finished, waiting for client requests.");
	}

	private static void getStaticInfo() {
		/**
		 * Henter hostname på min datamaskin
		 */
		if (HOST_NAME == null) HOST_NAME = DEFAULT_HOST;
		if (PORT == 0) PORT = DEFAULT_PORT;
		if (HOST_NAME.equals("undefined")) {
			try {
				myAdress = InetAddress.getLocalHost();
				HOST_NAME = "localhost";
			} catch (UnknownHostException e) {
				System.err.println("Fant ikke server " + HOST_NAME);
			}
		} else
			System.out.println("En MazeServer kjører allerede, bruk den");

		System.out.println("Maze server navn: " + HOST_NAME);
		System.out.println("Maze server ip:   " + myAdress.getHostAddress());
	}

	public static int getRMIPort() {
		return PORT;
	}

	public static String getHostName() {
		return HOST_NAME;
	}

	public static String getHostIP() {
		return myAdress.getHostAddress();
	}

	public static void main(String[] args) {
		try {
			rmi = new RMIServer();
		} catch (java.rmi.UnknownHostException uhe) {
			System.out.println("Maskinnavnet, " + HOST_NAME + " er ikke korrekt.");
		} catch (RemoteException re) {
			System.out.println("Error starting service");
			System.out.println("" + re);
			re.printStackTrace(System.err);
		} catch (MalformedURLException mURLe) {
			System.out.println("Internal error" + mURLe);
		} catch (NotBoundException nbe) {
			System.out.println("Not Bound");
			System.out.println("" + nbe);
		} catch (AlreadyBoundException abe) {
			System.out.println("Already Bound");
			System.out.println("" + abe);
		}
		System.out.println("RMIRegistry on " + HOST_NAME + ":" + PORT +
				"\n----------------------------");
	}  // main
}  // class RMIServer
