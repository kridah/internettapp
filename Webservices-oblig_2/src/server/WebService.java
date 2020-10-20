package server;

public class WebService implements WebServices {

	// må være på VPN for å koble til ad-tjener
	String adserver = "dc.ad.uit.no";
	String adport = "389";
	String adUsername = "";
	String adPassword = "";

	@Override
	public String returnUserFromLdap() {
		return null;
	}

	@Override
	public String returnFortuneCookie() {
		return null;
	}

	@Override
	public Double returnByteSize(Object o) {
		double size = 0.0;
		return size;
	}
}
