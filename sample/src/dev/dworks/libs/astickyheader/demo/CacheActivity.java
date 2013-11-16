package dev.dworks.libs.astickyheader.demo;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import dev.dworks.libs.astickyheader.R;

public class CacheActivity extends SherlockFragmentActivity implements OnScrollListener {

	private Bitmap mPlaceHolderBitmap;
	private LruCache<String, Bitmap> mMemoryCache;
    protected boolean mPauseWork = false;
    private final Object mPauseWorkLock = new Object();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initCache();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setPauseWork(true);
	}
	
    @Override
    public void onPause() {
        super.onPause();
        setPauseWork(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMemoryCache.evictAll();
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initCache() {
		mPlaceHolderBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.empty_photo);
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    final int cacheSize = maxMemory / 2;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @SuppressLint("NewApi") @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            if (Build.VERSION.SDK_INT >= 12) {
	                return bitmap.getByteCount()/ 1024;
	            }
	            else{
		            return (bitmap.getRowBytes() * bitmap.getHeight())/ 1024;
	            }
	        }
	    };
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
	
	public void loadBitmap(int resId, ImageView imageView) {
	    final String imageKey = String.valueOf(resId);

	    final Bitmap bitmap = getBitmapFromMemCache(imageKey);
	    if (bitmap != null) {
	    	imageView.setImageBitmap(bitmap);
	    } else {
		    if (cancelPotentialWork(resId, imageView)) {
		        final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
		        final AsyncDrawable asyncDrawable =
		                new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
		        imageView.setImageDrawable(asyncDrawable);
		        task.execute(resId);
		    }
	    }
	}
	
	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private int data = 0;

	    public BitmapWorkerTask(ImageView imageView) {
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    @Override
	    protected Bitmap doInBackground(Integer... params) {
	        data = params[0];
	        final Bitmap bitmap =  BitmapFactory.decodeResource(getResources(), data);
	        addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
	        return bitmap;
	    }

	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (isCancelled()) {
	            bitmap = null;
	        }

	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            final BitmapWorkerTask bitmapWorkerTask =  getBitmapWorkerTask(imageView);
	            if (this == bitmapWorkerTask && imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }
	}

	public static boolean cancelPotentialWork(int data, ImageView imageView) {
	    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

	    if (bitmapWorkerTask != null) {
	        final int bitmapData = bitmapWorkerTask.data;
	        if (bitmapData != data) {
	            bitmapWorkerTask.cancel(true);
	        } else {
	            return false;
	        }
	    }
	    return true;
	}
	
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}
	public static class AsyncDrawable extends BitmapDrawable {
	    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

	    public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
	        super(res, bitmap);
	        bitmapWorkerTaskReference =
	            new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
	    }

	    public BitmapWorkerTask getBitmapWorkerTask() {
	        return bitmapWorkerTaskReference.get();
	    }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Pause fetcher to ensure smoother scrolling when flinging
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            // Before Honeycomb pause image loading on scroll to help with performance
            if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
                setPauseWork(true);
            }
        } else {
        	setPauseWork(false);
        }
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
	}
	
    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }
}