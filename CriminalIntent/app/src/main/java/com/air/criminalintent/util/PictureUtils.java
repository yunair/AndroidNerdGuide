package com.air.criminalintent.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

/**
 * Created by Air on 15/7/21.
 */
public class PictureUtils {
    private static final String TAG = "PictureUtils";
    private PictureUtils() {}

    /**
     * Get a BitmapDrawable from a local file that is scaled down
     * to fit the current Window size.
     */
    @SuppressWarnings("deprecation")
    public static BitmapDrawable getScaledDrawable(Activity activity, String path) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        final int displayHeight = display.getHeight();
        final int displayWidth = display.getWidth();

        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        final int imageHeight = options.outHeight;
        final int imageWidth = options.outWidth;

        int inSampleSize = 1;
        /*if (imageHeight > displayHeight || imageWidth > displayWidth) {
            if (imageWidth > imageHeight) {
                inSampleSize = Math.round(imageHeight / displayHeight);
            } else {
                inSampleSize = Math.round(imageWidth / displayWidth);
            }
        }*/
        if (imageHeight > displayHeight || imageWidth > displayWidth) {

            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > displayHeight
                    && (halfWidth / inSampleSize) > displayWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(TAG, "Sample Size : " + inSampleSize);
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return new BitmapDrawable(activity.getResources(), bitmap);
    }

    public static void cleanImageView(ImageView imageView){
        if(!(imageView.getDrawable() instanceof BitmapDrawable)) return;
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        // Clean up the view's image for the sake of memory
        Bitmap bitmap = drawable.getBitmap();
        if(bitmap != null) {
            bitmap.recycle();
            imageView.setImageDrawable(null);
        }
    }
}
