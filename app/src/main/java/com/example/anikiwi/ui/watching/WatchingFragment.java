package com.example.anikiwi.ui.watching;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.anikiwi.databinding.FragmentWatchingBinding;

public class WatchingFragment extends Fragment {

    private FragmentWatchingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WatchingViewModel watchingViewModel =
                new ViewModelProvider(this).get(WatchingViewModel.class);

        binding = FragmentWatchingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textWatching;
        watchingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}