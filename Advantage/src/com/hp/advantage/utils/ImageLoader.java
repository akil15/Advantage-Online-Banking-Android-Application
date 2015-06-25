package com.hp.advantage.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hp.advantage.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    final int stub_id = R.drawable.missing;
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    ExecutorService executorService;
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());

    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public void DisplayImageOriginalSize(String url, ImageView imageView) {
        DisplayImage(url, imageView, -1);
    }

    public void DisplayImageScaled(String url, ImageView imageView) {
        DisplayImage(url, imageView, 70);
    }

    public void DisplayImage(String url, ImageView imageView, int requiredSize) {
        if (url == null) {
            imageView.setImageResource(stub_id);
        } else {
            imageViews.put(imageView, url);
            Bitmap bitmap = memoryCache.get(url);
            if (bitmap != null) imageView.setImageBitmap(bitmap);
            else
                queuePhoto(url, imageView, requiredSize);
        }

    }

    private void queuePhoto(String url, ImageView imageView, int requiredSize) {
        PhotoToLoad p = new PhotoToLoad(url, imageView, requiredSize);
        executorService.submit(new PhotosLoader(p));
    }

    public Bitmap GetBitmap(String url) {
        return GetBitmap(url, -1);
    }

    public Bitmap GetBitmap(String url, int requiredSize) {
        if (url == null || url.length() == 0) {
            return null;
        }

        File f = fileCache.getFile(url);

        //from SD cache
        Bitmap b = GenericUtils.decodeFile(f, requiredSize);
        if (b != null) return b;

        //from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            CopyStream(is, os);
            os.close();
            bitmap = GenericUtils.decodeFile(f, requiredSize);
            return bitmap;
        } catch (Exception ex) {
            //ex.printStackTrace();
            return null;
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url)) return true;
        return false;
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public int requiredSize;

        public PhotoToLoad(String u, ImageView i, int r) {
            url = u;
            imageView = i;
            requiredSize = r;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) return;
            Bitmap bmp = GetBitmap(photoToLoad.url, photoToLoad.requiredSize);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad)) return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) return;
            if (bitmap != null) photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
            photoToLoad.imageView.invalidate();
        }
    }

}
