package com.eloix.anikiwi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.eloix.anikiwi.R;
import com.eloix.anikiwi.model.Anime;

import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private List<Anime> animes;
    private ItemClickListener clickListener;
    public AnimeAdapter(Context context, List<Anime> animes, ItemClickListener clickListener) {
        this.context = context;
        this.animes = animes;
        this.clickListener = clickListener;
    }
    //sets the animes to the provided animes
    public void setAnimes(List<Anime> animes) {
        this.animes = animes;
        notifyDataSetChanged();
    }

    //initialize the contents of the View, adding the anime item layout to the RecyclerView, based on the viewType
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(context).inflate(R.layout.rv_anime_item, parent, false);
            return new ItemViewHolder(view);
        } else {
            //Todo: borrar esto
            view = LayoutInflater.from(context).inflate(R.layout.rv_anime_loading, parent, false);
            return new LoadingViewHolder(view);
        }

    }
    //updates the contents of the itemView to reflect the item at the given position
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(holder instanceof ItemViewHolder) {
            setAnimeItem((ItemViewHolder) holder, position);
        }
        else if(holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    private void setAnimeItem(ItemViewHolder holder, int position) {
        holder.tv_anime_title.setText(this.animes.get(position).getTitle());
        holder.itemView.setOnClickListener(v -> clickListener.onAnimeClick(animes.get(position)));
        holder.tv_anime_type.setText(this.animes.get(position).getType());
        holder.tv_anime_season_year.setText(this.animes.get(position).getSeasonAndYear());
        holder.tv_anime_status.setText(this.animes.get(position).getStatus());
        Glide.with(context)
                .load(this.animes.get(position).getImageUrl())
                .transform(new CenterCrop(), new RoundedCorners(18)) // Adjust the corner radius as needed
                .into(holder.img_anime);
    }

    //todo: borrar esto
    private void showLoadingView(LoadingViewHolder holder, int position) {
        //ProgressBar would be displayed
    }

    //returns the total number of items in the data set held by the adapter
    @Override
    public int getItemCount() {
        return animes == null ? 0 : animes.size();
    }

    public int getItemViewType(int position) {
        return animes.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    //provides a reference to the views for each data item
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_anime_title;
        ImageView img_anime;
        TextView tv_anime_type;
        TextView tv_anime_season_year;
        TextView tv_anime_status;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_anime_title = (TextView) itemView.findViewById(R.id.tv_anime_title);
            img_anime = (ImageView) itemView.findViewById(R.id.img_anime);
            tv_anime_type = (TextView) itemView.findViewById(R.id.tv_anime_type);
            tv_anime_season_year = (TextView) itemView.findViewById(R.id.tv_anime_season_year);
            tv_anime_status = (TextView) itemView.findViewById(R.id.tv_anime_status);
        }
    }

    //todo: borrar esto
    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    //interface for click listener
    public interface ItemClickListener {
        void onAnimeClick(Anime anime);
    }
}
