/**
 * 
 */
package com.pinpin.core.http;

 
import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

/**
 * @author tony
 *
 */
public class CookieManager extends CookieHandler {
	 
	private List<HttpCookie> cookieStore = new LinkedList<HttpCookie>();
	private static CookieManager instance = new CookieManager();
	
	private CookieManager() {
		
	}
	
	public static CookieManager getInstance() {
		return instance;
	}
	
	public void clear() {
		cookieStore.clear();
	}
	
	/* (non-Javadoc)
	 * @see java.net.CookieHandler#get(java.net.URI, java.util.Map)
	 */
	@Override
	public Map<String, List<String>> get(URI uri,
			Map<String, List<String>> requestHeaders) throws IOException {
//		logger.d("get(URI, Map<String, List<String>) is called. URI: " + uri.toString());
		
		StringBuilder cookies = new StringBuilder();
	    for (HttpCookie cookie : cookieStore) {
	      // Remove cookies that have expired
	      if (cookie.hasExpired()) {
	        cookieStore.remove(cookie);
	      } else if (cookie.matches(uri)) {
	        if (cookies.length() > 0) {
	          cookies.append(", ");
	        }
//	        //Log.e("uri!!!","cookie:"+cookie.toString());
	        cookies.append(cookie.toString());
	      }
	    }

	    Map<String, List<String>> cookieMap = new HashMap<String, List<String>>();
	    if (requestHeaders != null)
	    	cookieMap.putAll(requestHeaders);
	    
	    if (cookies.length() > 0) {
	      List<String> list = Collections.singletonList(cookies.toString());
	      cookieMap.put("Cookie", list);
	    }
	    
//	    logger.d("CookieMap: " + cookieMap);
	    return Collections.unmodifiableMap(cookieMap);
	}

	/* (non-Javadoc)
	 * @see java.net.CookieHandler#put(java.net.URI, java.util.Map)
	 */
	@Override
	public void put(URI uri, Map<String, List<String>> responseHeaders)
			throws IOException {
		//Log.e("put(URI, Map<String, List<String>) is called. URI: " , uri.toString());
		
		Set<String> headerKeys = responseHeaders.keySet();
		String setCookieKey = "Set-Cookie";
		for (String headerKey : headerKeys) {
			if ("Set-Cookie".equalsIgnoreCase(headerKey)) {
				setCookieKey = headerKey;
				break;
			}
		}
		List<String> setCookieList = responseHeaders.get(setCookieKey);
		
	    if (setCookieList != null) {
	      for (String item : setCookieList) {
	    	 
	        HttpCookie cookie = new HttpCookie(uri, item);
	        //Log.e("cookie",cookie.toLog());
	        for (HttpCookie existingCookie : cookieStore) {
	        	 //Log.e("existingCookie",existingCookie.toLog());
	          if ((cookie.getURI().equals(existingCookie.getURI()))
	              && (cookie.getName().equals(existingCookie.getName()))) {
	            cookieStore.remove(existingCookie);
	            break;
	          }
	        }
	        cookieStore.add(cookie);
	      }
	    }
	}

}
