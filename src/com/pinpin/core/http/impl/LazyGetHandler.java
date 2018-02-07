package com.pinpin.core.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.pinpin.core.http.LazyDisconnect;

public class LazyGetHandler extends AbstractGetHandler<LazyDisconnect> {

	public boolean needLasyDisconnect() {
		return true;
	}

	@Override
	protected LazyDisconnect getContent(final InputStream in, Object... args)
			throws IOException {
		// args[0] is HttpURLConnection
		final HttpURLConnection urlConn = (HttpURLConnection) args[0];
		
		LazyDisconnect ld = new LazyDisconnect() {
			public InputStream getSource() {
				return in;
			}

			public void disconnect() {
				urlConn.disconnect();
			}
		};
		
		return ld;
	}
}
