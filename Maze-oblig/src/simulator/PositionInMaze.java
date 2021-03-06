package simulator;

import java.io.Serializable;
import java.util.Objects;

public class PositionInMaze implements Serializable {

	private int xpos, ypos;
	
	public PositionInMaze(int xp, int yp) {
		this.xpos = xp;
		this.ypos = yp;
	}

	public int getXpos() {
		return xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public PositionInMaze up() { return new PositionInMaze(xpos, ypos - 1);}
	public PositionInMaze down() { return new PositionInMaze(xpos, ypos + 1);}
	public PositionInMaze left() { return new PositionInMaze(xpos - 1, ypos);}
	public PositionInMaze right() { return new PositionInMaze(xpos + 1, ypos);}

	public String toString() {
		return "xpos: " + xpos + "\typos: " + ypos;
	}

	public boolean equals(PositionInMaze o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PositionInMaze that = (PositionInMaze) o;
		return xpos == that.xpos &&
				ypos == that.ypos;
	}

}
