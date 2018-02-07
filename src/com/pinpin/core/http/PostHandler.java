package com.pinpin.core.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import android.os.Handler;

/**
 * 
 * http post 
 * 
 * @author lixd186
 *
 */
public interface PostHandler<T> {
	public ResponseWrapper<T> doPost(HttpURLConnection urlConn,  
			Map<String, List<String>> headers, 
			Map<String, List<String>> postDatas, MultipartFile... files) throws IOException;
	
	public boolean needLasyDisconnect();
}
