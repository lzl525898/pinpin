/**
 * 
 */
package com.pinpin.core.http.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.pinpin.utils.Log;
 


/**
 * @author tony
 *
 */
public class DefaultDownloadHandler extends AbstractGetHandler<String> {
	private String charset = "utf8";
	private File target ;
	
	
	
	public DefaultDownloadHandler(File target) {
		super();
		this.target = target;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	protected String getContent(InputStream in, Object... args) throws IOException {
//		 if(!target.exists()){
//			 target.createNewFile();
//		 }
		Log.e("getContent","getContent被调用");
		if(target.exists()&&target.length()>0){
			return target.getAbsolutePath();
		}
		FileOutputStream baos = new FileOutputStream(target);
		byte[] buffer = new byte[128];
		while(true) {
			int len = in.read(buffer);
			if (len < 0) {
                break;
            }
			
			baos.write(buffer, 0, len);
		}
		
		baos.close();
		
		
		return target.getAbsolutePath();
	}

}

