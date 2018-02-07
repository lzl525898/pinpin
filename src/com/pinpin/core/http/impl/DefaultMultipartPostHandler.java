/**
 * 
 */
package com.pinpin.core.http.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
 
import android.os.Handler;
import android.os.Message;


/**
 * @author tony
 *
 */
public class DefaultMultipartPostHandler extends AbstractMultipartPostHandler<String> {
	private String charset = "utf8"; 
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	@Override
	protected String getContent(InputStream in, Object... args)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[128];
		int total = 0;
		while(true) {
			int len = in.read(buffer);
			if (len < 0)
				break;
			total += len;
			baos.write(buffer, 0, len);
		}
		
		byte[] body = baos.toByteArray();
		return new String(body, charset);
	}

}
