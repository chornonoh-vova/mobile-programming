package lab2.photostar.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lab2.photostar.R;
import lab2.photostar.ui.EditActivity;
import lab2.photostar.ui.adapters.ThumbnailsAdapter;
import lab2.photostar.utils.BitmapUtils;

public class FiltersListFragment extends Fragment implements ThumbnailsAdapter.ThumbnailAdapterListener {
    public static final String FILE_KEY = "file_key";

    private RecyclerView recyclerView;
    private List<ThumbnailItem> thumbnailItemList;
    private ThumbnailsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filters_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        thumbnailItemList = new ArrayList<>();
        adapter = new ThumbnailsAdapter(thumbnailItemList, this, getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareThumbnail(getActivity().getIntent().getStringExtra(EditActivity.PHOTO_URL_EXTRA));

        return view;
    }

    public void prepareThumbnail(final String file) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Bitmap thumbImage = BitmapUtils.centerCrop(BitmapUtils.getBitmapFromGallery(file, 300, 300));
                ThumbnailsManager.clearThumbs();
                thumbnailItemList.clear();

                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImage;
                thumbnailItem.filterName = "Normal image";

                List<Filter> filters = FilterPack.getFilterPack(getActivity());

                for (Filter f : filters) {
                    ThumbnailItem item = new ThumbnailItem();
                    item.image = thumbImage;
                    item.filter = f;
                    item.filterName = f.getName();
                    ThumbnailsManager.addThumb(item);
                }

                thumbnailItemList.addAll(ThumbnailsManager.processThumbs(getActivity()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };

        new Thread(r).start();
    }

    private void runOnUiThread(Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        ((FiltersListFragmentListener) getActivity()).onFilterSelected(filter);
    }

    public interface FiltersListFragmentListener {
        void onFilterSelected(Filter filter);
    }
}
