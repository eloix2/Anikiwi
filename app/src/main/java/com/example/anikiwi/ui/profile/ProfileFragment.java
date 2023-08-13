package com.example.anikiwi.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.anikiwi.MainActivity;
import com.example.anikiwi.R;
import com.example.anikiwi.databinding.FragmentProfileBinding;
import com.example.anikiwi.ui.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Button logOutbutton;
    private GoogleSignInClient mGoogleSignInClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        logOutbutton = binding.buttonLogout;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your Web client ID from Firebase console
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso);

        logOutbutton.setOnClickListener(v -> {
            //sign out from google
            FirebaseAuth.getInstance().signOut();
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(requireActivity(), task -> {
                            Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        });

        });
        final TextView textView = binding.textProfile;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}