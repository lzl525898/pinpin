package com.pinpin.core.http.impl;

 

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinpin.core.http.CookieHandlerPatch;
import com.pinpin.core.http.GetHandler;
import com.pinpin.core.http.ResponseWrapper;

public abstract class AbstractGetHandler<T> implements GetHandler<T> {
 
	public ResponseWrapper<T> doGet(HttpURLConnection urlConn,
			Map<String, List<String>> headers) throws IOException {
		try {
			URI uri = urlConn.getURL().toURI();
			headers = CookieHandlerPatch.restoreCookieIfNecessary(uri, headers);
		} catch (URISyntaxException e) {
//			logger.e("URL to URI error.", e);
			throw new RuntimeException("URL to URI error.", e);
		}
		
		
		if (headers != null && !headers.isEmpty()) {
			Set<String> headerKeys = headers.keySet();
			for (String headerKey : headerKeys) {
				List<String> headerValues = headers.get(headerKey);
				
				for (String headerValue : headerValues) {
					urlConn.addRequestProperty(headerKey, headerValue);
                }
			}
		}
		
		
		Map<String, List<String>> responseHeaders = urlConn.getHeaderFields();
		
		try {
			URI uri = urlConn.getURL().toURI();
			CookieHandlerPatch.storeCookieIfNecessary(uri, responseHeaders);
		} catch (URISyntaxException e) {
//			logger.e("URL to URI error.", e);
			throw new RuntimeException("URL to URI error.", e);
		}
		// read body
		InputStream in = new BufferedInputStream(urlConn.getInputStream(), 8192);
		
		int responseCode = urlConn.getResponseCode();
		
		T body = null;
		if(needLasyDisconnect())
			body = getContent(in, urlConn);
		else
			body = getContent(in);
		
		ResponseWrapper<T> wrapper = new ResponseWrapper<T>(responseCode, body);
		wrapper.setResponseHeaders(responseHeaders);
		return wrapper;
	}
	
	protected abstract T getContent(InputStream in, Object... args) throws IOException;

	public boolean needLasyDisconnect() {
		return false;
	}

	
	
}
