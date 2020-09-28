import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

	public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
		PersonService service = (PersonService) Naming.lookup("rmi://localhost:5099/person");

		Person person = new Person("Kristoffer");
		String oldName = person.getFirstName();
		person = service.echo(person, "Martin");

		System.out.printf("Hey, %s, your new name is %s",oldName, person.getFirstName());
	}
}
