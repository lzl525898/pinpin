package com.pinpin.core.net.ssl;
 
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 *
 * @author lixd186
 */
public class DefaultSSLSocketFactory extends SSLSocketFactory {
    private static final String AGREEMENT = "TLS";
    
    public static final TrustManager[] INSECURE_TRUST_MANAGER = new TrustManager[] {
        new InsecureX509TrustManager()
    };
    
    private SSLSocketFactory mInsecureFactory = null;
    private SSLSocketFactory mSecureFactory = null;
    private TrustManager[] mTrustManagers = null;
    private KeyManager[] mKeyManagers = null;

    private final boolean mSecure;
    
//    public DefaultSSLSocketFactory(int handshakeTimeoutMillis) {
//        this(handshakeTimeoutMillis, true);
//    }
//    
    private DefaultSSLSocketFactory(boolean secure) {
        mSecure = secure;
    }
    
     public static SSLSocketFactory getInsecure() {
        return new DefaultSSLSocketFactory(false);
    }
    
    public static SSLSocketFactory getDefault() {
        return new DefaultSSLSocketFactory(true);
    }
    
    private synchronized SSLSocketFactory getDelegate() {
        // Relax the SSL check if instructed (for this factory, or systemwide)
        if (!mSecure) {
            if (mInsecureFactory == null) {
                try {
                    if (mSecure) {
//                        logger.w("*** BYPASSING SSL SECURITY CHECKS (socket.relaxsslcheck=yes) ***");
                    } else {
//                        logger.w("Bypassing SSL security checks at caller's request");
                    }
                    
                    SSLContext sc = SSLContext.getInstance(AGREEMENT);
                    sc.init(mKeyManagers, INSECURE_TRUST_MANAGER, new java.security.SecureRandom());

                    
                    mInsecureFactory = sc.getSocketFactory();
                } catch (KeyManagementException ex) {
//                    logger.e(ex.getMessage(), ex);
                    throw new RuntimeException(ex);
                } catch (NoSuchAlgorithmException ex) {
//                    logger.e(ex.getMessage(), ex);
                    throw new RuntimeException(ex);
                }
            }
            return mInsecureFactory;
        } else {
            if (mSecureFactory == null) {
                mSecureFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            }
            return mSecureFactory;
        }
    }
    
    
    /**
     * Sets the {@link TrustManager}s to be used for connections made by this factory.
     */
    public void setTrustManagers(TrustManager[] trustManager) {
        mTrustManagers = trustManager;

        // Clear out all cached secure factories since configurations have changed.
        mSecureFactory = null;
        // Note - insecure factories only ever use the INSECURE_TRUST_MANAGER so they need not
        // be cleared out here.
    }

    /**
     * Sets the {@link KeyManager}s to be used for connections made by this factory.
     */
    public void setKeyManagers(KeyManager[] keyManagers) {
        mKeyManagers = keyManagers;

        // Clear out any existing cached factories since configurations have changed.
        mSecureFactory = null;
        mInsecureFactory = null;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return getDelegate().getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return getDelegate().getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
//        logger.d("s: " + s.toString() + "; host: " + host + "; port: " + port +"; autoClose: " + autoClose);
        return getDelegate().createSocket(s, host, port, autoClose);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
//        logger.d("host: " + host + "; port: " + port);
        return getDelegate().createSocket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
//        logger.d("String host: " + host + "; port: " + port + "; InetAddress localHost: " + localHost.toString() + "; localPort: " + localPort);
        return getDelegate().createSocket(host, port, localHost, localPort);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
//        logger.d("InetAddress host: " + host.toString() + "; port: " + port);
        return getDelegate().createSocket(host, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
 //       logger.d("InetAddress address: " + address.toString() + "; port: " + port + ";InetAddress localAddress: " + localAddress.toString() + "; localPort: " + localPort);
        return getDelegate().createSocket(address, port, localAddress, localPort);
    }
    
}
