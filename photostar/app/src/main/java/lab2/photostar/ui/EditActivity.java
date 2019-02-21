package lab2.photostar.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import lab2.photostar.R;
import lab2.photostar.model.GalleryPhotos;
import lab2.photostar.ui.fragments.FiltersListFragment;
import lab2.photostar.utils.BitmapUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.zomato.photofilters.imageprocessors.Filter;

public class EditActivity extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener {
    public static final String PHOTO_URL_EXTRA = "photo_url_extra";
    public static final String PHOTO_SAVED_EXTRA = "saved_photo_extra";
    public static final int EDIT_PHOTO = 1;

    private ImageView imagePreview;
    private GalleryPhotos galleryPhotos;

    Bitmap originalImage;
    Bitmap filteredImage;

    private String photoUrl;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle("Edit");

        imagePreview = findViewById(R.id.image_preview);
        galleryPhotos = new GalleryPhotos(this);

        photoUrl = getIntent().getStringExtra(PHOTO_URL_EXTRA);

        loadImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveImageToGallery();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFilterSelected(Filter filter) {
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));
    }

    private void loadImage() {
        originalImage = BitmapUtils.getBitmapFromGallery(photoUrl, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(originalImage);
    }

    private void saveImageToGallery() {
        final String path = BitmapUtils.insertPhoto(getContentResolver(),
                filteredImage,
                "edit_" + System.currentTimeMillis(),
                "Edited image from: " + photoUrl);

        final String realPath = galleryPhotos.getRealPathFromURI(Uri.parse(path));

        if (!TextUtils.isEmpty(path)) {
            Intent data = new Intent();
            data.putExtra(PHOTO_URL_EXTRA, photoUrl);
            data.putExtra(PHOTO_SAVED_EXTRA, realPath);
            setResult(RESULT_OK, data);
            showToast("Image saved as: " + realPath);
        } else {
            Intent data = new Intent();
            data.putExtra(PHOTO_URL_EXTRA, photoUrl);
            setResult(RESULT_CANCELED, data);
            showToast("Saving failed");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public static void start(AppCompatActivity activity, String photoUrl) {
        Intent starter = new Intent(activity, EditActivity.class);
        starter.putExtra(PHOTO_URL_EXTRA, photoUrl);
        activity.startActivityForResult(starter, EDIT_PHOTO);
    }
}
