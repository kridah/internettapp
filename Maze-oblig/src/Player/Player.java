package Player;

import simulator.PositionInMaze;
import java.io.Serializable;
import java.util.UUID;

public class Player implements Serializable {
	private PositionInMaze position;
	private String uuid;
	private String username;

	public Player(PositionInMaze pos, String uuid, String username) {
		this.position = pos;
		this.uuid = UUID.randomUUID().toString();
		this.username = username;
	}

	public Player(String username, PositionInMaze position) {
		super();
		this.username = username;
		this.position = position;
		this.uuid = UUID.randomUUID().toString();
	}

	public Player(PositionInMaze position) {
		super();
		this.position = position;
		this.uuid = UUID.randomUUID().toString();
	}

	public PositionInMaze getPosition() {
		return position;
	}
	public void setPosition(PositionInMaze position) {
		this.position = position;
	}
	public String getUuid() {
		return uuid;
	}

	@Override
	public String toString() {
		return "Player{" +
				"pos=" + position +
				", uuid='" + uuid + '}';
	}
}
