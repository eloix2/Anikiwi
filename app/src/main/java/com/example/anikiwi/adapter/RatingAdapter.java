package com.example.anikiwi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.anikiwi.R;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.Rating;
import com.example.anikiwi.networking.RatingWithAnime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        if (holder instanceof ItemViewHolder) {
            setRatingItem((ItemViewHolder) holder, position);

            int maxEpisodes = this.ratings.get(position).getAnime().getEpisodes();
            int currentEpisodesWatched = this.ratings.get(position).getEpisodesWatched();

            // Disable the button if the maximum episodes have been watched
            ((ItemViewHolder) holder).btn_add_episode.setEnabled(currentEpisodesWatched < maxEpisodes);

            // Set click listener for the button
            ((ItemViewHolder) holder).btn_add_episode.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    clickListener.onAddEpisodeClick(ratings.get(adapterPosition));
                }
            });
        }
    }


    private void setRatingItem(ItemViewHolder holder, int position) {
        holder.tv_rating_title.setText(this.ratings.get(position).getAnime().getTitle());
        holder.itemView.setOnClickListener(v -> clickListener.onRatingClick(ratings.get(position)));
        holder.tv_rating_score.setText(String.format(Locale.getDefault(), "Rating: %d/10", this.ratings.get(position).getScore()));
        holder.tv_rating_started.setText(String.format("Started: %s", formatDate(this.ratings.get(position).getStartingDate())));
        holder.tv_rating_finished.setText(String.format("Finished: %s", formatDate(this.ratings.get(position).getFinishedDate())));
        holder.tv_episode_count.setText(String.format(Locale.getDefault(), "Episodes: %d/%d", this.ratings.get(position).getEpisodesWatched(), this.ratings.get(position).getAnime().getEpisodes()));

        Glide.with(context)
                .load(this.ratings.get(position).getAnime().getPicture())
                .transform(new CenterCrop(), new RoundedCorners(18)) // Adjust the corner radius as needed
                .into(holder.img_rating);
    }

    private String formatDate(String dateString) {
        if (dateString == null) {
            return "-";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dateString);

            if (date != null) {
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                return outputFormat.format(date);
            } else {
                return dateString; // return the original string if parsing fails
            }
        } catch (ParseException e) {
            // Handle parsing exception
            e.printStackTrace();
            return dateString; // return the original string if parsing fails
        }
    }


    @Override
    public int getItemCount() {
        return ratings != null ? ratings.size() : 0;
    }


    //provides a reference to the views for each data item
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_rating_title;
        ImageView img_rating;
        TextView tv_rating_score;
        TextView tv_rating_started;
        TextView tv_rating_finished;
        TextView tv_episode_count;
        Button btn_add_episode;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_rating_title = (TextView) itemView.findViewById(R.id.tv_rating_title);
            img_rating = (ImageView) itemView.findViewById(R.id.img_rating);
            tv_rating_score = (TextView) itemView.findViewById(R.id.tv_rating_score);
            tv_rating_started = (TextView) itemView.findViewById(R.id.tv_rating_started);
            tv_rating_finished = (TextView) itemView.findViewById(R.id.tv_rating_finished);
            tv_episode_count = (TextView) itemView.findViewById(R.id.tv_episode_count);
            btn_add_episode = itemView.findViewById(R.id.btn_add_episode);

            // Set click listener for the button
            btn_add_episode.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onAddEpisodeClick(ratings.get(position));
                }
            });
        }
    }

    //interface for click listener
    public interface ItemClickListener {
        void onRatingClick(RatingWithAnime rating);
        void onAddEpisodeClick(RatingWithAnime rating);
    }
}
