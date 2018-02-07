package com.pinpin.core.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import android.os.Handler;

public interface HttpClient<G, P> {
	public ResponseWrapper<G> get(String url, Map<String, List<String>> headers);
	
	public ResponseWrapper<P> post(String url, Map<String, List<String>> headers,
			Map<String, List<String>> postDatas, MultipartFile... files) throws IOException;
    
    public void setDefaultSSLSocketFactory(SSLSocketFactory sf);

    public void setHostnameVerifier(HostnameVerifier hv);
}

