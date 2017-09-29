package id.rentist.mitrarentist.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by mdhif on 30/09/2017.
 */

public class VolleySingleton {
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context context;

    public VolleySingleton(Context context){
        this.context = context;
    }

    public ImageLoader getImageUrl(){
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        return mImageLoader;
    }
}
