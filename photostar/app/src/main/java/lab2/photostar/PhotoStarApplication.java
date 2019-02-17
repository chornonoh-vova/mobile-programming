package lab2.photostar;

import android.app.Application;

import androidx.room.Room;
import lab2.photostar.db.PhotosDatabase;

public class PhotoStarApplication extends Application {
    private PhotosDatabase db = null;

    public PhotosDatabase getDb() {
        if (db == null) {
            db = Room.databaseBuilder(this, PhotosDatabase.class, "photos_db").build();
        }
        return db;
    }
}
