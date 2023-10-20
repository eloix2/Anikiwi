package com.example.anikiwi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.anikiwi.R;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.Rating;
import com.example.anikiwi.networking.RatingWithAnime;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private ItemClickListener clickListener;
    private List<RatingWithAnime> ratings;


    public RatingAdapter(Context context, List<RatingWithAnime> ratings, ItemClickListener clickListener) {
        this.context = context;
        this.ratings = ratings;
        this.clickListener = clickListener;
    }

    //sets the ratings to the provided ratings
    public void setRatings(List<RatingWithAnime> ratings) {
        this.ratings = ratings;
        notifyDataSetChanged();
    }

    //initialize the contents of the View, adding the rating item layout to the RecyclerView, based on the viewType
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_rating_item, parent, false);
        return new ItemViewHolder(view);
    }

    //updates the contents of the itemView to reflect the item at the given position
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(holder instanceof ItemViewHolder) {
            setRatingItem((ItemViewHolder) holder, position);
        }
    }

    private void setRatingItem(ItemViewHolder holder, int position) {
        holder.tv_rating_title.setText(this.ratings.get(position).getAnime().getTitle());
        holder.itemView.setOnClickListener(v -> clickListener.onRatingClick(ratings.get(position)));
        Glide.with(context)
                .load(this.ratings.get(position).getAnime().getPicture())
                .apply(RequestOptions.centerCropTransform())
                .into(holder.img_rating);
    }

    @Override
    public int getItemCount() {
        return ratings != null ? ratings.size() : 0;
    }


    //provides a reference to the views for each data item
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_rating_title;
        ImageView img_rating;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_rating_title = (TextView) itemView.findViewById(R.id.tv_rating_title);
            img_rating = (ImageView) itemView.findViewById(R.id.img_rating);
        }
    }

    //interface for click listener
    public interface ItemClickListener {
        void onRatingClick(RatingWithAnime rating);
    }
}
