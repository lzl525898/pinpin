package com.pinpin.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class AutoDownLoadManager {

	// 从服务器端下载文件
	private ProgressDialog progressDialog;
	private Context context;

	public interface OnDownloadCompleteListner {
		void onComplete(File file);
	};

	OnDownloadCompleteListner onCompleteListner;

	public void setOnCompleteListner(OnDownloadCompleteListner onCompleteListner) {
		this.onCompleteListner = onCompleteListner;
	}

	InputStream is;
	FileOutputStream fos;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case PROGRESS_IMCREMENT:

				progressDialog.setProgress(msg.arg1);
				break;
			case HTTP_CONNETION_FAIL:
				System.out.println("   HTTP_CONNETION_FAIL   ");
				Toast.makeText(context, "HTTP_CONNETION_FAIL 连接超时,请检查网络或稍候重试",
						Toast.LENGTH_SHORT).show();
				break;
			case DOWNLOAD_FAILED:
				Toast.makeText(context, "下载时出错，请重新再试", Toast.LENGTH_SHORT)
						.show();
				break;

			case DOWNLOAD_SECCESS:
				if (progressDialog != null) {
					progressDialog.dismiss();
					if (downloadFile != null) {
						if (onCompleteListner != null) {
							onCompleteListner.onComplete(downloadFile);
							break;
						}
						install(downloadFile, context);
					}
					progressDialog.setProgress(0);
				}
				break;
			}
		}
	};
	File downloadFile;
	private static final int PROGRESS_IMCREMENT = 1000;
	private static final int HTTP_CONNETION_FAIL = 4000;
	private static final int DOWNLOAD_SECCESS = 1001;
	private static final int DOWNLOAD_FAILED = 4001;
	private static final int DOWNLOAD_CANCELED = 4002;

	public AutoDownLoadManager(Context mcontext) {
		//Log.e("init","AutoDownLoadManager");
		this.context = mcontext;
		
	}

	public void download(final String url, final File target, String title) {
		showProgress(title);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				downloadFile = downLoadFile(url, target);
			}
		}).start();
	}

	// 下载apk程序代码
	private File downLoadFile(String httpUrl, File target) {
		// TODO Auto-generated method stub
		httpUrl = encodeChinese(httpUrl);
		//Log.e("start-download", httpUrl);
		/*
		 * final String fileName = "install.apk"; File tmpFile = new
		 * File("/sdcard/download"); if (!tmpFile.exists()) { tmpFile.mkdir(); }
		 * final File file = new File("/sdcard/download/" + fileName);
		 */

		try {
			URL url = new URL(httpUrl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				 
			  /*  if (httpUrl.startsWith("https://")) {
					  ((HttpsURLConnection)conn).setHostnameVerifier(new InsecureHostnameVerifier());
				      SSLSocketFactory sf = DefaultSSLSocketFactory.getInsecure();
				      ((HttpsURLConnection)conn).setSSLSocketFactory(sf);
				 }*/
				int totalLength = conn.getContentLength();
                //Log.e("totalLength","-"+totalLength);
				is = conn.getInputStream();
				fos = new FileOutputStream(target);
				byte[] buf = new byte[2048];
				// conn.connect();
				double percent = 0;
				int length = 0;
				if (conn.getResponseCode() >= 400) {
					handler.sendEmptyMessage(HTTP_CONNETION_FAIL);
				} else {
					if (-1 == totalLength) {
						while (true) {
							if (is != null) {
								int numRead = is.read(buf);
								
								if (-1 == numRead) {
									handler.sendEmptyMessage(DOWNLOAD_SECCESS);
									break;
								}
								fos.write(buf, 0, numRead);
							}
						}
					} else {
						while (percent <= 100) {
							if (is != null) {
								int numRead = is.read(buf);
								if (numRead <= 0) {
									if (length == totalLength) {
										handler.sendEmptyMessage(DOWNLOAD_SECCESS);
									} else {
										handler.sendEmptyMessage(DOWNLOAD_FAILED);
									}

									break;
								} else {
									fos.write(buf, 0, numRead);
									length = length + numRead;
									 //Log.d("length",length+"");
									 //Log.d("totalLength",totalLength+"");
									percent = ((double) length / (double) totalLength) * 100;
									 //Log.d("percent-d",""+percent);
									handler.sendEmptyMessage((int) percent);
									Message msg = handler.obtainMessage();
									msg.arg1 = (int) percent;
									msg.what = PROGRESS_IMCREMENT;
									msg.sendToTarget();
								}

							} else {
								handler.sendEmptyMessage(HTTP_CONNETION_FAIL);
								break;
							}

						}
					}

				}

				conn.disconnect();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				handler.sendEmptyMessage(HTTP_CONNETION_FAIL);
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			handler.sendEmptyMessage(HTTP_CONNETION_FAIL);
			e.printStackTrace();
		} finally {
			close(is, fos);
		}

		return target;
	}

	// 打开APK程序代码

	private void install(File file, Context context) {
		// TODO Auto-generated method stub
		// Log.d("OpenFile", file.getName());
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

	private void showProgress(String title) {
		if (null == progressDialog) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle(title);
			progressDialog.setMessage("请稍候...");
			progressDialog.setCancelable(true);
			progressDialog.setMax(100);
			progressDialog.setButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// flag = false;
							close(is, fos);
							progressDialog.setProgress(0);
							Toast.makeText(context, "下载已取消", Toast.LENGTH_LONG)
									.show();
						}
					});
			Message msg = new Message();
			msg.what = DOWNLOAD_CANCELED;
			progressDialog.setCancelMessage(msg);
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							close(is, fos);
							progressDialog.setProgress(0);
							Toast.makeText(context, "下载已取消", Toast.LENGTH_LONG)
									.show();
						}

					});
		}
		progressDialog.setProgress(0);
		progressDialog.show();

	}

	private String encodeChinese(String url) {
		StringBuilder sb = new StringBuilder();
		String arr[] = url.split("/");
		for (String part : arr) {
			// Log.d("s",part);
			char[] cahrArr = part.toCharArray();
			boolean hasChinese = false;
			for (char c : cahrArr) {
				// Log.d("c",c+"");
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
		if (!myurl.startsWith("http")) {
			myurl = "http://" + myurl;
		}

		// Log.d("sb",myurl);
		return myurl;
	}

}
