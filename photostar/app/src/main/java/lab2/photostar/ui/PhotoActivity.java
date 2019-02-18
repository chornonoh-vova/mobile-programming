package lab2.photostar.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import lab2.photostar.App;
import lab2.photostar.R;
import lab2.photostar.dao.PhotoDao;
import lab2.photostar.model.Photo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class PhotoActivity extends AppCompatActivity {
    public static final String PHOTO_URL_EXTRA = "photo_url";
    public static final int STAR_PHOTO = 1;

    private PhotoDao photoDao = App.get().getDb().photoDao();

    private Toolbar photoToolbar;
    private ImageView photoView;
    private ImageView[] stars;

    String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photoToolbar = findViewById(R.id.photo_toolbar);
        photoView = findViewById(R.id.photo_view);

        stars = new ImageView[5];

        stars[0] = findViewById(R.id.photo_star1);
        stars[1] = findViewById(R.id.photo_star2);
        stars[2] = findViewById(R.id.photo_star3);
        stars[3] = findViewById(R.id.photo_star4);
        stars[4] = findViewById(R.id.photo_star5);

        photoUrl = getIntent().getStringExtra(PHOTO_URL_EXTRA);

        photoToolbar.setTitle(Uri.parse(photoUrl).getLastPathSegment());
        photoToolbar.bringToFront();
        setSupportActionBar(photoToolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Picasso.get().load("file:" + photoUrl).into(photoView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int starsCount = photoDao.getStarsForPhoto(photoUrl);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setStars(starsCount);
                    }
                });
            }
        }).start();

        for (int i = 0; i < stars.length; i++) {
            final int finalI = i;
            stars[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Photo p = new Photo();
                            p.setPhotoUrl(photoUrl);
                            p.setStarCount(finalI + 1);

                            Photo pDb = photoDao.getPhoto(photoUrl);

                            if (pDb != null) {
                                p.setId(pDb.getId());
                                photoDao.update(p);
                            } else {
                                photoDao.insert(p);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setStars(finalI + 1);
                                }
                            });
                        }
                    }).start();
                }
            });
        }
    }

    private void setStars(int starsCount) {
        Drawable filled = getDrawable(R.drawable.ic_star);
        Drawable empty = getDrawable(R.drawable.ic_star_border);
        for (int i = 0; i < 5; i++) {
            if (i < starsCount) {
                stars[i].setImageDrawable(filled);
            } else {
                stars[i].setImageDrawable(empty);
            }
        }
    }

    public static void start(AppCompatActivity activity, String photoUrl) {
        Intent intent = new Intent(activity, PhotoActivity.class);
        intent.putExtra(PHOTO_URL_EXTRA, photoUrl);
        activity.startActivityForResult(intent, STAR_PHOTO);
    }
}
