package lab2.photostar;

import android.app.Application;

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
}
