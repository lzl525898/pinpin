package com.pinpin.core.http;

import android.os.Build;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class CookieHandlerPatch {

	public static boolean isAutoCookieManagement() {
		return Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.FROYO;
	}
	
	public static Map<String, List<String>> restoreCookieIfNecessary(URI uri, Map<String, 
			List<String>> requestHeaders) throws IOException {
        if (!isAutoCookieManagement()) {
			CookieHandler cookieHandler = CookieHandler.getDefault();
			return cookieHandler.get(uri, requestHeaders);
		}
        else {
            return requestHeaders;
        }
	}
	
	public static void storeCookieIfNecessary(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
		// fix <= Level 8(FROYO) CookieHandler doesn't work
		if (!isAutoCookieManagement()) {
			CookieHandler cookieHandler = CookieHandler.getDefault();
			cookieHandler.put(uri, responseHeaders);
		}
	}
}
