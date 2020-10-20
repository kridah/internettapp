import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.*;
import java.util.Hashtable;

public class ADConnector {
	public static String login(String username, String password) {
		Person person = null;
		Hashtable<String, String> environment = new Hashtable<>();
		DirContext context = null;


		if (username != null) {
			username = username.trim();
			if (username.length() == 0)
				username = null;
			if (username != null && !username.contains("@uit.no")) {
				username.concat("@uit.no");
			}
		}

		if (password != null) {
			password = password.trim();
			if (password.length() == 0)
				password = null;
		}

		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, "ldap://dc.ad.uit.no:389");
		environment.put(Context.REFERRAL, "follow");
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");

		if (username != null)
			environment.put(Context.SECURITY_PRINCIPAL, username);
		if (password != null)
			environment.put(Context.SECURITY_CREDENTIALS, password);

		try {
			LdapContext ldap = new InitialLdapContext(environment, null);

			String base = "dc=ad, dc=uit, dc=no";
			String filter = "(mail=" + username.replace("@uit.no", "")
									 + "@post.uit.no" + ")";
			SearchControls searchControls = new SearchControls();
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			searchControls.setReturningAttributes(new String[]{
					"givenName", "sn",
					"mail", "cn",
			});
			context = new InitialDirContext(environment);

			// Search
			NamingEnumeration response = context.search(base, filter, searchControls);

			if (response.hasMore()) {
				SearchResult result = (SearchResult) response.next();
				Attributes attributes = result.getAttributes();
				if (attributes != null) {
					System.out.println(attributes.toString());
				/**return attributes.toString();
				 * {
				 * mail=mail: kda068@post.uit.no,
				 * givenname=givenName: Kristoffer S.,
				 * sn=sn: Dahl,
				 * cn=cn: Dahl Kristoffer S. (kda068)
				 * }*/
				person = new Person(
						attributes.get("cn").toString(),
						attributes.get("givenName").toString(),
						attributes.get("sn").toString(),
						attributes.get("mail").toString()
				);
				return person.toString();
//					person = new Person(
//							attributes.get("cn").toString(),
//							attributes.get("title").toString(),
//							attributes.get("givenName").toString(),
//							"",""
//							"",
//							attributes.get("mail").toString(),
//							"",
//							"",
//							attributes.get("cn").toString(),
//							"",
//							""
//					);
//					return person.toString();
				}
			}
			return "Fant ingen oppføringer";

		}catch (AuthenticationException e) {
			return "Authentication failed: ";
		} catch(javax.naming.CommunicationException e){
			return "Failed to connect to Active Directory: ";
		} catch (NamingException e) {
			return "Problem retrieving AD server. RootDSE. Naming exception: ";
		} catch (Exception e) {
			return "Error occured ";
		}
	}
}
