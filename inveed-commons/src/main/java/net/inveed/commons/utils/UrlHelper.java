package net.inveed.commons.utils;

public class UrlHelper {
	public static final String normalizeURL(String uri) {
		while (true) {
			String nuri = uri.replace("//", "/");
			if (nuri.length() == uri.length()) {
				break;
			}
			uri = nuri;
		}
		
		if (uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		if (uri.endsWith("/")) {
			uri = uri.substring(0, uri.length() - 1);
		}
		return uri;
	}
}
