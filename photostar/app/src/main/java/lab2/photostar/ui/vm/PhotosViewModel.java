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

public class PhotosViewModel extends AndroidViewModel {
    private PhotoDao photoDao = App.get().getDb().photoDao();
    private GalleryPhotos galleryPhotos;
    private String folder;

    private static final int loadStep = 20;
    private int lastItem;
    private int totalItemCount;

    private List<Photo> photosList = new ArrayList<>();
    private MutableLiveData<List<Photo>> photos = null;

    public PhotosViewModel(@NonNull Application application) {
        super(application);

        galleryPhotos = new GalleryPhotos(application);
    }

    public LiveData<List<Photo>> getPhotos() {
        if (photos == null) {
            photos = new MutableLiveData<>();
            new Thread(loadPhotosList).start();

        }
        return photos;
    }

    public LiveData<List<Photo>> getNextPhotos() {
        MutableLiveData<List<Photo>> nextPhotos = new MutableLiveData<>();
        int oldLastItem = lastItem;
        if (lastItem == totalItemCount) {
            return nextPhotos;
        } else if (totalItemCount < lastItem + loadStep) {
            lastItem = totalItemCount;
            nextPhotos.postValue(photosList.subList(oldLastItem, lastItem));
        } else {
            lastItem += loadStep;
            nextPhotos.postValue(photosList.subList(oldLastItem, lastItem));
        }
        return nextPhotos;
    }

    private Runnable loadPhotosList = new Runnable() {
        @Override
        public void run() {
            List<String> photosUri = galleryPhotos.getAllPhotos();

            for (String photoUri : photosUri) {
                if (photoUri.contains(folder)) {
                    Photo photo = new Photo();
                    photo.setPhotoUrl(photoUri);
                    int starCount = photoDao.getStarsForPhoto(photoUri);

                    photo.setStarCount(starCount);

                    photosList.add(photo);
                }
            }

            totalItemCount = photosList.size();

            if (totalItemCount < loadStep) {
                lastItem = totalItemCount;
            } else {
                lastItem = loadStep;
            }

            photos.postValue(photosList.subList(0, lastItem));
        }
    };

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
