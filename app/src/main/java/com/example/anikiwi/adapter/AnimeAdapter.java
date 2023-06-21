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

import org.w3c.dom.Text;

import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.MyViewHolder> {
    private Context context;
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
    //initialize the contents of the View, adding the anime item layout to the RecyclerView
    @NonNull
    @Override
    public AnimeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_anime_item, parent, false);
        return new MyViewHolder(view);
    }
    //updates the contents of the itemView to reflect the item at the given position
    @Override
    public void onBindViewHolder(@NonNull AnimeAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_anime_title.setText(this.animes.get(position).getTitle());
        holder.itemView.setOnClickListener(v -> clickListener.onAnimeClick(animes.get(position)));
        Glide.with(context)
                .load(this.animes.get(position).getImage_url())
                .apply(RequestOptions.centerCropTransform())
                .into(holder.img_anime);
    }
    //returns the total number of items in the data set held by the adapter
    @Override
    public int getItemCount() {
        if(this.animes != null)
            return this.animes.size();

        return 0;
    }

    //provides a reference to the views for each data item
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_anime_title;
        ImageView img_anime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_anime_title = (TextView) itemView.findViewById(R.id.tv_anime_title);
            img_anime = (ImageView) itemView.findViewById(R.id.img_anime);
        }
    }
    //interface for click listener
    public interface ItemClickListener {
        void onAnimeClick(Anime anime);
    }
}
