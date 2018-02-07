/**
 *
 */
package com.pinpin.core.http.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinpin.core.http.CookieHandlerPatch;
import com.pinpin.core.http.MultipartFile;
import com.pinpin.core.http.PostHandler;
import com.pinpin.core.http.ResponseWrapper;
import com.pinpin.core.net.InvalidSocketStateException;
import com.pinpin.utils.IOUtil;
import com.pinpin.utils.Log;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
 

/**
 * @author tony
 *
 */
public abstract class AbstractMultipartPostHandler<T> implements
        PostHandler<T> {

    static final String BOUNDARY = "---------------------------7da10848071c";
    static final String MULTIPART_CONTENT_TYPE = "multipart/form-data;boundary="+ BOUNDARY;
    static final String LINE_END = "\r\n";
    static final String TWO_HYPHEN = "--";
    
    public ResponseWrapper<T> doPost(HttpURLConnection urlConn,
            Map<String, List<String>> headers,
            Map<String, List<String>> postDatas, MultipartFile... files) throws IOException {
        try {
        	urlConn.setRequestMethod("POST");
			URI uri = urlConn.getURL().toURI();
			headers = CookieHandlerPatch.restoreCookieIfNecessary(uri, headers);
		} catch (URISyntaxException e) {
			 if(e.getMessage()!=null)
			Log.e("URL to URI error.", e.getMessage());
			throw new RuntimeException("URL to URI error.", e);
		}
        
        // set request headers
        if (headers != null && !headers.isEmpty()) {
            Set<String> headerKeys = headers.keySet();
            for (String headerKey : headerKeys) {
                List<String> headerValues = headers.get(headerKey);

                for (String headerValue : headerValues) {
                    urlConn.addRequestProperty(headerKey, headerValue);
                }
            }
        }
        
        // add multipart/form-data header
        urlConn.addRequestProperty("Content-Type", MULTIPART_CONTENT_TYPE);
  
        urlConn.addRequestProperty("Accept", "*/*");
         
     
       // urlConn.connect();

        int totalLen = 0;
        // set request body -- multipart/form-data 
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(urlConn.getOutputStream(),8192));
        if (postDatas != null) {
            Set<String> postDataKeys = postDatas.keySet();
            for (String dataKey : postDataKeys) {
                List<String> dataValues = postDatas.get(dataKey);
                
                for (String dataValue : dataValues) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(TWO_HYPHEN).append(BOUNDARY).append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"").append(dataKey).append("\"").append(LINE_END);
                    sb.append(LINE_END);
                    sb.append(dataValue).append(LINE_END);
                    byte[] sbArr = sb.toString().getBytes();
                   
                    outputStream.write(sbArr);
                }
            }
        }
       // urlConn.addRequestProperty("Content-Length",""+totalLen);
        outputStream.flush();
        
        // write file's data
        if(files!=null){
        	for (MultipartFile multipartFile : files) {
                outputStream.writeBytes(TWO_HYPHEN + BOUNDARY + LINE_END);
                outputStream.write(("Content-Disposition: form-data; name=\""+multipartFile.getName()+"\"; filename=\""+multipartFile.getFilename()+"\""+LINE_END).getBytes());
                outputStream.writeBytes("Content-Type: " + multipartFile.getContentType() + LINE_END + LINE_END);
                
                File file = multipartFile.getFile();
                if (file != null) {
//                	//Log.e("File!!!", file.getAbsolutePath());
                    InputStream fileInputStream = null;
                    try {
                        fileInputStream = new BufferedInputStream(new FileInputStream(file), 8192);
                        IOUtil.copyTo(fileInputStream, outputStream);
                        outputStream.flush();
                    } catch (IOException e) {
//                    	//Log.e("//System.out======", "++++++++++++++++");
//                        logger.e(e.getMessage(), e);
                        throw e;
                    } finally {
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                    }
                }
                outputStream.writeBytes(LINE_END);
                
                int fileLength = (TWO_HYPHEN + BOUNDARY + LINE_END).getBytes().length +
                		("Content-Type: " + multipartFile.getContentType() + LINE_END + LINE_END).getBytes().length +
                		file.toString().getBytes().length + (LINE_END).getBytes().length;
            }
        }
        
        
        // write multipart/form-data request body last line.
        StringBuilder sb = new StringBuilder();
        sb.append(TWO_HYPHEN).append(BOUNDARY).append(TWO_HYPHEN).append(LINE_END);
        outputStream.writeBytes(sb.toString());
       
        outputStream.flush();
        outputStream.close();

        // GET response 
        int responseCode = urlConn.getResponseCode();
       
        Map<String, List<String>> responseHeaders = urlConn.getHeaderFields();

        try {
            URI uri = urlConn.getURL().toURI();
            CookieHandlerPatch.storeCookieIfNecessary(uri, responseHeaders);
        } catch (URISyntaxException e) {
//            logger.e("URL to URI error.", e);
            throw new RuntimeException("URL to URI error.", e);
        }

        // read body
        InputStream in = new BufferedInputStream(urlConn.getInputStream(), 8192);


        T body;
        if (needLasyDisconnect()) {
            body = getContent(in, urlConn);
        } 
        else {
            body = getContent(in);
        }

        ResponseWrapper<T> wrapper = new ResponseWrapper<T>(responseCode, body);
        wrapper.setResponseHeaders(responseHeaders);
        for(String name:responseHeaders.keySet()){
 
        	if(TextUtils.equals(name, "cookie")){
//        		logger.d("umap-cookie exsits");
        		wrapper.setRequestSuccess(true);
        		break;
        	}
        
        }
        if(200 != responseCode){
        	wrapper.setRequestSuccess(false);
        }
        in.close();
        
        if (responseCode == -1) {
//        	logger.e("invalid socket state: FIN_WAIT_2");
        	throw new InvalidSocketStateException("invalid socket state: FIN_WAIT_2");
        }
        return wrapper;
    }

    public boolean needLasyDisconnect() {
        return false;
    }

    protected abstract T getContent(InputStream in, Object... args)
            throws IOException;
}
