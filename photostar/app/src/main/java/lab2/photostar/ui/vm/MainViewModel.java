package lab2.photostar.ui.vm;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import lab2.photostar.App;
import lab2.photostar.dao.PhotoDao;
import lab2.photostar.model.GalleryPhotos;
import lab2.photostar.model.Photo;

public class MainViewModel extends AndroidViewModel {
    private PhotoDao photoDao = App.get().getDb().photoDao();
    private GalleryPhotos galleryPhotos;
    private String folder;

    private MutableLiveData<List<Photo>> photos = null;

    public MainViewModel(@NonNull Application application) {
        super(application);

        galleryPhotos = new GalleryPhotos(application);
    }

    public LiveData<List<Photo>> getPhotos() {
        if (photos == null) {
            photos = new MutableLiveData<>();

            Runnable loadPhotos = new Runnable() {
                @Override
                public void run() {
                    List<String> photosUri = galleryPhotos.getAllPhotos();

                    List<Photo> photosObj = new ArrayList<>();

                    for (String photoUri : photosUri) {
                        if (photoUri.contains(folder)) {
                            Photo photo = new Photo();
                            photo.setPhotoUrl(photoUri);
                            int starCount = photoDao.getStarsForPhoto(photoUri);

                            photo.setStarCount(starCount);

                            photosObj.add(photo);
                        }
                    }

                    photos.postValue(photosObj);
                }
            };

            new Thread(loadPhotos).start();

        }
        return photos;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
