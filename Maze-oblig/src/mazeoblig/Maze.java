package mazeoblig;

import simulator.PositionInMaze;
import simulator.VirtualUser;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import static java.lang.Thread.sleep;

/**
 * Class responsible for drawing the maze and populating it with clients
 */
@SuppressWarnings("serial")
public class Maze extends JApplet {
	public final static int DIM = 50;        // størrelse på labyrint
	boolean skipPlayerUpdate = false;
	private Thread thread;
	private Game game;
	private Box[][] mazeBox;
	private Boolean autorun = true;
	private Boolean autorunStopped = false;
	private JPanel panel;
	private Graphics graphics;
	private Image image;

	/**
	 * Establish server and registry connection (will only work if server and client is run from the same computer)
	 * Retrieve all remote objects from RMI server. Method supplied at project start, modified by author
	 */
	public void init() {
		try {
			game = new Game();
		} catch (RemoteException re) {
			System.err.println(re.getMessage());
			System.exit(1);
		}
		mazeBox = game.getMaze();

		// GUI
		int frameWidth = DIM * 10;
		int frameHeight = DIM * 10;
		image = createImage(frameWidth, frameHeight);
		graphics = image.getGraphics();

		int dim = DIM;
		int x, y;
		for (x = 0; x < (dim - 1); x++) {
			for (y = 0; y < (dim - 1); y++) {
				if (mazeBox[x][y].getUp() == null)
					graphics.drawLine(x * 10, y * 10, x * 10 + 10, y * 10);
				if (mazeBox[x][y].getDown() == null)
					graphics.drawLine(x * 10, y * 10 + 10, x * 10 + 10, y * 10 + 10);
				if (mazeBox[x][y].getLeft() == null)
					graphics.drawLine(x * 10, y * 10, x * 10, y * 10 + 10);
				if (mazeBox[x][y].getRight() == null)
					graphics.drawLine(x * 10 + 10, y * 10, x * 10 + 10, y * 10 + 10);
			}

			/* Tegner GUI med visuelle representasjon av labyrint for klienten*/
			setBackground(Color.white);
			panel = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(image, 0, 0, this);

					game.getMazeClients().forEach((key, virtualPlayer) -> {
						if (key.equals(game.getPlayer().getUuid())) {
							return;
						} else {
							g.setColor(Color.black);            // farge for alle andre spillere i GUI
						}
						g.fillOval(virtualPlayer.getPosition().getXpos() * 10, virtualPlayer.getPosition().getYpos() * 10, 8, 8);
					});

					// Setter den lokale brukerens farge
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
					if (skipPlayerUpdate) {
						skipPlayerUpdate = false;
					} else {
						repaint();
					}
				}
			};
			int refreshRate = 500;
			Timer timer = new Timer(refreshRate, actionListener);
			timer.setRepeats(true);
			timer.start();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					game.logout();
				}
			});
		}
	}

	public void start() {
		autopilot();
	}

	/* Fører spillere gjennom labyrinten basert på IterationLoop */
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
				System.err.println("Autopilot feilet. Mistet tråden");
				e.getLocalizedMessage();
			}
		});
		if (autorun)
			thread.start();
	}

	private void moveTo(PositionInMaze position) {
		skipPlayerUpdate = true;
		game.moveTo(position);
		repaint();
	}
}