package mazeoblig;

import simulator.PositionInMaze;
import simulator.VirtualUser;

import javax.swing.*;
import java.awt.*;
import java.applet.*;


/**
 *
 * <p>Title: Maze</p>
 *
 * <p>Description: En enkel applet som viser den randomiserte labyrinten</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.util.HashMap;

import static java.lang.Thread.sleep;

/**
 * Class responsible for drawing the maze and populating it with clients
 */
@SuppressWarnings("serial")
public class Maze extends Applet {

	private Box[][] mazeBox;
	public final static int DIM = 30;
	Thread thread;
	Game game;
	private Boolean autorun = true;
	private Boolean autorunStopped = false;
	int refreshEvery = 1000;
	//private String server_hostname;
	//private int server_portnumber;
	//private ServerInterface serverInterface;
	private final int CLIENTS_TO_CREATE = 5;

	private JPanel panel;
	private Graphics graphics;
	private Image image;
	private int frameWidth = 500;
	private int frameHeight = 500;

	/**
	 * Establish server and registry connection (will only work if server and client is run from the same computer)
	 * Retrieve all remote objects from RMI server. Method supplied at project start, modified by author
	 */
	public void init() {
		//int x, y;

		try {
			game = new Game();
		} catch (RemoteException re) {
			System.err.println(re.getMessage());
			System.exit(1);
		}
		mazeBox = game.getMaze();

		// Graphics
		image = createImage(frameWidth, frameHeight);
		graphics = image.getGraphics();

		int dim = DIM;
		for (int x = 0; x < (dim - 1); x++) {
			for (int y = 0; y < (dim - 1); y++) {
				if (mazeBox[x][y].getUp() == null)
					graphics.drawLine(x * 10, y * 10, x * 10 + 10, y * 10);
				if (mazeBox[x][y].getDown() == null)
					graphics.drawLine(x * 10, y * 10 + 10, x * 10 + 10, y * 10 + 10);
				if (mazeBox[x][y].getLeft() == null)
					graphics.drawLine(x * 10, y * 10, x * 10, y * 10 + 10);
				if (mazeBox[x][y].getRight() == null)
					graphics.drawLine(x * 10 + 10, y * 10, x * 10 + 10, y * 10 + 10);
			}

			setBackground(Color.white);
			panel = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(image, 0, 0, this);

					game.getMazeClients().forEach((key, player) -> {
						if (key.equals(game.getPlayer().getUuid())) {
							return;
						} else {
							g.setColor(Color.black);
						}
						g.fillOval(player.getPosition().getXpos() * 10, player.getPosition().getYpos() * 10, 8, 8);
					});

					g.setColor(Color.MAGENTA);
					g.fillOval(game.getPlayer().getPosition().getXpos() * 10, game.getPlayer().getPosition().getYpos() * 10, 8, 8);
					showStatus("Antall spillere koblet til: " + game.getMazeClients().size());
				}

				@Override
				public Dimension getPreferredSize() {
					return new Dimension(frameWidth, frameHeight);
				}
			};
			add(panel);


			ActionListener actionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					repaint();
				}
			};

		}
	}

	public void start() {
		autopilot();
	}

	private void autopilot() {
		thread = new Thread(() -> {
			try {
				PositionInMaze[] position;
				VirtualUser virtualUser = new VirtualUser(mazeBox, game.getPlayer());

				position = virtualUser.getFirstIterationLoop();
				for (int i = 0; i < position.length; i++) {
					if (autorunStopped) {
						return;
					}
					sleep(500);
					moveTo(position[i]);
				}
				position = virtualUser.getIterationLoop();
				for (int i = 0; i < position.length; i++) {
					if (autorunStopped) {
						return;
					}
					sleep(500);
					moveTo(position[i]);

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		if (autorun)
			thread.start();
	}

	private void moveTo(PositionInMaze position) {
		game.moveTo(position);
		repaint();
	}
}