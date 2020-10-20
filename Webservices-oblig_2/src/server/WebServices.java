package server;

public interface WebServices {

	String returnUserFromLdap();
	String returnFortuneCookie();
	Double returnByteSize(Object o);

}
