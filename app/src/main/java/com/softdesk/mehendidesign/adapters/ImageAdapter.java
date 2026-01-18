package com.softdesk.mehendidesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.models.DesignItem;
import com.softdesk.mehendidesign.ui.FullViewActivity;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    Context context;
    List<DesignItem> designList;
    boolean isFeed; // true = Home (Pinterest), false = Gallery (Grid)
    int lastPosition = -1;

    public ImageAdapter(Context context, List<DesignItem> designList, boolean isFeed) {
        this.context = context;
        this.designList = designList;
        this.isFeed = isFeed;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🔥 FIX: হোম ফিডের জন্য Pinterest Layout, গ্যালারির জন্য Grid Layout
        int layoutId = isFeed ? R.layout.layout_item_feed : R.layout.layout_item_gallery;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DesignItem item = designList.get(position);

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imageView);

        // গ্যালারি মোডে সেন্টার ক্রপ, কিন্তু হোমে ফিট (Pinterest Style)
        if (!isFeed) {
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            holder.imageView.setAdjustViewBounds(true);
        }

        holder.viewCountText.setText(item.getViewCount() + "");

        // টাইটেল সেট করা (Crash ফিক্স)
        if(holder.titleView != null) {
            holder.titleView.setText(item.getCategoryName());
        }

        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(v -> {
            int newCount = item.getViewCount() + 1;
            item.setViewCount(newCount);
            notifyItemChanged(holder.getAdapterPosition());

            Intent intent = new Intent(context, FullViewActivity.class);
            intent.putExtra("IMAGE_URL", item.getImageUrl());
            context.startActivity(intent);
        });
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            TranslateAnimation anim = new TranslateAnimation(0, 0, 150, 0);
            anim.setDuration(400);
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() { return designList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView, viewCountText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage);
            titleView = itemView.findViewById(R.id.itemTitle);
            viewCountText = itemView.findViewById(R.id.itemViewCount);
        }
    }
}