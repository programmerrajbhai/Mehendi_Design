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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.models.DesignItem;
import com.softdesk.mehendidesign.ui.FullViewActivity;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    Context context;
    List<DesignItem> designList;
    boolean isFeed; // true = Home (Pinterest Style), false = Gallery (Grid Style)
    int lastPosition = -1;

    public ImageAdapter(Context context, List<DesignItem> designList, boolean isFeed) {
        this.context = context;
        this.designList = designList;
        this.isFeed = isFeed;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🔥 হোম ফিডের জন্য Pinterest Layout, গ্যালারির জন্য Grid Layout
        int layoutId = isFeed ? R.layout.layout_item_feed : R.layout.layout_item_gallery;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DesignItem item = designList.get(position);

        // 🖼️ ইমেজ লোড করা (Glide দিয়ে অপটিমাইজড)
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_bg) // প্লেসহোল্ডার (আপনার ড্রয়েবল অনুযায়ী দিন)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // ক্যাশিং অন রাখা যাতে ফাস্ট লোড হয়
                .into(holder.imageView);

        // 📏 স্কেলিং ঠিক করা
        if (!isFeed) {
            // গ্যালারি মোড (Grid): ছবি ক্রপ করে বক্সে ফিট হবে
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            // হোম ফিড (Pinterest): ছবির হাইট অনুযায়ী লম্বা হবে
            holder.imageView.setAdjustViewBounds(true);
        }

        // 👀 ভিউ কাউন্ট সেট করা
        if (holder.viewCountText != null) {
            holder.viewCountText.setText(String.valueOf(item.getViewCount()));
        }

        // 📝 টাইটেল সেট করা (Null Safety সহ)
        if(holder.titleView != null) {
            holder.titleView.setText(item.getCategoryName());
        }

        // ✨ এনিমেশন
        setAnimation(holder.itemView, position);

        // 👆 ক্লিক লিসেনার (Full View তে যাওয়া)
        holder.itemView.setOnClickListener(v -> {
            // লোকাল ভিউ কাউন্ট বাড়ানো (ইফেক্টের জন্য)
            int newCount = item.getViewCount() + 1;
            item.setViewCount(newCount);
            notifyItemChanged(holder.getAdapterPosition());

            Intent intent = new Intent(context, FullViewActivity.class);
            intent.putExtra("IMAGE_URL", item.getImageUrl());
            // 🔥 নতুন: আমরা নামটাও পাঠাচ্ছি যাতে সেভ করার সময় সুন্দর নাম হয়
            intent.putExtra("IMAGE_NAME", item.getCategoryName());
            context.startActivity(intent);
        });
    }

    // সুন্দর স্ক্রল এনিমেশন
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