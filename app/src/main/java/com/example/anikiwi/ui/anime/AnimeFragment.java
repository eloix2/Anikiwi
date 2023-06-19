package com.example.anikiwi.ui.anime;

import android.os.Bundle;
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AnimeViewModel animeViewModel =
                new ViewModelProvider(this).get(AnimeViewModel.class);

        binding = FragmentAnimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView rvAnime = binding.rvAnime;
        TextView noResult = binding.tvErrorAnime;
        rvAnime.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        adapter = new AnimeAdapter(this.getContext(), animes, this);
        rvAnime.setAdapter(adapter);

        animeViewModel.getAnimesObserver().observe(getViewLifecycleOwner(), new Observer<List<Anime>>() {
            @Override
            public void onChanged(List<Anime> anime) {
                if(anime != null) {
                    animes = anime;
                    adapter.setAnimes(animes);
                    noResult.setVisibility(View.GONE);
                }
                else {
                    noResult.setVisibility(View.VISIBLE);
                }
            }
        });
        animeViewModel.makeApiCall();
        return root;
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
}