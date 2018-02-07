package com.pinpin.core.http.impl;

 
import java.io.IOException;
import java.net.Authenticator;
import java.net.BindException;
import java.net.ConnectException;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

 

import org.json.JSONObject;

import com.pinpin.core.http.CookieManager;
import com.pinpin.core.http.GetHandler;
import com.pinpin.core.http.HttpClient;
import com.pinpin.core.http.MultipartFile;
import com.pinpin.core.http.PostHandler;
import com.pinpin.core.http.ResponseWrapper;
import com.pinpin.core.net.ssl.DefaultSSLSocketFactory;
import com.pinpin.core.net.ssl.InsecureHostnameVerifier;
import com.pinpin.utils.Log;

import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
 
public class DefaultHttpClient<G, P> implements HttpClient<G, P> {

    private static int TIMEOUT = 30000;
    
    private GetHandler<G> getHandler;
    private PostHandler<P> postHandler;
    private SSLSocketFactory customizedSSLSocketFactory;
    private HostnameVerifier customizedHostnameVerifier;

    public DefaultHttpClient() {
        this(new DefaultGetHandler(), new DefaultMultipartPostHandler());
    }

    public DefaultHttpClient(GetHandler<G> getHandler) {
        this(getHandler, new DefaultMultipartPostHandler());
    }
    
    

    public DefaultHttpClient(PostHandler<P> postHandler) {
        this(new DefaultGetHandler(), postHandler);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public DefaultHttpClient(GetHandler getHandler, PostHandler postHandler) {
        this.getHandler = getHandler;
        this.postHandler = postHandler;

        disableConnectionReuseIfNecessary();
        CookieHandler.setDefault(CookieManager.getInstance());
        CookieHandler cookieHandler = CookieHandler.getDefault();

        if (cookieHandler == null) {
//            logger.d("Default CookieHandler is null.");
        } else {
//            logger.d("Default CookieHandler is " + cookieHandler.getClass().getName());
        }
    }

    /**
     *
     * HTTP basic authentication
     *
     * @param authenticator
     */
    public void setAuthenticator(Authenticator authenticator) {
        Authenticator.setDefault(authenticator);
    }

//    /**
//     * 
//     * @param trustManager 
//     */
//    public void setTrustManager(TrustManager trustManager) {
//        
//    }
    public void setDefaultSSLSocketFactory(SSLSocketFactory sf) {
        customizedSSLSocketFactory = sf;
    }

    public void setHostnameVerifier(HostnameVerifier hv) {
        customizedHostnameVerifier = hv;
    }

 
    private void initSSL(HttpsURLConnection conn) {
        // ssl hostname verifier
        if (customizedHostnameVerifier != null) {
            conn.setHostnameVerifier(customizedHostnameVerifier);
        } else {
            conn.setHostnameVerifier(new InsecureHostnameVerifier());
        }
        // ssl SSLSocketFactory setup
        if (customizedSSLSocketFactory != null) {
            conn.setSSLSocketFactory(customizedSSLSocketFactory);
        } else {
            SSLSocketFactory sf = DefaultSSLSocketFactory.getInsecure();
            conn.setSSLSocketFactory(sf);
        }
    }

    public ResponseWrapper<G> get(String url, Map<String, List<String>> headers) {
        if (url == null || !url.startsWith("http")) {
            throw new IllegalArgumentException("invalid url.");
        }

        HttpURLConnection urlConn = null;
        try {
            URL getUrl = new URL(url);
            urlConn = (HttpURLConnection) getUrl.openConnection();

//            if (url.startsWith("https://")) {
//                initSSL((HttpsURLConnection) urlConn);
//            }

            urlConn.setConnectTimeout(TIMEOUT);
            urlConn.setDefaultUseCaches(false);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("connection", "close");
            ResponseWrapper<G> response = getHandler.doGet(urlConn, headers);

            return response;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url. " + e.getMessage());
        } catch (IOException e) {
//            logger.e(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (null != urlConn && !getHandler.needLasyDisconnect()) {
                urlConn.disconnect();
            }
        }

    }

    private void disableConnectionReuseIfNecessary() {
        // work around prior to Froyo bugs in HTTP connection reuse
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
//        	logger.i("disable connection reuese.");
            System.setProperty("http.keepAlive", "false");
        }
    }
    
   

	@Override
	public ResponseWrapper<P> post(String url,
			Map<String, List<String>> headers,
			Map<String, List<String>> postDatas,
			MultipartFile... files) throws IOException {
		// TODO Auto-generated method stub
		 if (url == null || !url.startsWith("http")) {
	            throw new IllegalArgumentException("invalid url.");
	        }

	       HttpURLConnection urlConn = null;
	        try {
	            URL getUrl = new URL(url);
//	            //Log.e("current url",url);
	            String referer = null;
	            referer = url;
	             
                Log.e("referer",referer);
	            urlConn = (HttpURLConnection) getUrl.openConnection();
//	            if (url.startsWith("https://")) {
//	                initSSL((HttpsURLConnection)urlConn);
//	            }
                urlConn.setConnectTimeout(TIMEOUT);
	            urlConn.setDefaultUseCaches(false);
	            urlConn.setUseCaches(false);
	            urlConn.setDoInput(true);
	            urlConn.setDoOutput(true);
	            urlConn.setRequestProperty("referer", referer);
	            urlConn.setRequestProperty("Connection", "close");
	            ResponseWrapper<P> response = postHandler.doPost(urlConn, headers, postDatas, files);

	            return response;
	        } catch (MalformedURLException e) {
	            throw new IllegalArgumentException("Invalid url. " + e.getMessage());
	        }finally {
	            if (null != urlConn && !postHandler.needLasyDisconnect()) {
	                urlConn.disconnect();
	            }
	        }
	}
}
