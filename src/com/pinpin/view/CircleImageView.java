package com.pinpin.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {
	 
    Path path;
    public PaintFlagsDrawFilter mPaintFlagsDrawFilter;// 毛边过滤
    Paint paint;
     
    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
       init();
    }
 
    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }
 
    public CircleImageView(Context context) {
        super(context);
    
        // TODO Auto-generated constructor stub
      init();
    }
    public boolean  createCircle(Bitmap bitmap){
    	boolean result = false;
    if(bitmap==null) return result;
    int min = Math.min(bitmap.getWidth(),bitmap.getHeight());
    Bitmap circleBitmap = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
    BitmapShader shader = new BitmapShader (bitmap,  TileMode.CLAMP, TileMode.CLAMP);
	paint.setShader(shader);
    Canvas c = new Canvas(circleBitmap);
    c.setDrawFilter(mPaintFlagsDrawFilter);
    c.drawCircle(min/2,min/2, min/2, paint);
    if(circleBitmap!=null){
    	 setImageBitmap(circleBitmap);
    	   result = true;
    }
    return result;
  
    }
    public Bitmap creategrayCircle(Bitmap bitmap){
    	 if(bitmap==null) return null;
    	 int min = Math.min(bitmap.getWidth(),bitmap.getHeight());
    	    Bitmap faceIconGreyBitmap = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
    	    Canvas canvas = new Canvas(faceIconGreyBitmap);
    	    Paint paint = new Paint();
    	    ColorMatrix colorMatrix = new ColorMatrix();
    	    colorMatrix.setSaturation(0);
    	    ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
    	      colorMatrix);
    	    paint.setColorFilter(colorMatrixFilter);
    	    canvas.drawBitmap(bitmap, 0, 0, paint);
    	    if(faceIconGreyBitmap!=null){
    	    	 setImageBitmap(faceIconGreyBitmap);
    	    }
    	    return faceIconGreyBitmap;
    	
    }
     public void init(){
    	
    	 
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0,
                Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
       // paint.setColor(Color.WHITE);
         
    } 
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
	/**
	 * 高斯模糊
	 * 
	 * @param bmp
	 * @return
	 */
	//
	public static Drawable BoxBlurFilter(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		float hRadius = 10;
		float vRadius = 10;
		int iterations = 7;
		for (int i = 0; i < iterations; i++) {
			blur(inPixels, outPixels, width, height, hRadius);
			blur(outPixels, inPixels, height, width, vRadius);
		}
		blurFractional(inPixels, outPixels, width, height, hRadius);
		blurFractional(outPixels, inPixels, height, width, vRadius);
		bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	public static void blur(int[] in, int[] out, int width, int height,
			float radius) {
		int widthMinus1 = width - 1;
		int r = (int) radius;
		int tableSize = 2 * r + 1;
		int divide[] = new int[256 * tableSize];

		for (int i = 0; i < 256 * tableSize; i++)
			divide[i] = i / tableSize;

		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;
			int ta = 0, tr = 0, tg = 0, tb = 0;

			for (int i = -r; i <= r; i++) {
				int rgb = in[inIndex + clamp(i, 0, width - 1)];
				ta += (rgb >> 24) & 0xff;
				tr += (rgb >> 16) & 0xff;
				tg += (rgb >> 8) & 0xff;
				tb += rgb & 0xff;
			}

			for (int x = 0; x < width; x++) {
				out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
						| (divide[tg] << 8) | divide[tb];

				int i1 = x + r + 1;
				if (i1 > widthMinus1)
					i1 = widthMinus1;
				int i2 = x - r;
				if (i2 < 0)
					i2 = 0;
				int rgb1 = in[inIndex + i1];
				int rgb2 = in[inIndex + i2];

				ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
				tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
				tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
				tb += (rgb1 & 0xff) - (rgb2 & 0xff);
				outIndex += height;
			}
			inIndex += width;
		}
	}

	public static void blurFractional(int[] in, int[] out, int width,
			int height, float radius) {
		radius -= (int) radius;
		float f = 1.0f / (1 + 2 * radius);
		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;

			out[outIndex] = in[0];
			outIndex += height;
			for (int x = 1; x < width - 1; x++) {
				int i = inIndex + x;
				int rgb1 = in[i - 1];
				int rgb2 = in[i];
				int rgb3 = in[i + 1];

				int a1 = (rgb1 >> 24) & 0xff;
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = rgb1 & 0xff;
				int a2 = (rgb2 >> 24) & 0xff;
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = rgb2 & 0xff;
				int a3 = (rgb3 >> 24) & 0xff;
				int r3 = (rgb3 >> 16) & 0xff;
				int g3 = (rgb3 >> 8) & 0xff;
				int b3 = rgb3 & 0xff;
				a1 = a2 + (int) ((a1 + a3) * radius);
				r1 = r2 + (int) ((r1 + r3) * radius);
				g1 = g2 + (int) ((g1 + g3) * radius);
				b1 = b2 + (int) ((b1 + b3) * radius);
				a1 *= f;
				r1 *= f;
				g1 *= f;
				b1 *= f;
				out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
				outIndex += height;
			}
			out[outIndex] = in[width - 1];
			inIndex += width;
		}
	}

	public static int clamp(int x, int a, int b) {
		return (x < a) ? a : (x > b) ? b : x;
	}

    /*@Override
    protected void onDraw(Canvas cns) {

    	if( cns.isHardwareAccelerated()&&Build.VERSION.SDK_INT<=15){
    		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    	} 
        // TODO Auto-generated method stub
        float h = getMeasuredHeight()- 3.0f;
        float w = getMeasuredWidth()- 3.0f;
        if (path == null) {
            path = new Path();
            path.addCircle(
                    w/2.0f
                    , h/2.0f
                    , (float) Math.min(w/2.0f, (h / 2.0))
                    , Path.Direction.CCW);
            path.close();
        }
        cns.drawCircle(w/2.0f, h/2.0f,  Math.min(w/2.0f, h / 2.0f) + 1.5f, paint);
         int saveCount = cns.getSaveCount();
        cns.save();
        cns.setDrawFilter(mPaintFlagsDrawFilter);
         try{
        	  cns.clipPath(path,Region.Op.REPLACE);
         }catch (Exception e) {
        	 e.printStackTrace();
         }
        
        cns.setDrawFilter(mPaintFlagsDrawFilter);
        cns.drawColor(Color.WHITE);
        super.onDraw(cns);
        cns.restoreToCount(saveCount);
    }*/
     
}