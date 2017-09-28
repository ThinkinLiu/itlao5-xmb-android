package com.e7yoo.e7.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017/9/28.
 */

public class ViewUtil {

    public static String saveViewCapture(Context context, View view) {
        Bitmap bmp = null;
        try {
            bmp = capture(view, view.getWidth(), view.getHeight(), false, Bitmap.Config.RGB_565);
            return screenshot(context, bmp, String.valueOf(System.currentTimeMillis()), Bitmap.CompressFormat.JPEG);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            if(bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
                bmp = null;
            }
        }
    }

    /**
     * @param view, the View you want to screenshot, such as WebView, etc.
     * @param width, screenshot's width.
     * @param height, screenshot's height.
     * @param scroll, true if you want to start capture from currently scroll position.
     * @param config, the Bitmap.Config, such as ARGB_8888, etc.
     *
     * @return the screenshot's Bitmap
     */
    public static Bitmap capture(View view, float width, float height, boolean scroll, Bitmap.Config config) {
        if (!view.isDrawingCacheEnabled()) {
            view.setDrawingCacheEnabled(true);
        }
        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, config);
        bitmap.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(bitmap);
        int left = view.getLeft();
        int top = view.getTop();
        if (scroll) {
            left = view.getScrollX();
            top = view.getScrollY();
        }
        int status = canvas.save();
        canvas.translate(-left, -top);
        float scale = width / view.getWidth();
        canvas.scale(scale, scale, left, top);
        view.draw(canvas);
        canvas.restoreToCount(status);
        Paint alphaPaint = new Paint();
        alphaPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(0f, 0f, 1f, height, alphaPaint);
        canvas.drawRect(width - 1f, 0f, width, height, alphaPaint);
        canvas.drawRect(0f, 0f, width, 1f, alphaPaint);
        canvas.drawRect(0f, height - 1f, width, height, alphaPaint);
        canvas.setBitmap(null);
        return bitmap;
    }
    public static String screenshot(Context context, Bitmap bitmap, String name, Bitmap.CompressFormat compressFormat) {
        if (bitmap == null) {
            return null;
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (name == null || name.trim().isEmpty()) {
            name = String.valueOf(System.currentTimeMillis());
        }
        name = name.trim();

        int count = 0;
        String type = ".jpg";
        switch (compressFormat) {
            case JPEG:
                type = ".jpg";
                break;
            case WEBP:
                type = ".webp";
                break;
            case PNG:
                type = ".png";
                break;
        }
        File file = new File(dir, name + type);
        while (file.exists()) {
            count++;
            file = new File(dir, name + "_" + count + type);
        }

        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(compressFormat, 100, stream);
            stream.flush();
            stream.close();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }
}
