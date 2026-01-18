package com.softdesk.mehendidesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.models.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    List<CategoryModel> list;

    public CategoryAdapter(Context context, List<CategoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layout_item_category.xml ta niche banabo
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel model = list.get(position);
        holder.catName.setText(model.getTitle());

        int resourceId = context.getResources().getIdentifier(model.getImageUrl(), "drawable", context.getPackageName());

        Glide.with(context)
                .load(model.getImageUrl()) // Direct URL from model
                .placeholder(R.mipmap.ic_launcher) // Loading howa porjonto eta dekhabe
                .error(R.mipmap.ic_launcher) // Link broken hole eta dekhabe
                .centerCrop()
                .into(holder.catImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.softdesk.mehendidesign.ui.GalleryActivity.class);
            intent.putExtra("CAT_ID", model.getId()); // ID pathacchi
            intent.putExtra("CAT_TITLE", model.getTitle()); // Title pathacchi

            context.startActivity(intent);

            // --- ACTIVITY SLIDE ANIMATION ---
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImage;
        TextView catName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.itemImage);
            catName = itemView.findViewById(R.id.itemTitle);
        }
    }
}