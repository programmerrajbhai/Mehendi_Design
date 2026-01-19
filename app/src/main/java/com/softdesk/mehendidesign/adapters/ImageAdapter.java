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
    boolean isFeed;
    int lastPosition = -1;

    public ImageAdapter(Context context, List<DesignItem> designList, boolean isFeed) {
        this.context = context;
        this.designList = designList;
        this.isFeed = isFeed;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isFeed ? R.layout.layout_item_feed : R.layout.layout_item_gallery;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DesignItem item = designList.get(position);

        // ইমেজ লোড করা
        Glide.with(context)
                .load(item.getImageUrl())
                // .placeholder(R.drawable.placeholder_bg) // আপনার যদি placeholder থাকে তবে আন-কমেন্ট করুন
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

        // লেআউট অনুযায়ী স্কেলিং
        if (!isFeed) {
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            holder.imageView.setAdjustViewBounds(true);
        }

        // ভিউ কাউন্ট সেট করা
        if (holder.viewCountText != null) {
            holder.viewCountText.setText(String.valueOf(item.getViewCount()));
        }

        // টাইটেল সেট করা
        if(holder.titleView != null) {
            holder.titleView.setText(item.getCategoryName());
        }

        // এনিমেশন
        setAnimation(holder.itemView, position);

        // 🔥 আপডেট করা ক্লিক লিসেনার (সোয়াইপ এর জন্য)
        holder.itemView.setOnClickListener(v -> {
            // ১. ভিউ কাউন্ট আপডেট (অপশনাল, শুধু লোকালে দেখানোর জন্য)
            int newCount = item.getViewCount() + 1;
            item.setViewCount(newCount);
            notifyItemChanged(holder.getAdapterPosition());

            // ২. গ্লোবাল লিস্টে ডাটা পাঠানো (যাতে FullViewActivity তে সোয়াইপ কাজ করে)
            // এটি FullViewActivity তে আমরা static হিসেবে ডিক্লেয়ার করেছি
            FullViewActivity.sDesignList = designList;

            // ৩. পজিশন সহ ফুল ভিউ অ্যাক্টিভিটি চালু করা
            Intent intent = new Intent(context, FullViewActivity.class);
            intent.putExtra("POSITION", holder.getAdapterPosition()); // ক্লিক করা ইমেজের পজিশন
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