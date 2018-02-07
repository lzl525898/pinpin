package com.pinpin.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class VersionCheckManager {
   public static String cookie =null;
	// 从服务器端下载文件
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (null != l) {
				l.finish((String)msg.obj);
			}
		}
	};

	public interface VersionCheckListener {
		void finish(String json);

	}

	VersionCheckListener l;
	InputStream is;
	ByteArrayOutputStream fos;
	public String threadId;

	public VersionCheckManager(VersionCheckListener l) {
		this.l = l;

	}

	public void checkVersion(final String fileUrl) {
		//Log.e("verison_file_url",fileUrl);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				threadId = Thread.currentThread().getName();
				//Log.e("threadId",threadId);
				String json = downLoadFile(fileUrl);
				//Log.e("verison_text",json);
				Message msg = handler.obtainMessage();
				msg.obj = json;
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 
	 * @param pair
	 * @return int 0>=成功加版本号 -1 -- 无法创建目录 -2 SD卡没有空间，-3 sd卡没有加载 -4--responseCode
	 *         > 400 -5--下载出错
	 */
	private String downLoadFile(String fileUrl) {
		// TODO Auto-generated method stub
		int code = -5;
		String json = null;
		String httpUrl = encodeChinese(fileUrl);
		try {
			URL url = new URL(httpUrl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				/* if (httpUrl.startsWith("https://")) {
					  ((HttpsURLConnection)conn).setHostnameVerifier(new InsecureHostnameVerifier());
				      SSLSocketFactory sf = DefaultSSLSocketFactory.getInsecure();
				      ((HttpsURLConnection)conn).setSSLSocketFactory(sf);
				 }*/
				 conn.setRequestMethod("GET");
				 conn.setRequestProperty("Accept","*/*");
				conn.setDoInput(true);
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				
				int totalLength = conn.getContentLength();
				//Log.e("totalLength", totalLength+"----");
//				Toast.makeText(, "totalLength : " + totalLength, )
				
				is = conn.getInputStream();
				fos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int length = 0;
				if (conn.getResponseCode() >= 400) {
					code = -4;
				} else {
					while (true) {
						if (is != null) {
							int numRead = is.read(buf);
							//Log.e("numRead", numRead+"");
							if ( numRead==-1 ) {
								//if (length >= totalLength) {
									// handler.sendEmptyMessage(DOWNLOAD_SECCESS);
									byte[] result = fos.toByteArray();
									json = new String(result);
									
								//} else {
								//	code = -5;
								//}
								break;
							} else {
								fos.write(buf, 0, numRead);
								length = length + numRead;
							}
						} else {
							code = -5;
							break;
						}
					}
				}
				conn.disconnect();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			code = -5;
		} finally {
			close(is, fos);
		}
		if (TextUtils.isEmpty(json)) {
			HashMap map = new HashMap();
			map.put("error_code", code);
			JSONObject js = new JSONObject(map);
			json = js.toString();
		}
		return json;
	}

	// 打开APK程序代码

	private void install(File file, Context context) {
		// TODO Auto-generated method stub
		Log.d("OpenFile", file.getName());
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	private void close(InputStream bis, OutputStream os) {
		try {
			if (bis != null) {
				bis.close();
			}
			if (os != null) {
				os.flush();
				os.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			//Log.e("下载文件出错2", "下载文件出错2");
		}
	}

	private String encodeChinese(String url) {
		StringBuilder sb = new StringBuilder();
		Uri uri = Uri.parse(url);
		String scheme = uri.getScheme();
		String host = uri.getHost();
		int port = uri.getPort();
		String path = uri.getPath();
		if (port == -1) {
			if (TextUtils.equals("http", scheme)) {
				port = 80;
			} else if (TextUtils.equals("https", scheme)) {
				port = 443;
			}
		}

		String arr[] = path.split("/");
		for (String part : arr) {
			Log.d("s", part);
			char[] cahrArr = part.toCharArray();
			boolean hasChinese = false;
			for (char c : cahrArr) {
				Log.d("c", c + "");
				Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
				if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
						|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
						|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
						|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
						|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
						|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
					hasChinese = true;
				}

			}
			if (hasChinese) {
				try {
					part = URLEncoder.encode(part, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sb.append("/");
			sb.append(part);

		}
		String myurl = sb.substring(1).toString();

		myurl = scheme + "://" + host + ":" + port + myurl;
		Log.d("sb", myurl);
		return myurl;
	}

}
