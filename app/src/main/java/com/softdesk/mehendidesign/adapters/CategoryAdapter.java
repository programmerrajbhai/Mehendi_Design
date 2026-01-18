package com.softdesk.mehendidesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.models.CategoryModel;
import com.softdesk.mehendidesign.ui.GalleryActivity;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    List<CategoryModel> categoryList;
    int lastPosition = -1;

    public CategoryAdapter(Context context, List<CategoryModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🔥 Using the new Wide Card Layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel item = categoryList.get(position);

        holder.title.setText(item.getTitle());

        Glide.with(context)
                .load(item.getImageUrl())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.image);

        // 🔥 BEAUTIFUL ANIMATION (Scale Up)
        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GalleryActivity.class);
            intent.putExtra("CAT_ID", item.getId());
            intent.putExtra("CAT_TITLE", item.getTitle());
            context.startActivity(intent);
        });
    }

    // --- Animation Logic ---
    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(500); // 0.5 second duration
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() { return categoryList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.catImage);
            title = itemView.findViewById(R.id.catTitle);
        }
    }
}