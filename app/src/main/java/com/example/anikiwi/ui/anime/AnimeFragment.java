package com.example.anikiwi.ui.anime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anikiwi.MainActivity;
import com.example.anikiwi.R;
import com.example.anikiwi.adapter.AnimeAdapter;
import com.example.anikiwi.databinding.FragmentAnimeBinding;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.ui.animedata.AnimeDataActivity;
import com.example.anikiwi.utilities.WrapContentLinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

public class AnimeFragment extends Fragment implements AnimeAdapter.ItemClickListener {

    private FragmentAnimeBinding binding;
    private List<Anime> animes;
    private AnimeAdapter adapter;
    boolean isLoading = false;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButtonRetry;
    TextView noResult;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        AnimeViewModel animeViewModel =
                new ViewModelProvider(this).get(AnimeViewModel.class);
        animeViewModel.init();

        binding = FragmentAnimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        floatingActionButtonRetry = binding.fabRetry;
        noResult = binding.tvErrorAnime;
        progressBar = binding.pbAnime;

        // Custom Toolbar
        setFragmentToolbar(root);
        setToolbarMenu();

        // Retry button
        floatingActionButtonRetry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            floatingActionButtonRetry.setVisibility(View.GONE);
            noResult.setVisibility(View.GONE);
            animeViewModel.refreshAnimes();
        });

        initObserver(animeViewModel);
        initRecyclerView();
        initScrollListener(animeViewModel);
        return root;
    }

    // Sets the toolbar for the fragment
    private void setFragmentToolbar(View root) {
        // Find the Toolbar in the fragment's layout
        Toolbar toolbar = root.findViewById(R.id.custom_Toolbar);
        toolbar.setTitle("Animes");
        // Set the Toolbar as the ActionBar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
    }
    // Sets the menu for the toolbar
    private void setToolbarMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // Add else ifs for other menu items here
                if (menuItem.getItemId() == R.id.action_search) {
                    // Handle search icon press
                    Toast.makeText(getContext(), "Search icon pressed", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void initObserver(AnimeViewModel animeViewModel){
        animeViewModel.getAnimesObserver().observe(getViewLifecycleOwner(), anime -> {
            if(anime != null) {
                animes = anime;
                adapter.setAnimes(animes);
                progressBar.setVisibility(View.GONE);
                floatingActionButtonRetry.setVisibility(View.GONE);
                noResult.setVisibility(View.GONE);
            }
            else {
                progressBar.setVisibility(View.GONE);
                floatingActionButtonRetry.setVisibility(View.VISIBLE);
                noResult.setVisibility(View.VISIBLE);
            }
        });
    }
    private void initRecyclerView() {
        RecyclerView recyclerView = binding.rvAnime;
        //recyclerView.getRecycledViewPool().clear(); operacion costosa
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
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
        //TODO: hacer que se abra el anime en cuestion
        //intent a la activity de anime pasando el anime

        Intent intent = new Intent(this.getContext(), AnimeDataActivity.class);
        intent.putExtra("anime_id", anime.getId()); // Pass anime id to the details activity
        intent.putExtra("anime_title", anime.getTitle()); // Pass anime title to the details activity
        // Add other data you want to pass to the AnimeDetailsActivity
        this.requireContext().startActivity(intent);


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

                if (!animeViewModel.isLoading()) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == animes.size() - 1) {
                        // bottom of list!
                        animeViewModel.setLoading(true);
                        loadMore(animeViewModel);
                    }
                }
            }

            //Todo: He cambiado la manera en la que se invoca a la progress bar para no meterla en el reciclerview, esto lo ha arreglado. En caso que lo deje así lo ideal quizas es borrar lo que tenga que ver con la antigua progress bar

            private void loadMore(AnimeViewModel animeViewModel) {

                progressBar.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //load more data from the viewmodel
                        animeViewModel.loadMore();
                        adapter.notifyDataSetChanged();

                        animeViewModel.setLoading(false);
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1000);
            }
        });

    }
}