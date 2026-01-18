package com.softdesk.mehendidesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.ui.FullViewActivity;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    Context context;
    List<String> imageUrls;
    int lastPosition = -1;

    public ImageAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = imageUrls.get(position);

        // Hide Text Title (Design gallery te lekha dorkar nai)
        holder.titleView.setVisibility(View.GONE);

        // Load Image
        Glide.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.imageView);

        // Animation
        setAnimation(holder.itemView, position);

        // --- CLICK LISTENER (এই পার্টটা মিসিং ছিল) ---
        holder.itemView.setOnClickListener(v -> {
            // FullViewActivity তে যাওয়ার কোড
            Intent intent = new Intent(context, FullViewActivity.class);
            intent.putExtra("IMAGE_URL", url); // ছবির লিংকটা সাথে করে পাঠাচ্ছি
            context.startActivity(intent);
        });
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() { return imageUrls.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View titleView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage);
            titleView = itemView.findViewById(R.id.itemTitle);
        }
    }
}