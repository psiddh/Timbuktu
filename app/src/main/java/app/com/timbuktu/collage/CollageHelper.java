package app.com.timbuktu.collage;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import app.com.timbuktu.MediaItem;
import app.com.timbuktu.SyncCache;
import app.com.timbuktu.collections.Collection;

public  class CollageHelper {

    public static Bitmap doCollage(Collection collection) {
        ConcurrentHashMap<Integer, MediaItem> cache = SyncCache.getInstance().getMap();

        String [] paths = new String[collection.size()];
        int index = 0;

        for (Integer id : collection.get()) {
            if (cache.containsKey(id)) {
                MediaItem item = cache.get(id);
                if (item != null) {
                    paths[index++] = item.getPath();
                }
            }
        }
        return CollageHelper.doCollageInternal(paths,500, 100, 100, 3);
    }

    public static Bitmap doCollageInternal(String[] images, int totalWidth, int imgWidth, int imgHeight,
                                   int maxRows) {
        // STEP 1: Calculate Minimum width of each collage item
        int preferredNumOfItemsWidthWise = (int) Math.floor (totalWidth / imgWidth);
        int remainderWidth = (totalWidth % (imgWidth * preferredNumOfItemsWidthWise));
        int widthAdjustmentPerCollageItem = (int) Math.floor (remainderWidth / preferredNumOfItemsWidthWise);
        int calculatedCollageItemWidth = imgWidth + widthAdjustmentPerCollageItem;
        // STEP 2: Calculate the number of rows
        int totalNumberOfImages = images.length;
        int numberOfRows = (int) Math.min(Math.floor(totalNumberOfImages / preferredNumOfItemsWidthWise), maxRows);
        if (numberOfRows == 0) numberOfRows = 1;
        int totalHeight = imgHeight * numberOfRows;
        int minAllowedItems = Math.min((numberOfRows * preferredNumOfItemsWidthWise), totalNumberOfImages);
        // STEP 3: Load Images
        List<Bitmap> bitmaps = new ArrayList<>(images.length);

        for (int i = 0; i < minAllowedItems; i++) {
            try {
                String path = images[i];

                ExifInterface exifInterface = new ExifInterface(path);
                Bitmap bitmap;

                if (exifInterface.hasThumbnail()) {
                    byte[] thumbnail = exifInterface.getThumbnail();

                    bitmap = decodeSampledBitmapFromThumbnail(thumbnail, 100, 100);
                } else {
                    bitmap = decodeSampledBitmapFromPathName(path, 100, 100);
                }

                bitmaps.add(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return getCollagedBitmap(bitmaps, totalHeight, totalWidth, minAllowedItems, imgHeight,
                calculatedCollageItemWidth, preferredNumOfItemsWidthWise);
    }

    private static Bitmap getCollagedBitmap(List<Bitmap> bmpList, int imgHeight, int imgWidth,
                                            int minAllowedItems, int minCollageItemHeight, int minCollageItemWidth,
                                            int preferredNumOfItemsWidthWise) {
        Bitmap drawnBitmap = null;
        int left = 0;
        int top = 0;
        int right = minCollageItemWidth;
        int bottom = minCollageItemHeight;
        Bitmap b = null;

        try {
            drawnBitmap = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(drawnBitmap);
            //int left = 0;
            //int top = 0;
            Iterator<Bitmap> bmpIter = bmpList.iterator();
            int size = bmpList.size();

            for (int counter = 0; counter < size && bmpIter.hasNext(); counter++) {
                b = bmpIter.next();
                if (b != null && !b.isRecycled()) {
                    //canvas.drawBitmap(b, left, top, null);
                    canvas.drawBitmap(b, null, new Rect(left, top, right, bottom), null);
                } else {
                    continue;
                }
                if ((left + minCollageItemWidth) <= imgWidth) {
                    left += minCollageItemWidth;
                    right += minCollageItemWidth;
                } else {
                    left = 0;
                    right = minCollageItemWidth;
                    top += minCollageItemHeight;
                    bottom += minCollageItemHeight;
                }
            }

            // Is this a hack?
            //if (bmpList.size() % preferredNumOfItemsWidthWise == 0) {
            if (b != null && !b.isRecycled())
                canvas.drawBitmap(b, null, new Rect(left, top, right, bottom), null);
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }

        return drawnBitmap;
    }

    public static Bitmap decodeSampledBitmapFromThumbnail(byte[] thumbnail, int reqWidth,
                                                          int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight) {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfWidth / inSampleSize > reqWidth
                    && halfHeight / inSampleSize > reqHeight) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromPathName(String pathName, int reqWidth,
                                                         int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        int rotate = getImageOrientation(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);

        if (bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.preRotate(rotate);

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, false);
        }

        return bitmap;
    }


    public static int getImageOrientation(String path, BitmapFactory.Options options) {
        int rotate = 0;

        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            if (rotate == 90 || rotate == 270) {
                int width = options.outWidth;
                options.outWidth = options.outHeight;
                options.outHeight = width;
            }
        } catch (IOException e) {
            Log.d("Collage", "", e);
        }

        return rotate;
    }
}
