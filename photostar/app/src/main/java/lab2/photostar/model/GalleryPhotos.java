package lab2.photostar.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GalleryPhotos {
    private Context context;

    public GalleryPhotos(Context context) {
        this.context = context;
    }
    public List<String> getAllFolders() {
        List<String> photos = getAllPhotos();
        List<String> folders = new ArrayList<>();

        // loop to convert photos list to folders list
        for (String item: photos) {
            // photo folders getting
            String tmp = item.substring(0, item.lastIndexOf('/'));

            if (!folders.contains(tmp)) {
                folders.add(tmp);
            }
        }
        return folders;
    }
    public List<String> getAllPhotos() {
        List<String> photos = new ArrayList<>();

        // get all external content images
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // define columns
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DATE_MODIFIED };

        String sortOrder = MediaStore.MediaColumns.DATE_ADDED + " DESC";

        // cursor to get all images (like from database)
        // using try-with-resources to auto-close
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, sortOrder)) {

            int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            while (cursor.moveToNext()) {
                photos.add(cursor.getString(columnIndexData));
            }
        }

        return photos;
    }

    public int getThumbId(String photoUrl) {
        int returnId = 0;
        // get all external content images
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // define columns
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media._ID
        };

        String selection = MediaStore.MediaColumns.DATA + " = ?";

        String[] selectionArgs = { photoUrl };

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                MediaStore.Images.Media.DEFAULT_SORT_ORDER)) {
            int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            while (cursor.moveToNext()) {
                returnId = cursor.getInt(columnIndexData);
            }
        }

        return returnId;
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
