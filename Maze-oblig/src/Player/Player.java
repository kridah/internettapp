package Player;


import simulator.PositionInMaze;

import java.io.Serializable;
import java.util.UUID;

public class Player implements Serializable {
	private PositionInMaze pos;
	private String uuid;
	private String username;

	public Player(PositionInMaze pos, String uuid, String username) {
		this.pos = pos;
		this.uuid = UUID.randomUUID().toString();
		this.username = username;
	}

	public PositionInMaze getPos() {
		return pos;
	}

	public void setPos(PositionInMaze pos) {
		this.pos = pos;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "Player{" +
				"pos=" + pos +
				", uuid='" + uuid + '\'' +
				", username='" + username + '\'' +
				'}';
	}
}
