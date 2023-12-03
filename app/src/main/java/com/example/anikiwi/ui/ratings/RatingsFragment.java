package com.example.anikiwi.ui.ratings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anikiwi.R;
import com.example.anikiwi.adapter.RatingAdapter;
import com.example.anikiwi.databinding.FragmentRatingsBinding;
import com.example.anikiwi.networking.RatingWithAnime;
import com.example.anikiwi.networking.SessionManager;
import com.example.anikiwi.networking.User;
import com.example.anikiwi.ui.anime.AnimeViewModel;
import com.example.anikiwi.ui.animedata.AnimeDataActivity;
import com.example.anikiwi.utilities.OnDataLoadedListener;
import com.example.anikiwi.utilities.WrapContentLinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

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
        setFragmentToolbar(root);
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

    // Sets the toolbar for the fragment
    private void setFragmentToolbar(View root) {
        // Find the Toolbar in the fragment's layout
        Toolbar toolbar = root.findViewById(R.id.custom_Toolbar);
        String status = ratingsViewModel.getWatchStatus().getValue();
        toolbar.setTitle("Ratings -> " + status.substring(0, 1).toUpperCase() + status.substring(1));
        // Set the Toolbar as the ActionBar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
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
                    Toast.makeText(getContext(), "Filter icon pressed", Toast.LENGTH_SHORT).show();
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

                if (menuItem.getItemId() == R.id.r_popup_option1) {
                    // Handle option 1
                    ratingsViewModel.setWatchStatus("watching");
                } else if (menuItem.getItemId() == R.id.r_popup_option2) {
                    // Handle option 2 click
                    ratingsViewModel.setWatchStatus("completed");
                } else if (menuItem.getItemId() == R.id.r_popup_option3) {
                    // Handle option 3 click
                    ratingsViewModel.setWatchStatus("planning");
                } else if (menuItem.getItemId() == R.id.r_popup_option4) {
                    // Handle option 4 click
                    ratingsViewModel.setWatchStatus("dropped");
                } else {
                    return false;
                }

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



}