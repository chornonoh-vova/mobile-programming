package lab2.photostar.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import lab2.photostar.R;

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.ViewHolder> {
    private List<ThumbnailItem> thumbnailItemList;
    private ThumbnailAdapterListener listener;
    private Context context;
    private int selectedIndex = 0;

    public ThumbnailsAdapter(List<ThumbnailItem> thumbnailItemList, ThumbnailAdapterListener listener, Context context) {
        this.thumbnailItemList = thumbnailItemList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ThumbnailItem item = thumbnailItemList.get(position);
        holder.thumbnail.setImageBitmap(item.image);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFilterSelected(item.filter);
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });

        holder.filterName.setText(item.filterName);
        if (selectedIndex == position) {
            holder.filterName.setTextColor(ContextCompat.getColor(context, R.color.filter_label_selected));
        } else {
            holder.filterName.setTextColor(ContextCompat.getColor(context, R.color.filter_label_normal));
        }
    }

    @Override
    public int getItemCount() {
        return thumbnailItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView filterName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            filterName = itemView.findViewById(R.id.filter_name);
        }

    }

    public interface ThumbnailAdapterListener {
        void onFilterSelected(Filter filter);
    }
}
