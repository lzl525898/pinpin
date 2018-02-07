/**
 * 
 */
package com.pinpin.utils;

/**
 * @author lixd186
 *
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
 

public class IOUtil {

	private static final int BUFFER_SIZE = 128;
	
	private static String[] fileName = 
			{"/www/android.html", 
			"/core/js/client.js", "/core/js/cordova-1.7.0.js", "/core/js/sizzle.min.js", "/core/js/app.js"};
	

	public static void copyTo(InputStream from, OutputStream to)
			throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		while (true) {
			int len = from.read(buffer);
			if (len < 0) {
				break;
			}
			to.write(buffer, 0, len);
		}
	}

	public static void copyFile(File from, File to) throws IOException {
		FileInputStream fis = new FileInputStream(from);
		FileOutputStream fos = new FileOutputStream(to);

		copyTo(fis, fos);

		fis.close();
		fos.close();
	}

	public static byte[] toBytes(Object object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream os = new ObjectOutputStream(baos);
			os.writeObject(object);
			os.close();
		} 
		catch (Exception e) {
		}
		return baos.toByteArray();
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromBytes(byte[] bytes) {
		if (bytes == null)
			return null;
		try {
			ObjectInputStream is = new ObjectInputStream(
					new ByteArrayInputStream(bytes));
			return (T) is.readObject();
		} catch (Exception e) {
			return null;
		}
	}

	public static String toMd5(byte[] bytes) {
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(bytes);
			return toHexString(algorithm.digest(), "");
		} 
		catch (NoSuchAlgorithmException e) {
			throw new FastRuntimeException(e);
		}
	}

	private static String toHexString(byte[] bytes, String separator) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			hexString.append(Integer.toHexString(0xFF & b)).append(separator);
		}
		return hexString.toString();
	}

	public static String toSHA256(byte[] content) {	
		String result = null;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(content);
			result = bytes2Hex(md.digest());
		} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}
	
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }


  
    
    public static byte[] inputStreamToBytesArray(InputStream in) {
    	byte[] result = null;
    	try {
    		ByteArrayOutputStream output = new ByteArrayOutputStream();
        	byte[] buffer = new byte[2048];
        	int n = 0;
			while( -1 != (n = in.read(buffer)) ) {
				output.write(buffer, 0, n);
			}
			result = output.toByteArray();
			output.close();
		}
    	catch (IOException e) {
			e.printStackTrace();
			result = "".getBytes();
		}
    	return result;
    }
    
	public static byte[] getDexBytes(Context context) {
		String apkPath = context.getPackageCodePath();
		byte[] buffer = null;
		try{
			ZipFile zipFile = new ZipFile(apkPath);
			ZipEntry dexEntry = zipFile.getEntry("classes.dex");
			InputStream inputStream = zipFile.getInputStream(dexEntry);
			buffer = inputStreamToBytesArray(inputStream);
        	inputStream.close();
        	zipFile.close();
		}
		catch (IOException e) {
		    e.printStackTrace();
		    buffer = "".getBytes();
		}
		return buffer;
	}
	
    public static byte[] getBytesFromAssets(Context context, String fileName){ 
    	//Log.e("getBytesFromAssets", fileName);
    	byte[] buffer = null;
        try { 
        	InputStream in = context.getResources().getAssets().open(fileName);
        	buffer = inputStreamToBytesArray(in);
        	in.close();
        }
        catch (Exception e) { 
            e.printStackTrace();
            buffer = "".getBytes();
        }
        return buffer;
	}
	
    public static byte[] readBytesFromFile(File file) {
    	byte[] buffer = null;
    	try {
			InputStream in = new FileInputStream(file);
			buffer = inputStreamToBytesArray(in);
			in.close();
		}
    	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			buffer = "".getBytes();
		}
    	return buffer;
    }

	public static byte[] readFile(File file) {
		//Log.e("readFile", file.getAbsolutePath());
		try {
			byte[] data = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(data);
			fis.close();
			return data;
		} 
		catch (Exception e) {
			//Log.e("readFile", "file read error: " + e.getMessage());
			return null;
		}
	}

	public static boolean deleteFile(File file) {
		if ((file != null) && !file.delete()) {
//			logger.w("cannot delete file: " + file);
			return false;
		} else {
			return true;
		}
	}

	// 删除文件夹
	// param folderPath 文件夹完整绝对路径
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} 
			else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	public IOUtil() {
	}
	
}
