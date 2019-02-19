package lab2.photostar.ui.adapters;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import lab2.photostar.R;
import lab2.photostar.model.GalleryPhotos;
import lab2.photostar.model.Photo;

public class PhotosListAdapter extends RecyclerView.Adapter<PhotosListAdapter.ViewHolder> {
    private final View.OnClickListener listener;
    private List<Photo> dataset;

    public PhotosListAdapter(List<Photo> dataset, View.OnClickListener listener) {
        this.dataset = dataset;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_list_item, parent, false);
        v.setOnClickListener(listener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = dataset.get(position);

        Uri photoUri = Uri.parse("file://" + photo.getPhotoUrl());

        holder.photoName.setText(photoUri.getLastPathSegment());

        ContentResolver cr = holder.itemView.getContext().getContentResolver();

        holder.photo.setImageBitmap(MediaStore.Images.Thumbnails.getThumbnail(cr,
                holder.gallery.getThumbId(photo.getPhotoUrl()),
                MediaStore.Images.Thumbnails.MINI_KIND, null));

//        Picasso.get()
//                .load("file:" + photo.getPhotoUrl())
//                .placeholder(R.drawable.ic_image_placeholder)
//                .resize(200, 200)
//                .centerCrop()
//                .into(holder.photo);

        holder.setStars(photo.getStarCount());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public List<Photo> getDataset() {
        return dataset;
    }

    public void setDataset(List<Photo> dataset) {
        this.dataset = dataset;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        GalleryPhotos gallery;
        private TextView photoName;
        private ImageView photo;
        private ImageView[] stars;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            photoName = itemView.findViewById(R.id.photo_name);
            photo = itemView.findViewById(R.id.photo);

            stars = new ImageView[5];

            stars[0] = itemView.findViewById(R.id.photo_star1);
            stars[1] = itemView.findViewById(R.id.photo_star2);
            stars[2] = itemView.findViewById(R.id.photo_star3);
            stars[3] = itemView.findViewById(R.id.photo_star4);
            stars[4] = itemView.findViewById(R.id.photo_star5);

            gallery = new GalleryPhotos(itemView.getContext());
        }

        public void setStars(int starsCount) {
            Drawable filled = itemView.getContext().getDrawable(R.drawable.ic_star);
            Drawable empty = itemView.getContext().getDrawable(R.drawable.ic_star_border);
            for (int i = 0; i < 5; i++) {
                if (i < starsCount) {
                    stars[i].setImageDrawable(filled);
                } else {
                    stars[i].setImageDrawable(empty);
                }
            }
        }
    }
}
