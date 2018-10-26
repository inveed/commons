package net.inveed.commons.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

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
	
	public static final Map<String, String> decodeUrlEncodedValues(String data, String encoding) throws UnsupportedEncodingException {
		HashMap<String, String> ret = new HashMap<>();
		String[] params = data.split("&");
		for (String p : params) {
			String[] pvs = p.split("=");
			if (pvs.length == 1) {
				ret.put(URLDecoder.decode(pvs[0].trim(), encoding), "");
			} else if (pvs.length == 2) {
				ret.put(URLDecoder.decode(pvs[0].trim(), encoding), URLDecoder.decode(pvs[1].trim(), encoding));
			} else {
				//TODO: LOG
			}
		}
		return ret;
	}
}
