
/** Klassen definerer brukerfelt som er aktuelle for en LDAP-oppføring */
public class Person {
	// https://help.estos.com/help/en-US/meta/3.5/metadirectory/dokumentation/additional/ldapfields.htm
	String cn;				// common name
	String displayName;
	String givenName;		// fornavn
	String sn;				// etternavn
	String title;
	String company;
	String department;
	String physicalDeliveryOfficeName;
	String mail;
	String telephoneNumber;


	public Person(String cn, String givenName, String sn, String mail) {
		this.cn = cn;
		this.givenName = givenName;
		this.sn = sn;
		this.mail = mail;
	}

	public Person(String cn, String displayName, String givenName, String sn, String title, String company, String department, String physicalDeliveryOfficeName, String mail, String telephoneNumber) {
		this.cn = cn;
		this.displayName = displayName;
		this.givenName = givenName;
		this.sn = sn;
		this.title = title;
		this.company = company;
		this.department = department;
		this.physicalDeliveryOfficeName = physicalDeliveryOfficeName;
		this.mail = mail;
		this.telephoneNumber = telephoneNumber;
	}

	@Override
	public String toString() {
		return "Person{\n" +
				 cn + "\n" +
				 givenName + "\n" +
				 sn + "\n" +
				 mail + "\n" +
				'}';
	}

	public static void main(String[] args) {
		String result = ADConnector.login("kda068@uit.no", "ENTER-PASSWORD-HERE");
		System.out.println(result);
	}
}
