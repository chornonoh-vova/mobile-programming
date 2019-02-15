package lab2.photostar.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import lab2.photostar.model.Photo;

@Dao
public interface PhotoDao {
    @Query("select * from photo")
    List<Photo> getAll();

    @Query("select star_count from photo where photo_url like :photoUrl")
    int getStarsForPhoto(String photoUrl);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Photo> photos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Photo photo);

    @Update
    void update(Photo photo);

    @Delete
    void delete(Photo photo);
}
