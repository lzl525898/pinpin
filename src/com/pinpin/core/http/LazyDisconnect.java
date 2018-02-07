package com.pinpin.core.http;

import java.io.InputStream;

/**
 * 
 * lasy close URLConnection flag interface
 * LasyDisconnect object must be called disconnect() method.
 * 
 * @author lixd186
 *
 */
public interface LazyDisconnect {
	public void disconnect();
	
	public InputStream getSource();
}
