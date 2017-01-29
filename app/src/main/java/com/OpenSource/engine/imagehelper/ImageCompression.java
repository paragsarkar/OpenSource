package com.OpenSource.engine.imagehelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;


import com.OpenSource.engine.application.AppInstance;
import com.OpenSource.engine.utils.Utils;
import com.bumptech.glide.Glide;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by parag sarkar on 12/9/16.
 */

@EBean
public class ImageCompression {


    static final String TEMP_FOLDER = "tmp";
    static final String TMP_BMP_IMG_FILE_NAME = "tmpBmpImageFile";

    @App
    AppInstance appInstance;
    @Bean
    Utils utils;


    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;

    public void compressImage(Uri src, Uri output) {
        compressImage(new File(src.getPath()), new File(output.getPath()));
    }

    public void compressImage(Bitmap bitmap, Uri output) {
        File tmp = bitmapToTempCacheFile(bitmap);
        compressImage(tmp, new File(output.getPath()));
        tmp.delete();
    }

    public void compressImage(Bitmap bitmap, File output) {
        File tmp = bitmapToTempCacheFile(bitmap);
        // compressImage(tmp, output);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(output);
            //write the compressed bitmap at the destination specified by filename.
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        tmp.delete();
    }

    public interface BitmapCompression {
        void onBitmapReady(Bitmap bitmap);

    }

    //call on background thread
    public void compressImageWithGlide(Uri src, Uri output, int width, int height, int quality) {
        try {
            Bitmap scaledBitmap = Glide.with(appInstance)
                    .load(src)
                    .asBitmap()
                    .centerCrop()
                    .into(width, height)
                    .get();
            FileOutputStream out = null;
            out = new FileOutputStream(new File(output.getPath()));
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //.into(612, 816)
    public void compressImageWithGlide(Bitmap bitmap, File output, int width, int height) {
        File tmp = bitmapToTempCacheFile(bitmap);
        try {
            Bitmap scaledBitmap = Glide.with(appInstance)
                    .load(tmp)
                    .asBitmap()
                    .centerCrop()
                    .into(width, height)
                    .get();
            FileOutputStream out = null;
            out = new FileOutputStream(output);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            tmp.delete();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void compressImage(File src, File output) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bmp = BitmapFactory.decodeFile(src.getPath(), options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(src.getPath(), options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        if (bmp != null) {
            bmp.recycle();
        }

        ExifInterface exif;
        try {
            exif = new ExifInterface(src.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(output);
            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    @NonNull
    private File getTempCacheDirectoryFile(String fileName) {
        File sharableCacheDir = new File(appInstance.getCacheDir(), TEMP_FOLDER);
        if (!sharableCacheDir.exists()) {
            sharableCacheDir.mkdirs();
        }
        return new File(sharableCacheDir, fileName);
    }

    public File bitmapToTempCacheFile(Bitmap bitmap) {
        File tempCacheFile = null;
        try {
            tempCacheFile = getTempCacheDirectoryFile(File.createTempFile(TMP_BMP_IMG_FILE_NAME, ".jpg").getName());
            if (tempCacheFile.exists()) {
                tempCacheFile.delete();
                tempCacheFile = getTempCacheDirectoryFile(File.createTempFile(TMP_BMP_IMG_FILE_NAME, ".jpg").getName());
            }
            FileOutputStream fileOutputStream = null;
            fileOutputStream = FileUtils.openOutputStream(tempCacheFile);
            fileOutputStream.write(utils.bitmap2ByteArray(bitmap));
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempCacheFile;
    }
}
