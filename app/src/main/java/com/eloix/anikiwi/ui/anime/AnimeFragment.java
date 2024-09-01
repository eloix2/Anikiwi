package com.eloix.anikiwi.ui.anime;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eloix.anikiwi.R;
import com.eloix.anikiwi.adapter.AnimeAdapter;
import com.eloix.anikiwi.databinding.FragmentAnimeBinding;
import com.eloix.anikiwi.model.Anime;
import com.eloix.anikiwi.ui.animedata.AnimeDataActivity;
import com.eloix.anikiwi.utilities.OnDataLoadedListener;
import com.eloix.anikiwi.utilities.ToolbarUtil;
import com.eloix.anikiwi.utilities.WrapContentLinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimeFragment extends Fragment implements AnimeAdapter.ItemClickListener {

    private FragmentAnimeBinding binding;
    private List<Anime> animes;
    private AnimeAdapter animeAdapter;
    boolean isLoading = false;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButtonRetry;
    //TextView noResult;
    LinearLayout linearLayoutError;
    private AnimeViewModel animeViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        animeViewModel =
                new ViewModelProvider(this).get(AnimeViewModel.class);

        binding = FragmentAnimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        linearLayoutError = binding.llErrorAnime;
        // Sets the visibility of the error layout to GONE every time the fragment is created
        // This is done to avoid the error layout staying visible after the user navigates back to the fragment
        linearLayoutError.setVisibility(View.GONE);
        floatingActionButtonRetry = binding.fabRetry;
        progressBar = binding.pbAnime;

        animeViewModel.init(new OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                // Update the UI with the new data
                animeAdapter.notifyDataSetChanged();
                linearLayoutError.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDataLoadFailed(String errorMessage) {
                // Show error message with floating action button to retry
                linearLayoutError.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

        // Custom Toolbar
        ToolbarUtil.setCustomToolbar((AppCompatActivity) this.requireActivity(), binding.getRoot(), "Animes");
        setToolbarMenu();

        // Retry button
        floatingActionButtonRetry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            linearLayoutError.setVisibility(View.GONE);
            animeViewModel.reloadAnimes(new OnDataLoadedListener() {
                @Override
                public void onDataLoaded() {
                    // Update the UI with the new data
                    animeAdapter.notifyDataSetChanged();
                    linearLayoutError.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onDataLoadFailed(String errorMessage) {
                    linearLayoutError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        });

        initObserver(animeViewModel);
        initRecyclerView();
        initScrollListener(animeViewModel);
        configSwipe();
        return root;
    }

    // Sets the menu for the toolbar
    private void setToolbarMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.anime_list_toolbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // Add else ifs for other menu items here
                if (menuItem.getItemId() == R.id.action_search) {
                    // Handle search icon press
                    //Toast.makeText(getContext(), "Search icon pressed", Toast.LENGTH_SHORT).show();
                    showCustomDialog();
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
                animeAdapter.setAnimes(animes);
            }
        });
    }
    private void initRecyclerView() {
        RecyclerView recyclerView = binding.rvAnime;
        //recyclerView.getRecycledViewPool().clear(); operacion costosa
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        animeAdapter = new AnimeAdapter(this.getContext(), animes, this);
        recyclerView.setAdapter(animeAdapter);
    }

    private void configSwipe() {
        binding.animeSwipeRefreshLayout.setColorSchemeResources(R.color.md_theme_dark_primary);
        binding.animeSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.md_theme_dark_onPrimary);
        binding.animeSwipeRefreshLayout.setOnRefreshListener(() -> {
            animeViewModel.refreshAnimes(new OnDataLoadedListener() {
                @Override
                public void onDataLoaded() {
                    if(isAdded()) {
                        // Update the UI with the new data
                        animeAdapter.notifyDataSetChanged();
                        binding.animeSwipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onDataLoadFailed(String errorMessage) {
                    if(isAdded()) {
                        binding.animeSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        });
    }

    // Called when the fragment is no longer in use
    @Override
    public void onStop(){
        super.onStop();
        // Clear the menu provider when the fragment is stopped
        binding.animeSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAnimeClick(Anime anime) {
        //Toast.makeText(this.getContext(), anime.getTitle(), Toast.LENGTH_SHORT).show();
        //intent a la activity de anime pasando el anime

        Intent intent = new Intent(this.getContext(), AnimeDataActivity.class);
        intent.putExtra("anime_id", anime.getId()); // Pass anime id to the details activity
        intent.putExtra("anime_title", anime.getTitle()); // Pass anime title to the details activity
        this.requireContext().startActivity(intent);
    }

    public void initScrollListener(AnimeViewModel animeViewModel) {
        RecyclerView recyclerView = binding.rvAnime;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!animeViewModel.isLoading()) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == animes.size() - 1) {
                        animeViewModel.setLoading(true);
                        loadMoreData(animeViewModel);
                    }
                }
            }
        });
    }

    private void loadMoreData(AnimeViewModel animeViewModel) {
        // Show a loading indicator
        progressBar.setVisibility(View.VISIBLE);

        animeViewModel.loadMoreData(new OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                // Update the UI with the new data
                animeAdapter.notifyDataSetChanged();

                // Hide the loading indicator
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDataLoadFailed(String errorMessage) {
                // Handle data load failure, show an error message to the user if needed
                // Hide the loading indicator
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    public void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);

        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextYear = dialogView.findViewById(R.id.editTextYear);

        // Lists of seasons, types and statuses
        Spinner spinnerSeasons = dialogView.findViewById(R.id.spinnerSeason);
        Spinner spinnerTypes = dialogView.findViewById(R.id.spinnerType);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        // Creates ArrayAdapters to populate the Spinners with seasons, types and statuses
        ArrayAdapter<CharSequence> adapterSeason = ArrayAdapter.createFromResource(this.requireContext(),
                R.array.seasons_array, R.layout.custom_spinner_item);

        adapterSeason.setDropDownViewResource(R.layout.custom_spinner_item);

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this.requireContext(),
                R.array.types_array, R.layout.custom_spinner_item);

        adapterType.setDropDownViewResource(R.layout.custom_spinner_item);

        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(this.requireContext(),
                R.array.status_array, R.layout.custom_spinner_item);

        adapterStatus.setDropDownViewResource(R.layout.custom_spinner_item);

        // Set the adapter to the Spinner
        spinnerSeasons.setAdapter(adapterSeason);
        spinnerTypes.setAdapter(adapterType);
        spinnerStatus.setAdapter(adapterStatus);

        // Set the values of the fields that are saved from the last search
        editTextTitle.setText(animeViewModel.getSavedQuery("title"));
        editTextYear.setText(animeViewModel.getSavedQuery("year"));
        spinnerSeasons.setSelection(adapterSeason.getPosition(animeViewModel.getSavedQuery("season")));
        spinnerTypes.setSelection(adapterType.getPosition(animeViewModel.getSavedQuery("type")));
        spinnerStatus.setSelection(adapterStatus.getPosition(animeViewModel.getSavedQuery("status")));

        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = editTextTitle.getText().toString();
                String year = editTextYear.getText().toString();
                String selectedSeason = spinnerSeasons.getSelectedItem().toString();
                String selectedTypes = spinnerTypes.getSelectedItem().toString();
                String selectedStatus = spinnerStatus.getSelectedItem().toString();

                // TODO: Add tag filter functionality

                // Realiza acciones con los datos ingresados por el usuario
                // Llamar a la API
                Map<String, Object> queryParams = new HashMap<>();
                if(!title.isEmpty())
                    queryParams.put("title", title);
                if(!year.isEmpty())
                    queryParams.put("year", year);
                if(!selectedSeason.isEmpty())
                    queryParams.put("season", selectedSeason);
                if(!selectedTypes.isEmpty())
                    queryParams.put("type", selectedTypes);
                if(!selectedStatus.isEmpty())
                    queryParams.put("status", selectedStatus);

                progressBar.setVisibility(View.VISIBLE);

                animeViewModel.filterAnimes(queryParams, new OnDataLoadedListener() {
                    @Override
                    public void onDataLoaded() {
                        // Update the UI with the new data
                        animeAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        linearLayoutError.setVisibility(View.GONE);
                    }

                    @Override
                    public void onDataLoadFailed(String errorMessage) {
                        // Show error message with floating action button to retry
                        linearLayoutError.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Cierra el diálogo si se presiona "Cancelar"
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}