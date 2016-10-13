package com.beliautopart.beliautopart.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Brands on 31/08/2016.
 */
public class MyCacheNetworkImage implements com.android.volley.toolbox.ImageLoader.ImageCache {

    @Override
    public Bitmap getBitmap(String path) {
        if (path.contains("file://")) {
            return BitmapFactory.decodeFile(path);
        } else {
            // Here you can add an actual cache
            return null;
        }
    }
    @Override
    public void putBitmap(String key, Bitmap bitmap) {
        // Here you can add an actual cache
    }
}
