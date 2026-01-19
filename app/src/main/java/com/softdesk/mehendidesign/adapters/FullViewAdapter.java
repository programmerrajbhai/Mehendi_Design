package com.softdesk.mehendidesign.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.models.DesignItem;
import java.util.List;

public class FullViewAdapter extends RecyclerView.Adapter<FullViewAdapter.ViewHolder> {

    private Context context;
    private List<DesignItem> designList;

    public FullViewAdapter(Context context, List<DesignItem> designList) {
        this.context = context;
        this.designList = designList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_full_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DesignItem item = designList.get(position);
        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return designList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
        }
    }
}