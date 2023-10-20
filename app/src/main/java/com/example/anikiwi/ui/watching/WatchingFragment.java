package com.example.anikiwi.ui.watching;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anikiwi.adapter.AnimeAdapter;
import com.example.anikiwi.adapter.RatingAdapter;
import com.example.anikiwi.databinding.FragmentWatchingBinding;
import com.example.anikiwi.networking.RatingWithAnime;
import com.example.anikiwi.networking.SessionManager;
import com.example.anikiwi.networking.User;
import com.example.anikiwi.utilities.WrapContentLinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class WatchingFragment extends Fragment implements RatingAdapter.ItemClickListener {

    private FragmentWatchingBinding binding;
    private RatingAdapter ratingAdapter;
    private WatchingViewModel watchingViewModel;
    private List<RatingWithAnime> ratings;
    ProgressBar progressBar;
    FloatingActionButton floatingActionButtonRetry;
    TextView noResult;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        watchingViewModel =
                new ViewModelProvider(this).get(WatchingViewModel.class);
        watchingViewModel.init();
        binding = FragmentWatchingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        floatingActionButtonRetry = binding.fabRetry;
        noResult = binding.tvErrorRating;
        progressBar = binding.pbRating;

        //TOOLBAR



        // Retry button
        floatingActionButtonRetry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            floatingActionButtonRetry.setVisibility(View.GONE);
            noResult.setVisibility(View.GONE);
            //todo: change or delete
            initObserver(watchingViewModel);

            watchingViewModel.init();
        });

        //final TextView textView = binding.textWatching;
        //watchingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        initObserver(watchingViewModel);
        initRecyclerView();
        initScrollListener(watchingViewModel);
        return root;
    }

    private void initScrollListener(WatchingViewModel watchingViewModel) {

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.rvRating;
        //recyclerView.getRecycledViewPool().clear(); operacion costosa
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        ratingAdapter = new RatingAdapter(this.getContext(), ratings, this);
        recyclerView.setAdapter(ratingAdapter);
    }

    private void initObserver(WatchingViewModel watchingViewModel) {
        User activeUser = SessionManager.getInstance().getActiveUser();
        if (activeUser != null) {
            watchingViewModel.getRatedAnimesLiveData(activeUser.getId()).observe(getViewLifecycleOwner(), ratingWithAnimes -> {
                if (ratingWithAnimes != null) {
                    ratings = ratingWithAnimes;
                    ratingAdapter.setRatings(ratingWithAnimes); // Update the adapter
                    progressBar.setVisibility(View.GONE);
                    floatingActionButtonRetry.setVisibility(View.GONE);
                    noResult.setVisibility(View.GONE);
                } else {
                    // Handle the case where there are no results
                    progressBar.setVisibility(View.GONE);
                    floatingActionButtonRetry.setVisibility(View.VISIBLE);
                    noResult.setVisibility(View.VISIBLE);
                }
            });
        } else {
            // Handle the case where activeUser is null
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRatingClick(RatingWithAnime rating) {

    }
}