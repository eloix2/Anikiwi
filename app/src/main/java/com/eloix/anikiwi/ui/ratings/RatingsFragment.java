package com.eloix.anikiwi.ui.ratings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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

import com.eloix.anikiwi.R;
import com.eloix.anikiwi.adapter.RatingAdapter;
import com.eloix.anikiwi.databinding.FragmentRatingsBinding;
import com.eloix.anikiwi.model.RatingWithAnime;
import com.eloix.anikiwi.networking.SessionManager;
import com.eloix.anikiwi.model.User;
import com.eloix.anikiwi.ui.animedata.AnimeDataActivity;
import com.eloix.anikiwi.utilities.FilterUtils;
import com.eloix.anikiwi.utilities.OnDataLoadedListener;
import com.eloix.anikiwi.utilities.ToolbarUtil;
import com.eloix.anikiwi.utilities.WrapContentLinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingsFragment extends Fragment implements RatingAdapter.ItemClickListener {

    private FragmentRatingsBinding binding;
    private RatingAdapter ratingAdapter;
    private RatingsViewModel ratingsViewModel;
    private List<RatingWithAnime> ratings;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButtonRetry;
    //TextView noResult;
    LinearLayout linearLayoutError;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ratingsViewModel =
                new ViewModelProvider(this).get(RatingsViewModel.class);

        binding = FragmentRatingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        linearLayoutError = binding.llErrorRating;
        // Sets the visibility of the error layout to GONE every time the fragment is created
        // This is done to avoid the error layout staying visible after the user navigates back to the fragment
        linearLayoutError.setVisibility(View.GONE);
        floatingActionButtonRetry = binding.fabRetry;
        progressBar = binding.pbRating;
        ratingAdapter = new RatingAdapter(this.getContext(), new ArrayList<>(), this);
        Log.println(Log.INFO, "RatingsFragment", "onCreateView: " + ratingsViewModel.getWatchStatus().getValue());
        ratingsViewModel.init(new OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                ratingAdapter.notifyDataSetChanged();
                linearLayoutError.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDataLoadFailed(String errorMessage) {
                linearLayoutError.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
        // Custom Toolbar
        String status = ratingsViewModel.getWatchStatus().getValue();
        ToolbarUtil.setCustomToolbar((AppCompatActivity) requireActivity(), root, "Ratings -> " + status.substring(0, 1).toUpperCase() + status.substring(1));
        setToolbarMenu(root);

        // Retry button
        floatingActionButtonRetry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            linearLayoutError.setVisibility(View.GONE);
            ratingsViewModel.reloadRatings(new OnDataLoadedListener() {
                @Override
                public void onDataLoaded() {
                    // Update the UI with the new data
                    ratingAdapter.notifyDataSetChanged();
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

        initObserver(ratingsViewModel);
        initRecyclerView();
        initScrollListener(ratingsViewModel);
        configSwipe();
        return root;
    }

    private void configSwipe() {
        binding.ratingsSwipeRefreshLayout.setColorSchemeResources(R.color.md_theme_dark_primary);
        binding.ratingsSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.md_theme_dark_onPrimary);
        binding.ratingsSwipeRefreshLayout.setOnRefreshListener(() -> {
            ratingsViewModel.refreshRatings(new OnDataLoadedListener() {
                @Override
                public void onDataLoaded() {
                    if (isAdded()){
                        // Update the UI with the new data
                        ratingAdapter.notifyDataSetChanged();
                        // Hide the loading indicator
                        binding.ratingsSwipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onDataLoadFailed(String errorMessage) {
                    if(isAdded()) {
                        // Hide the loading indicator
                        binding.ratingsSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        });
    }

    public void initScrollListener(RatingsViewModel ratingsViewModel) {
        RecyclerView recyclerView = binding.rvRating;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!ratingsViewModel.isLoading()) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == ratings.size() - 1) {
                        ratingsViewModel.setLoading(true);
                        loadMoreData(ratingsViewModel);
                    }
                }
            }
        });
    }

    private void loadMoreData(RatingsViewModel ratingsViewModel) {
        // Show a loading indicator here
        progressBar.setVisibility(View.VISIBLE);

        ratingsViewModel.loadMoreData(new OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                // Update the UI with the new data
                ratingAdapter.notifyDataSetChanged();

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

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.rvRating;
        //recyclerView.getRecycledViewPool().clear(); operacion costosa
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(ratingAdapter);
    }

    private void initObserver(RatingsViewModel ratingsViewModel) {
        User activeUser = SessionManager.getInstance().getActiveUser();
        if (activeUser != null) {
            ratingsViewModel.getRatedAnimesLiveData(activeUser.getId()).observe(getViewLifecycleOwner(), ratingWithAnimes -> {
                if (ratingWithAnimes != null) {
                    ratings = ratingWithAnimes;
                    ratingAdapter.setRatings(ratingWithAnimes); // Update the adapter
                }
            });
        }
    }

    // Sets the menu for the toolbar
    private void setToolbarMenu(View root) {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.ratings_list_toolbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_filter) {
                    // Handle action_filter click
                    //Toast.makeText(getContext(), "Filter icon pressed", Toast.LENGTH_SHORT).show();
                    showCustomDialog();
                    return true;
                } else if (item.getItemId() == R.id.action_popup_menu) {
                    // Handle action_popup_menu click
                    showPopupMenu(root);
                    return true;
                }
                return false;
            }


        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void showPopupMenu(View root) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), requireView().findViewById(R.id.action_popup_menu));
        popupMenu.getMenuInflater().inflate(R.menu.ratings_popup_menu, popupMenu.getMenu());
        // Set the item click listener
        popupMenu.setOnMenuItemClickListener(menuItem -> {
                int menuItemId = menuItem.getItemId();
                String watchStatus;
                if (menuItemId == R.id.r_popup_option1) {
                    watchStatus = "watching";
                } else if (menuItemId == R.id.r_popup_option2) {
                    watchStatus = "completed";
                } else if (menuItemId == R.id.r_popup_option3) {
                    watchStatus = "planning";
                } else if (menuItemId == R.id.r_popup_option4) {
                    watchStatus = "dropped";
                } else {
                    return false;
                }
                ratingsViewModel.setWatchStatus(watchStatus);
                progressBar.setVisibility(View.VISIBLE);
                ratingsViewModel.refreshRatings(new OnDataLoadedListener() {
                    @Override
                    public void onDataLoaded() {
                        // Update the UI with the new data
                        ratingAdapter.notifyDataSetChanged();
                        // Hide the loading indicator
                        Toolbar toolbar = root.findViewById(R.id.custom_Toolbar);
                        String status = ratingsViewModel.getWatchStatus().getValue();
                        toolbar.setTitle("Ratings -> " + status.substring(0, 1).toUpperCase() + status.substring(1));
                        linearLayoutError.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onDataLoadFailed(String errorMessage) {
                        linearLayoutError.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
                return true;
        });
        popupMenu.show();
    }

    // Called when the fragment is no longer in use
    @Override
    public void onStop(){
        super.onStop();
        // Clear the menu provider when the fragment is stopped
        binding.ratingsSwipeRefreshLayout.setRefreshing(false);
    }

    // Called when the fragment is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRatingClick(RatingWithAnime rating) {
        //Toast.makeText(this.getContext(), rating.getAnime().getTitle(), Toast.LENGTH_SHORT).show();
        //intent a la activity de anime pasando el anime

        Intent intent = new Intent(this.getContext(), AnimeDataActivity.class);
        intent.putExtra("anime_id", rating.getAnime().getId()); // Pass anime id to the details activity
        intent.putExtra("anime_title", rating.getAnime().getTitle()); // Pass anime title to the details activity
        // Add other data you want to pass to the AnimeDetailsActivity
        this.requireContext().startActivity(intent);
    }

    @Override
    public void onAddEpisodeClick(RatingWithAnime rating) {
        int maxEpisodes = rating.getAnime().getEpisodes();
        int currentEpisodesWatched = rating.getEpisodesWatched();

        if (currentEpisodesWatched < maxEpisodes) {
            // Increment episodes watched if within the limit
            rating.incrementEpisodesWatched();

            // Notify the adapter that the item has changed
            int position = ratings.indexOf(rating);
            if (position != -1) {
                ratingAdapter.notifyItemChanged(position);
            }

            // Call ViewModel method to add an episode
            ratingsViewModel.addEpisodeToRating(rating.getId(), new OnDataLoadedListener() {
                @Override
                public void onDataLoaded() {
                    // Handle data loaded (optional)
                }

                @Override
                public void onDataLoadFailed(String errorMessage) {
                    // Handle data load failure, e.g., show an error message
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // Handle case where the maximum episodes have already been watched
            Toast.makeText(getContext(), "You have already watched all the episodes", Toast.LENGTH_SHORT).show();
        }
    }

    public void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter_ratings, null);
        builder.setView(dialogView);

        EditText editTextMinScore = dialogView.findViewById(R.id.editTextMinScore);
        EditText editTextMaxScore = dialogView.findViewById(R.id.editTextMaxScore);
        EditText editTextMinDate = dialogView.findViewById(R.id.editTextMinDate);
        EditText editTextMaxDate = dialogView.findViewById(R.id.editTextMaxDate);

        // Set min and max for editTexts and set onClickListeners for the date editTexts
        FilterUtils.setEditTextFilters(editTextMinScore, "0", "10");
        FilterUtils.setEditTextFilters(editTextMaxScore, "0", "10");
        FilterUtils.setOnClickListeners(this.requireContext(), editTextMinDate);
        FilterUtils.setOnClickListeners(this.requireContext(), editTextMaxDate);

        // Set the values of the fields that are saved from the last search
        editTextMinScore.setText(ratingsViewModel.getSavedQuery("minScore"));
        editTextMaxScore.setText(ratingsViewModel.getSavedQuery("maxScore"));
        editTextMinDate.setText(ratingsViewModel.getSavedQuery("minDate"));
        editTextMaxDate.setText(ratingsViewModel.getSavedQuery("maxDate"));

        // Button to clear the EditText fields
        Button clearButton = dialogView.findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextMinScore.setText("");
                editTextMaxScore.setText("");
                editTextMinDate.setText("");
                editTextMaxDate.setText("");
            }
        });

        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String minScore = editTextMinScore.getText().toString();
                String maxScore = editTextMaxScore.getText().toString();
                String minDate = editTextMinDate.getText().toString();
                String maxDate = editTextMaxDate.getText().toString();

                // Realiza acciones con los datos ingresados por el usuario
                // Llamar a la API
                Map<String, Object> queryParams = new HashMap<>();
                if(!minScore.isEmpty())
                    queryParams.put("minScore", minScore);
                if(!maxScore.isEmpty())
                    queryParams.put("maxScore", maxScore);
                if(!minDate.isEmpty())
                    queryParams.put("minDate", minDate);
                if(!maxDate.isEmpty())
                    queryParams.put("maxDate", maxDate);

                progressBar.setVisibility(View.VISIBLE);

                ratingsViewModel.filterRatings(queryParams, new OnDataLoadedListener() {
                    @Override
                    public void onDataLoaded() {
                        // Update the UI with the new data
                        ratingAdapter.notifyDataSetChanged();
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