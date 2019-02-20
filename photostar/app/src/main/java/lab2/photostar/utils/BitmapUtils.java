package lab2.photostar.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public final class BitmapUtils {
    public static Bitmap centerCrop(Bitmap src) {
        int height = src.getHeight();
        int width = src.getWidth();

        if (width >= height) {
            return Bitmap.createBitmap(src, width / 2 - height / 2, 0, height, height);
        } else {
            return Bitmap.createBitmap(src, 0, height / 2 - width / 2, width, width);
        }
    }

    public static Bitmap getBitmapFromGallery(String path, int width, int height) {
        final BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inSampleSize = calculateInSampleSize(opts, width, height);

        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, opts);
    }

    private static int calculateInSampleSize(BitmapFactory.Options opts, int width, int height) {
        final int rawHeight = opts.outHeight;
        final int rawWidth = opts.outWidth;

        int inSampleSize = 1;

        if (rawHeight > height || rawWidth > width) {
            final int halfHeight = rawHeight / 2;
            final int halfWidth = rawWidth / 2;

            while ((halfHeight / inSampleSize) >= height && (halfWidth / inSampleSize) >= width) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String insertPhoto(ContentResolver cr, Bitmap src, String title, String desc) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, desc);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        long currentTime = System.currentTimeMillis();
        values.put(MediaStore.Images.Media.DATE_ADDED, currentTime);
        values.put(MediaStore.Images.Media.DATE_TAKEN, currentTime);

        Uri uri;

        uri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try (OutputStream out = cr.openOutputStream(uri)) {
                src.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (IOException e) {
                cr.delete(uri, null, null);
            }
        }

        return uri != null ? uri.toString() : null;
    }
}
