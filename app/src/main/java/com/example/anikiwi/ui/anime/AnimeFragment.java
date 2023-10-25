package com.example.anikiwi.ui.anime;

import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anikiwi.R;
import com.example.anikiwi.adapter.AnimeAdapter;
import com.example.anikiwi.databinding.FragmentAnimeBinding;
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.ui.animedata.AnimeDataActivity;
import com.example.anikiwi.utilities.WrapContentLinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimeFragment extends Fragment implements AnimeAdapter.ItemClickListener {

    private FragmentAnimeBinding binding;
    private List<Anime> animes;
    private AnimeAdapter adapter;
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
        animeViewModel.init();

        binding = FragmentAnimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        linearLayoutError = binding.llErrorAnime;
        floatingActionButtonRetry = binding.fabRetry;
        //noResult = binding.tvErrorAnime;
        progressBar = binding.pbAnime;

        // Custom Toolbar
        setFragmentToolbar(root);
        setToolbarMenu();

        // Retry button
        floatingActionButtonRetry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            linearLayoutError.setVisibility(View.GONE);
            //floatingActionButtonRetry.setVisibility(View.GONE);
            //noResult.setVisibility(View.GONE);
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
                menuInflater.inflate(R.menu.anime_list_toolbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // Add else ifs for other menu items here
                if (menuItem.getItemId() == R.id.action_search) {
                    // Handle search icon press
                    Toast.makeText(getContext(), "Search icon pressed", Toast.LENGTH_SHORT).show();
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
                adapter.setAnimes(animes);
                progressBar.setVisibility(View.GONE);
                linearLayoutError.setVisibility(View.GONE);
                //floatingActionButtonRetry.setVisibility(View.GONE);
                //noResult.setVisibility(View.GONE);
            }
            else {
                progressBar.setVisibility(View.GONE);
                linearLayoutError.setVisibility(View.VISIBLE);
                //floatingActionButtonRetry.setVisibility(View.VISIBLE);
                //noResult.setVisibility(View.VISIBLE);
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
                R.array.seasons_array, android.R.layout.simple_spinner_item);

        adapterSeason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this.requireContext(),
                R.array.types_array, android.R.layout.simple_spinner_item);

        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(this.requireContext(),
                R.array.status_array, android.R.layout.simple_spinner_item);

        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

                animeViewModel.filterAnimes(queryParams);
                adapter.notifyDataSetChanged();
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