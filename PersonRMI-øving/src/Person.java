import java.io.Serializable;

public class Person implements Serializable {
	private String firstName;

	public Person(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String setFirstName(String firstName) {
		return this.firstName = firstName;
	}

}
