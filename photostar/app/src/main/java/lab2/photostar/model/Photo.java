package lab2.photostar.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Photo {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "photo_url")
    private String photoUrl;

    @ColumnInfo(name = "star_count")
    private int starCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public Photo() {
    }
}
