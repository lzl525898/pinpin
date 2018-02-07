package com.pinpin.core.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * 
 * http get 
 * 
 * @author lixd186
 *
 */
public interface GetHandler<T> {
	public ResponseWrapper<T> doGet(HttpURLConnection urlConn, Map<String, List<String>> headers) throws IOException;
	
	public boolean needLasyDisconnect();
}
