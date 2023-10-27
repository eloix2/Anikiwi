package com.example.anikiwi.ui.ratings;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.anikiwi.ui.animedata.AnimeDataActivity;
import com.example.anikiwi.utilities.WrapContentLinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        ratingsViewModel.init();
        binding = FragmentRatingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        linearLayoutError = binding.llErrorRating;
        floatingActionButtonRetry = binding.fabRetry;
        //noResult = binding.tvErrorRating;
        progressBar = binding.pbRating;

        // Custom Toolbar
        setFragmentToolbar(root);
        setToolbarMenu();

        // Retry button
        floatingActionButtonRetry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            linearLayoutError.setVisibility(View.GONE);
            //floatingActionButtonRetry.setVisibility(View.GONE);
            //noResult.setVisibility(View.GONE);
            //todo: change or delete
            initObserver(ratingsViewModel);

            ratingsViewModel.init();
        });

        //final TextView textView = binding.textWatching;
        //ratingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        initObserver(ratingsViewModel);
        initRecyclerView();
        initScrollListener(ratingsViewModel);
        configSwipe();
        return root;
    }

    private void configSwipe() {
        binding.ratingsSwipeRefreshLayout.setOnRefreshListener(() -> {
            ratingsViewModel.getRatedAnimesLiveData(SessionManager.getInstance().getActiveUser().getId());
            binding.ratingsSwipeRefreshLayout.setRefreshing(false);
        });
    }

    private void initScrollListener(RatingsViewModel ratingsViewModel) {

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.rvRating;
        //recyclerView.getRecycledViewPool().clear(); operacion costosa
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        ratingAdapter = new RatingAdapter(this.getContext(), ratings, this);
        recyclerView.setAdapter(ratingAdapter);
    }

    private void initObserver(RatingsViewModel ratingsViewModel) {
        User activeUser = SessionManager.getInstance().getActiveUser();
        if (activeUser != null) {
            ratingsViewModel.getRatedAnimesLiveData(activeUser.getId()).observe(getViewLifecycleOwner(), ratingWithAnimes -> {
                if (ratingWithAnimes != null) {
                    ratings = ratingWithAnimes;
                    ratingAdapter.setRatings(ratingWithAnimes); // Update the adapter
                    progressBar.setVisibility(View.GONE);
                    linearLayoutError.setVisibility(View.GONE);
                    //floatingActionButtonRetry.setVisibility(View.GONE);
                    //noResult.setVisibility(View.GONE);
                } else {
                    // Handle the case where there are no results
                    progressBar.setVisibility(View.GONE);
                    linearLayoutError.setVisibility(View.VISIBLE);
                    //floatingActionButtonRetry.setVisibility(View.VISIBLE);
                    //noResult.setVisibility(View.VISIBLE);
                }
            });
        } else {
            // Handle the case where activeUser is null
        }
    }

    // Sets the toolbar for the fragment
    private void setFragmentToolbar(View root) {
        // Find the Toolbar in the fragment's layout
        Toolbar toolbar = root.findViewById(R.id.custom_Toolbar);
        toolbar.setTitle("Ratings");
        // Set the Toolbar as the ActionBar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
    }
    // Sets the menu for the toolbar
    private void setToolbarMenu() {
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
                    showPopupMenu();
                    return true;
                }
                return false;
            }


        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), requireView().findViewById(R.id.action_popup_menu));
        popupMenu.getMenuInflater().inflate(R.menu.ratings_popup_menu, popupMenu.getMenu());

        // Set the item click listener
        popupMenu.setOnMenuItemClickListener(menuItem -> {

                if (menuItem.getItemId() == R.id.r_popup_option1) {
                    // Handle option 1
                    Toast.makeText(getContext(), "Option 1 selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (menuItem.getItemId() == R.id.r_popup_option2) {
                    // Handle option 2 click
                    Toast.makeText(getContext(), "Option 2 selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (menuItem.getItemId() == R.id.r_popup_option3) {
                    // Handle option 3 click
                    Toast.makeText(getContext(), "Option 3 selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (menuItem.getItemId() == R.id.r_popup_option4) {
                    // Handle option 4 click
                    Toast.makeText(getContext(), "Option 4 selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }

        });

        popupMenu.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRatingClick(RatingWithAnime rating) {
        Toast.makeText(this.getContext(), rating.getAnime().getTitle(), Toast.LENGTH_SHORT).show();
        //intent a la activity de anime pasando el anime

        Intent intent = new Intent(this.getContext(), AnimeDataActivity.class);
        intent.putExtra("anime_id", rating.getAnime().getId()); // Pass anime id to the details activity
        intent.putExtra("anime_title", rating.getAnime().getTitle()); // Pass anime title to the details activity
        // Add other data you want to pass to the AnimeDetailsActivity
        this.requireContext().startActivity(intent);
    }
}