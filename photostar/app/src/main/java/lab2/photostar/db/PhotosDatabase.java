package lab2.photostar.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import lab2.photostar.dao.PhotoDao;
import lab2.photostar.model.Photo;

@Database(entities = {Photo.class}, version = 1)
public abstract class PhotosDatabase extends RoomDatabase {
    public abstract PhotoDao photoDao();
}
