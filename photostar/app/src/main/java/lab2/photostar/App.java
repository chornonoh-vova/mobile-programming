package lab2.photostar;

import android.app.Application;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.room.Room;
import lab2.photostar.db.PhotosDatabase;

public class App extends Application {
    private static App instance;

    private PhotosDatabase db = null;

    public static App get() {
        return instance;
    }

    public PhotosDatabase getDb() {
        return db;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize database
        db = Room.databaseBuilder(this, PhotosDatabase.class, "photos_db").build();

        instance = this;
    }

    public static void refreshGallery() {
        MediaScannerConnection.scanFile(instance, new String[] { Environment.getExternalStorageDirectory().toString() }, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri)
            {
                Log.i("ExternalStorage", "Scanned " + path + ":");
                Log.i("ExternalStorage", "-> uri=" + uri);
            }
        });
    }
}
