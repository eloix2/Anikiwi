package com.example.anikiwi.ui.anime;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anikiwi.adapter.AnimeAdapter;
import com.example.anikiwi.databinding.FragmentAnimeBinding;
import com.example.anikiwi.networking.Anime;

import java.util.List;

public class AnimeFragment extends Fragment implements AnimeAdapter.ItemClickListener {

    private FragmentAnimeBinding binding;
    private List<Anime> animes;
    private AnimeAdapter adapter;
    boolean isLoading = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AnimeViewModel animeViewModel =
                new ViewModelProvider(this).get(AnimeViewModel.class);
        animeViewModel.init();

        binding = FragmentAnimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView noResult = binding.tvErrorAnime;
        animeViewModel.getAnimesObserver().observe(getViewLifecycleOwner(), anime -> {
            if(anime != null) {
                animes = anime;
                adapter.setAnimes(animes);
                noResult.setVisibility(View.GONE);
            }
            else {
                noResult.setVisibility(View.VISIBLE);
            }
        });
        initRecyclerView();
        initScrollListener(animeViewModel);
        return root;
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.rvAnime;
        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 1));
        adapter = new AnimeAdapter(this.getContext(), animes, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAnimeClick(Anime anime) {
        Toast.makeText(this.getContext(), anime.getTitle(), Toast.LENGTH_SHORT).show();
    }

    public void initScrollListener(AnimeViewModel animeViewModel) {
        RecyclerView recyclerView = binding.rvAnime;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState){
                super.onScrollStateChanged(recyclerView, newState);

            }
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == animes.size() - 1) {
                        // bottom of list!
                        loadMore(animeViewModel);
                        isLoading = true;
                    }
                }
            }
            private void loadMore(AnimeViewModel animeViewModel) {
                animes.add(null);
                adapter.notifyDataSetChanged();
                //adapter.notifyItemInserted(animes.size() - 1);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int lastAnime = animeViewModel.getAnimeSize() - 1;
                        animes.remove(lastAnime);
                        //TODO: fix this error. A veces se cae la app al hacer scroll estando en la ultima pagina, los notify tampoco van bien, parece que pilla el index antiguo cuando se cambia de vista y se hace scroll en casos muy concretos
                        //TODO: mirar si el getItemCount() del adapter funciona
                        //adapter.notifyDataSetChanged();
                        //int scrollPosition = animes.size();
                        //adapter.notifyItemRemoved(lastAnime);
                        //load more data from the viewmodel
                        animeViewModel.loadMore();
                        //animes.addAll(animes);
                        //adapter.notifyDataSetChanged();
                        isLoading = false;
                    }
                }, 2000);
            }
        });

    }
}