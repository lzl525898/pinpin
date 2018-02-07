/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pinpin.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;

public class ImageUtils {
//	public static String getThumbnailImagePath(String imagePath) {
//		String path = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
//		path += "th" + imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
//		EMLog.d("msg", "original image path:" + imagePath);
//		EMLog.d("msg", "thum image path:" + path);
//		return path;
//	}
	private Bitmap createCircleImage(Bitmap source, int min) { 
		final Paint paint = new Paint(); 
		paint.setAntiAlias(true); 
		// 注意一定要用ARGB_8888，否则因为背景不透明导致遮罩失败 
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888); 
		// 产生一个同样大小的画布 
		Canvas canvas = new Canvas(target); 
		// 首先绘制圆形 
		canvas.drawCircle(min / 2, min / 2, min / 2, paint); 
		// 使用SRC_IN 
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)); 
		// 绘制图片 
		canvas.drawBitmap(source, 0, 0, paint); 
		return target; 
		}
	public static String getImagePath(String remoteUrl)
	{
		String imageName= remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ imageName;
        EMLog.d("msg", "image path:" + path);
        return path;
		
	}
	
	
	public static String getThumbnailImagePath(String thumbRemoteUrl) {
		String thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ "th"+thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }
	public static Bitmap getBitmapPath(String path, int w, int h) {
		
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int)scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }
	public static Bitmap getBitmapFromFile(File dst, int width, int height) {
	    if (null != dst && dst.exists()) {
	        BitmapFactory.Options opts = null;
	        if (width > 0 && height > 0) {
	            opts = new BitmapFactory.Options();
	            opts.inJustDecodeBounds = true;
	            BitmapFactory.decodeFile(dst.getPath(), opts);
	            // 计算图片缩放比例
	            final int minSideLength = Math.min(width, height);
	            opts.inSampleSize = computeSampleSize(opts, minSideLength,
	                    width * height);
	            opts.inJustDecodeBounds = false;
	            opts.inInputShareable = true;
	            opts.inPurgeable = true;
	        }
	        try {
	        	Bitmap bmp = BitmapFactory.decodeFile(dst.getPath(), opts);
	        	int digree = getOrientationByPath(dst.getAbsolutePath());
	    		if ( digree != 0 ) {
	    			Matrix m = new Matrix();  
	                m.postRotate(digree);  
	                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),  
	                		bmp.getHeight(), m, true); 
	    		}
	    		
	        	
	            return bmp;
	        } catch (OutOfMemoryError e) {
	            e.printStackTrace();
	        }
	    }
	    return null;
	} 
	private static int getOrientationByPath(String path) {
		//Log.e(TAG, "getOrientationByPath");
		
		int digree = 0;  
        ExifInterface exif = null;  
        try {  
            exif = new ExifInterface(path);  
        }
        catch (IOException e) {  
            e.printStackTrace();  
            exif = null;  
        }  
        if (exif != null) {
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,  
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (ori) {  
            case ExifInterface.ORIENTATION_ROTATE_90:  
                digree = 90;  
                break;  
            case ExifInterface.ORIENTATION_ROTATE_180:  
                digree = 180;  
                break;  
            case ExifInterface.ORIENTATION_ROTATE_270:  
                digree = 270;
                break;
            default:  
                digree = 0;  
                break;  
            }  
        }
        
        return digree;
	}
	
	public static String getCompressImageFile(File source, int width, int height) throws FileNotFoundException {
		Bitmap bmp = getBitmapFromFile(source,width,height);
		FileOutputStream stream = new FileOutputStream(source);
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		return source.getAbsolutePath();
	}
	

	public static int computeSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength,
	            maxNumOfPixels);

	    int roundedSize;
	    if (initialSize <= 8) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }

	    return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;

	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
	            .sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
	            .floor(w / minSideLength), Math.floor(h / minSideLength));

	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }

	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}
	
	
	public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
	  {
	   if(null == bitmap)
	   {
	    return  null;
	   }
	   Bitmap result = bitmap;
	   int widthOrg = bitmap.getWidth();
	   int heightOrg = bitmap.getHeight();
	   if(edgeLength <= 0)
	   {
		   edgeLength = Math.min(widthOrg, heightOrg);
	   }
	                                                                                 
	  
	                                                                                 
	   if(edgeLength > Math.max(widthOrg, heightOrg) )
	   {
		   edgeLength = Math.min(widthOrg, heightOrg);
	   }
	    //压缩到一个最小长度是edgeLength的bitmap
	    int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
	    int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
	    int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
	    Bitmap scaledBitmap;
	                                                                                  
	          try{
	           scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
	          }
	          catch(Exception e){
	           return null;
	          }
	                                                                                       
	       //从图中截取正中间的正方形部分。
	       int xTopLeft = (scaledWidth - edgeLength) / 2;
	       int yTopLeft = (scaledHeight - edgeLength) / 2;
	                                                                                     
	       try{
	        result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
	        if(scaledBitmap!=null&&!scaledBitmap.isRecycled()){
	        	 scaledBitmap.recycle();
	        	 scaledBitmap = null;
	        }
	       
	       }
	       catch(Exception e){
	        return null;
	       }       
	  
	                                                                                      
	   return result;
	  }
	
	public  static void  startCropImage(Activity ctx,Uri uri,Uri target, int outputX, int outputY, int requestCode){
	 
		    Intent intent = new Intent("com.android.camera.action.CROP");
		 
		    intent.setDataAndType(uri, "image/*");
		 
		    intent.putExtra("crop", "true");
		 
		    intent.putExtra("aspectX", 1);
		 
		    intent.putExtra("aspectY", 1);
		 
		    intent.putExtra("outputX", outputX);
	 
		    intent.putExtra("outputY", outputY);
		 
		    intent.putExtra("scale", true);
	 
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, target);
		 
		    intent.putExtra("return-data", false);
	 
		    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		  
		    intent.putExtra("noFaceDetection", true); // no face detection
		 
		    ctx.startActivityForResult(intent, requestCode);
	 
		}
	
}
