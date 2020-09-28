import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PersonService extends Remote {

	Person echo(Person person, String newName) throws RemoteException;
}
