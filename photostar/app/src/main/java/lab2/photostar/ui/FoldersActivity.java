package lab2.photostar.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lab2.photostar.R;
import lab2.photostar.model.GalleryPhotos;
import lab2.photostar.ui.adapters.FoldersListAdapter;

import android.os.Bundle;
import android.view.View;

public class FoldersActivity extends AppCompatActivity {
    private RecyclerView foldersList;
    private FoldersListAdapter adapter;
    private GalleryPhotos photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);

        foldersList = findViewById(R.id.folders_list);

        Toolbar toolbar = findViewById(R.id.folders_tools_bar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        photos = new GalleryPhotos(this);

        adapter = new FoldersListAdapter(this, photos.getAllFolders(), foldersListener);

        foldersList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        foldersList.setItemAnimator(new DefaultItemAnimator());
        foldersList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        foldersList.setAdapter(adapter);
    }

    private View.OnClickListener foldersListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int itemPosition = foldersList.getChildLayoutPosition(v);
            String folder = adapter.getData().get(itemPosition);
            MainActivity.start(FoldersActivity.this, folder);
        }
    };
}
