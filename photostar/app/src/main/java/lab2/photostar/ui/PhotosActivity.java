package lab2.photostar.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lab2.photostar.R;
import lab2.photostar.model.Photo;
import lab2.photostar.ui.adapters.PhotosListAdapter;
import lab2.photostar.ui.vm.PhotosViewModel;

public class PhotosActivity extends AppCompatActivity {
    public static final String FOLDER_URL_EXTRA = "folder_url_extra";
    private Toolbar mainToolbar;
    private RecyclerView photosList;
    private PhotosListAdapter adapter;
    private String folder;

    private boolean isLoading = true;

    private PhotosViewModel photosViewModel;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        mainToolbar = findViewById(R.id.main_toolbar);
        photosList = findViewById(R.id.photos_list);

        folder = getIntent().getStringExtra(FOLDER_URL_EXTRA);

        mainToolbar.setTitle(Uri.parse(folder).getLastPathSegment());
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        photosViewModel = ViewModelProviders.of(this).get(PhotosViewModel.class);
        photosViewModel.setFolder(folder);

        adapter = new PhotosListAdapter(new ArrayList<Photo>(), photoListener);

        initPhotosList();

        photosViewModel.getPhotos().observe(this, new Observer<List<Photo>>() {
            @Override
            public void onChanged(List<Photo> photos) {
                isLoading = false;
                adapter.setDataset(photos);
                runLayoutAnimation(photosList);
            }
        });
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photos_activity_menu, menu);

        MenuItem layoutItem = menu.getItem(0);

        String defaultLayout = settings.getString("layout", "list");

        if (defaultLayout.equals("list")) {
            layoutItem.setIcon(R.drawable.ic_grid);
        } else {
            layoutItem.setIcon(R.drawable.ic_list);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.layout_item:
                updateListLayout(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PhotoActivity.STAR_PHOTO) {
            if (resultCode == RESULT_OK) {
                for (int i = 0; i < adapter.getDataset().size(); i++) {
                    String photoUrl = data.getStringExtra(PhotoActivity.PHOTO_URL_EXTRA);
                    if (adapter.getDataset().get(i).getPhotoUrl().equals(photoUrl)) {
                        int starCount = data.getIntExtra(PhotoActivity.PHOTO_STARS_EXTRA, 0);
                        adapter.getDataset().get(i).setStarCount(starCount);
                        runLayoutAnimation(photosList);
                    }
                }
            }
        }
    }

    private void updateListLayout(MenuItem item) {
        RecyclerView.LayoutManager current = photosList.getLayoutManager();
        if (current instanceof GridLayoutManager) {
            photosList.setLayoutManager(new LinearLayoutManager(this));
            settings.edit().putString("layout", "list").apply();
            item.setIcon(R.drawable.ic_grid);
        } else if (current instanceof LinearLayoutManager) {
            photosList.setLayoutManager(getGridLayout());
            settings.edit().putString("layout", "grid").apply();
            item.setIcon(R.drawable.ic_list);
        }
    }

    private GridLayoutManager getGridLayout() {
        int orientation = getResources().getConfiguration().orientation;
        int spanCount;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 3;
        } else {
            spanCount = 4;
        }

        return new GridLayoutManager(this, spanCount);
    }

    private void initPhotosList() {
        if (settings.getString("layout", "list").equals("list")) {
            photosList.setLayoutManager(new LinearLayoutManager(this));
        } else {
            photosList.setLayoutManager(getGridLayout());
        }
        photosList.setItemAnimator(new DefaultItemAnimator());
        photosList.setAdapter(adapter);
        photosList.addOnScrollListener(recyclerViewOnScrollListener);
        photosList.setNestedScrollingEnabled(true);
    }

    private View.OnClickListener photoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int itemPosition = photosList.getChildLayoutPosition(v);
            Photo item = ((PhotosListAdapter) photosList.getAdapter()).getDataset().get(itemPosition);
            startPhoto(item.getPhotoUrl());
        }
    };

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();

            int visibleItemCount = manager.getChildCount();
            int totalItemCount = manager.getItemCount();
            int firstVisibleItemPosition = (manager instanceof LinearLayoutManager) ?
                    ((LinearLayoutManager) manager).findFirstVisibleItemPosition() :
                    ((GridLayoutManager) manager).findFirstVisibleItemPosition();

            if (!isLoading) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 20) {
                    loadNext();
                }
            }
        }
    };

    private void loadNext() {
        photosViewModel.getNextPhotos().observe(PhotosActivity.this, new Observer<List<Photo>>() {
            @Override
            public void onChanged(List<Photo> photos) {
                isLoading = false;
                List<Photo> dataset = new ArrayList<>(adapter.getDataset());
                dataset.addAll(photos);
                adapter.setDataset(dataset);
                runLayoutAnimation(photosList);
            }
        });
    }

    private void startPhoto(String photoUrl) {
        PhotoActivity.start(this, photoUrl);
    }

    public static void start(Context context, String folder) {
        Intent starter = new Intent(context, PhotosActivity.class);
        starter.putExtra(FOLDER_URL_EXTRA, folder);
        context.startActivity(starter);
    }
}
