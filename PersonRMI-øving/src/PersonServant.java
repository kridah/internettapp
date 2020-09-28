import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PersonServant extends UnicastRemoteObject implements PersonService {
	protected PersonServant() throws RemoteException {
	}

	@Override
	public Person echo(Person person, String newName) throws RemoteException {
		person = setName(person, newName);
		return person;
	}

	public Person setName(Person person, String newName) throws RemoteException {
		person.setFirstName(newName);
		return person;
	}
}
