package lab2.photostar.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import lab2.photostar.R;

public class FoldersListAdapter extends RecyclerView.Adapter<FoldersListAdapter.ViewHolder> {
    private final View.OnClickListener listener;
    public List<String> getData() {
        return data;
    }

    private List<String> data;
    private Context context;

    public FoldersListAdapter(Context context, List<String> data, View.OnClickListener listener) {
        this.data = data;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.folders_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String folder = data.get(position);
        holder.folderName.setText(Uri.parse(folder).getLastPathSegment());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView folderName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folder_name);
            itemView.setOnClickListener(listener);
        }
    }
}
