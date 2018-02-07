/**
 * 
 */
package com.pinpin.core.http.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author tony
 *
 */
public class DefaultGetHandler extends AbstractGetHandler<String> {
	private String charset = "utf8";
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	protected String getContent(InputStream in, Object... args) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[128];
		while(true) {
			int len = in.read(buffer);
			if (len < 0) {
                break;
            }
			
			baos.write(buffer, 0, len);
		}
		
		byte[] body = baos.toByteArray();
		
		
		return new String(body, charset);
	}

}

