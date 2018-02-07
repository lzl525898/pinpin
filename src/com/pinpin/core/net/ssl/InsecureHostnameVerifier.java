package com.pinpin.core.net.ssl;

 
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 
 * ignore hostname verify.
 *
 * @author lixd186
 */
public class InsecureHostnameVerifier implements HostnameVerifier{
  
    public boolean verify(String hostname, SSLSession session) {
        String peerHost = session.getPeerHost();
        int peerPort = session.getPeerPort();
        
//        logger.d("hostname: " + hostname + "; peer host: " + peerHost + "; peer port: " + peerPort);
        
        return true;
    }
    
}
