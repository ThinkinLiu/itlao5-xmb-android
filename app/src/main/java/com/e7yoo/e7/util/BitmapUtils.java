package com.e7yoo.e7.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.View;

import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class BitmapUtils {
    /**
     * get the resolution of the device
     * 
     * @param context
     * @return return the resolution of the device (height x width)
     */
    public static String getResolution(Context context) {
        DisplayMetrics localDisplayMetrics = context.getResources().getDisplayMetrics();
        return localDisplayMetrics.heightPixels + "x" + localDisplayMetrics.widthPixels;
    }

    /**
     * get the dpi and the orientation of the device
     * 
     * @param context
     * @return the dpi and the orientation of the device（LDPI,MDPI,HDPI）
     */
    public static String getScreenDpiOrientation(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Configuration configuration = context.getResources().getConfiguration();
        String density;
        StringBuilder sb = new StringBuilder();

        if (displayMetrics.density < 1.0F) {
            density = "LDPI";
        } else if (displayMetrics.density >= 1.5F) {
            density = "HDPI";
        } else {
            density = "MDPI";
        }
        sb.append(density);
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sb.append("_L");
        } else {
            sb.append("_P");
        }
        return sb.toString();
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     * 
     * @param dp
     *            A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context
     *            Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

   
    
    
    /**
     * This method converts device specific pixels to density independent pixels.
     * 
     * @param px
     *            A value in px (pixels) unit. Which we need to convert into db
     * @param context
     *            Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(Context context, float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        
        //120 160 240 320    metrics.densityDpi
        float dp = px / (metrics.densityDpi / 160f);
        
        System.out.println("pxToDp densityDpi 密度  ++   " + metrics.densityDpi + "  dp值 ++  " + dp);
        
        if(dp >= 85 || dp <= 60){
        	dp = 80;
        }
        return dp;
    }

    
    
    /**
     * Decode an input stream into a bitmap. If the input stream is null, or cannot be used to decode a bitmap, the function returns null. The stream's position
     * will be where ever it was after the encoded data was read.
     * 
     * 
     * @param inputStream
     *            the inputstream to decode
     * @param density
     *            The density to use for the bitmap
     * @return The decoded bitmap, or null if the image data could not be decoded
     */
    public static Bitmap decodeStream(InputStream inputStream, float density) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDensity = (int) (160.0F * density);
        option.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(inputStream, null, option);
    }

    /**
     * Decode a bitmap resource into a bitmap. If the specified resource id is not a bitmap, or cannot be decoded into a bitmap, the function returns null.
     * 
     * @param context
     *            context
     * @param resId
     *            the bitmap resource id to decode
     * @param density
     *            density The density to use for the bitmap
     * @return The decoded bitmap, or null if the image data could not be decoded
     */
    public static Bitmap decodeResource(Context context, int resId, float density) {
        InputStream is = context.getResources().openRawResource(resId);
        return decodeStream(is, density);
    }

    /**
     * Decode a bitmap resource into a bitmap. If the specified resource id is not a bitmap, or cannot be decoded into a bitmap, the function returns null.
     * 
     * @param context
     *            context
     * @param resId
     *            the bitmap resource id to decode
     * @return The decoded bitmap, or null if the image data could not be decoded
     */
    public static Bitmap decodeResource(Context context, int resId) {
        InputStream is = context.getResources().openRawResource(resId);
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return decodeStream(is, metrics.density);
    }

    /**
     * Decode a file path into a bitmap. If the specified file name is null, or cannot be decoded into a bitmap, the function returns null.
     * 
     * @param pathName
     *            the file path to decode
     * @param density
     *            density The density to use for the bitmap
     * @return The decoded bitmap, or null if the image data could not be decoded
     */
    public static Bitmap decodeFile(String pathName, float density) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDensity = (int) (160.0F * density);
        return BitmapFactory.decodeFile(pathName, option);
    }

    /**
     * Decode an input stream into a bitmap. If the input stream is null, or cannot be used to decode a bitmap, the function returns null. The stream's position
     * will be where ever it was after the encoded data was read.
     * 
     * @param inputStream
     *            the inputstream to decode
     * @return The decoded bitmap, or null if the image data could not be decoded
     */
    public static Bitmap decodeStream(InputStream inputStream) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDensity = 160;
        option.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(inputStream, null, option);
    }

    public static Bitmap takeScreenShot(Activity activity) {
    	Bitmap b = null;
    	try {
            // View是你需要截图的View     
            View view = activity.getWindow().getDecorView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap b1 = view.getDrawingCache();
            // 获取状态栏高度     
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            // 获取屏幕长和高     
            int width = b1.getWidth();
            int height = b1.getHeight();
            // 去掉标题栏     
            // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);     
            b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                    - statusBarHeight);
            view.destroyDrawingCache();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return b;
    }
    
    public static void takeScreenShot(Activity activity,String text) {
        try {
            if (getAvailableSDcard(activity)) {
                // View是你需要截图的View     
                View view = activity.getWindow().getDecorView();
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                Bitmap b1 = view.getDrawingCache();
                // 获取状态栏高度     
                Rect frame = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int statusBarHeight = frame.top;
                // 获取屏幕长和高     
                int width = b1.getWidth();
                int height = b1.getHeight();
                // 去掉标题栏     
                // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);     
                Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                        - statusBarHeight);
                view.destroyDrawingCache();
                String cacheDir = Environment.getExternalStorageDirectory().getPath()
                        + "/Android/data/" + activity.getPackageName() + "/cache/";
                Uri uri = savePic(b, cacheDir, "shareData.png");
                if (uri != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(
                            Intent.EXTRA_TEXT,text);
                    Intent chooserIntent = Intent.createChooser(intent, "选择分享方式");
                    if (chooserIntent == null) {
                        return;
                    }
                    try {
                        ActivityUtil.toActivity(activity, chooserIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
            			TastyToast.makeText(activity, "没有找到可分享的应用", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }
                }
                b.recycle();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Uri savePic(Bitmap b, String filePath, String fileName) {
        File f = new File(filePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            File image = new File(filePath + File.separator + fileName);
            if (image.exists()) {
                image.delete();
            }
            fos = new FileOutputStream(image);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 85, fos);
                fos.flush();
                fos.close();
                return Uri.fromFile(image);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	/**
	 * 
	 * @param b
	 * @param filePath
	 * @param fileName
	 * @param existOpt 图片fileName存在，1:删除后保存， 2:取消保存
	 * @return 0.保存成功，1.图片已存在(existOpt=2时),2.未检测到sd卡,3.sd卡空间不足,-1.未知异常
	 */
    @SuppressWarnings("deprecation")
	public static int savePic(Bitmap b, String filePath, String fileName, int existOpt) {
    	int result = -1;
    	try {
        	boolean sdCardExist = Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState());
        	if(!sdCardExist) {
        		return 2;
        	} else {
        		File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                long sdCardSize = availableBlocks * blockSize;// 字节   
                if (sdCardSize < b.getWidth() * b.getHeight() * 4) {
                    return 3;
                }
        	}
            File f = new File(filePath);
            if (!f.exists()) {
                f.mkdirs();
            }
            FileOutputStream fos = null;
            File image = new File(filePath + File.separator + fileName);
            if (image.exists()) {
            	if(existOpt == 1) {
                    image.delete();	
            	} else {
            		return 1;
            	}
            }
            fos = new FileOutputStream(image);
            if (null != fos) {
                boolean bool = b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                if(bool) {
                    result = 0;	
                }
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    @SuppressWarnings("deprecation")
	public static boolean getAvailableSDcard(Context context) {
        boolean sdCardExist = Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()); // 判断sd卡是否存在     
        System.out.println("+++" + sdCardExist);
        if (sdCardExist) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            long sdCardSize = (availableBlocks * blockSize) / 1024;// KB值     
            if (sdCardSize > 2048) {
                return true;
            } else {
    			TastyToast.makeText(context, "SD卡空间不足", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        } else {
			TastyToast.makeText(context, "未插入SD卡或SD卡不可用", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        }
        return false;
    }
    
    @SuppressWarnings("deprecation")
	public static boolean getAvailableSDcard2(Context context) {
        boolean sdCardExist = Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()); // 判断sd卡是否存在     
       
        if (sdCardExist) {
            File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);           
            
            StatFs stat = new StatFs(path.getAbsolutePath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            long sdCardSize = (availableBlocks * blockSize) / 1024;// KB值     
            if (sdCardSize > 2048) {
                return true;
            } else {
    			TastyToast.makeText(context, "更新失败，SD卡空间不足", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        } else {
			TastyToast.makeText(context, "更新失败，未插入SD卡或SD卡不可用", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        }
        return false;
    }
}
