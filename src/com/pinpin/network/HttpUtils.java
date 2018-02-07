package com.pinpin.network;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;

import com.pinpin.core.http.MultipartFile;

public class HttpUtils {
	public static HttpClient client;
	public static KeyStore keyStore;

	public enum RequestMethod {
		GET, POST,MULTIPART,DOWNLOAD
	};

	public static class RequestData {
		public RequestMethod method = RequestMethod.POST;
		public HttpEntity entity;
		public String uri;
		public String encode ="UTF-8";
		public String toast = "加载中...";
		public String jsonStr;
		public String target;
		 
		public HashMap<String,  List<String>> body;
		public MultipartFile[] files;
	}
	public static RequestData simplePostData(String url,final HashMap<String,  String> data){
		RequestData request = new RequestData();
		request.uri = url;
		request.body = new  HashMap<String,  List<String>>();
		for(final String key:data.keySet()){
			 List<String> list = new ArrayList<String>(){{
				 add(data.get(key));
				 }};
				 request.body.put(key, list); 
		}
		return request;
	}
	public static RequestData fileData(String url,final HashMap<String,  String> files){
		RequestData request = new RequestData();
		request.method = RequestMethod.MULTIPART;
		request.uri = url;
		request.files = new MultipartFile[files.keySet().size()];
		request.body = new  HashMap<String,  List<String>>();
		int i = 0;
		for(final String key:files.keySet()){
			MultipartFile mfile = new MultipartFile(key, new File(files.get(key)));
			request.files[i] = mfile;
			i++;
		}
		return request;
	}
	public static RequestData simpleFileData(String url,String key,File file){
		RequestData request = new RequestData();
		request.method = RequestMethod.MULTIPART;
		request.uri = url;
		request.files = new MultipartFile[1];
		request.files[0] = new MultipartFile(key,file);
		request.body = new  HashMap<String,  List<String>>();
		return request;
	}
	public static class ResposneBundle {
		public String charset;
		public String path;
		public int index;
		public byte[] result;

		public ResposneBundle(String charset, byte[] result) {
			super();
			this.charset = charset;
			this.result = result;
		}
		
		public ResposneBundle(String path) {
			super();
			this.path = path;
			 
		}

		public String getContent() {
			if (result != null) {
				try {
					return new String(result, charset);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

	}

	/*public static ResposneBundle excuteRequest(RequestData data,TaskResultListener listener) {
		 
		InputStream in = null;
		ByteArrayOutputStream out = null;
		HttpUriRequest request = null;
		ResposneBundle result = null;
		try {
			HttpClient client = new DefaultHttpClient();

			switch (data.method) {
			case GET:
				request = new HttpGet(data.uri);
				break;
			case POST:
				request = new HttpPost(data.uri);

				if (data.body != null && data.body.size() > 0) {
					Log.e("post params", data.body.toString());
					List<NameValuePair> list = new ArrayList<NameValuePair>();
					for (String key : data.body.keySet()) {
						for (String value : data.body.get(key)) {
							BasicNameValuePair param = new BasicNameValuePair(
									key, URLEncoder.encode(value, data.encode));
							list.add(param);
						}
					}
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
					((HttpPost) request).setEntity(entity);
				}

				break;
			}

			HttpResponse response = client.execute(request);

			long contentLength = response.getEntity().getContentLength();
			HeaderElement[] contentType = response.getEntity().getContentType()
					.getElements();
			String charset = null;
			for (HeaderElement element : contentType) {
				for (int i = 0; i < element.getParameterCount(); i++) {
					NameValuePair pair = element.getParameter(i);
					Log.e("contentType", "Name:" + pair.getName() + " Value:"
							+ pair.getValue());
					if (pair.getName().equalsIgnoreCase("charset")) {
						charset = pair.getValue();
					}
				}
			}

			int responseCode = response.getStatusLine().getStatusCode();

			Log.e("返回code", "" + responseCode);
			in = response.getEntity().getContent();
			// EntityUtils.toByteArray(response.getEntity());

			out = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			while (true) {
				int len = in.read(buffer);
				if (len == -1) {
					break;
				}
				out.write(buffer, 0, len);
			}
			byte[] responseArr = out.toByteArray();
			// 如若charset为空，设置默认为UTF-8�?
			if (TextUtils.isEmpty(charset)) {
				 charset = "UTF-8";
			}
			Log.e("charset", ":" + charset);
			result = new ResposneBundle(charset, responseArr);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			if(listener!=null){
				listener.failed("网络不给力！");
			}
			//e.printStackTrace();
		} catch (IOException e) {
			if(listener!=null){
				listener.failed("网络不给力！");
			}
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}*/

 

	 
}
