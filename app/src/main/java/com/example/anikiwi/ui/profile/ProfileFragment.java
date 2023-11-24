package com.example.anikiwi.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.anikiwi.MainActivity;
import com.example.anikiwi.R;
import com.example.anikiwi.databinding.FragmentProfileBinding;
import com.example.anikiwi.networking.SessionManager;
import com.example.anikiwi.ui.login.LoginActivity;
import com.example.anikiwi.utilities.OnDataLoadedListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView profileImage;
    private TextView profileName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //set profile image using glide and the firebase profile image
        profileImage = binding.profileImageView;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            Uri photoUrl = currentUser.getPhotoUrl();

            if (photoUrl != null) {
                // Get the URL of the user's profile image
                String profileImageUrl = photoUrl.toString();
                // Load the profile image into the ImageView using Glide
                Glide.with(this)
                        .load(profileImageUrl)
                        .transform(new CircleCrop())
                        .into(profileImage);
            } else {
                // If the user does not have a profile image, load the default profile image
                profileImage.setImageResource(R.drawable.baseline_person_24);
            }

        }

        //set profile name
        profileName = binding.profileName;
        profileName.setText(currentUser.getDisplayName());

        // set recommendations animes images to test
        ImageView anime1 = binding.image1;
        ImageView anime2 = binding.image2;
        ImageView anime3 = binding.image3;

        setRoundedImage("https://cdn.myanimelist.net/images/anime/11/39717.jpg", anime1);
        setRoundedImage("https://cdn.myanimelist.net/images/anime/1671/127574.jpg", anime2);
        setRoundedImage("https://cdn.myanimelist.net/images/anime/12/81160.jpg", anime3);







        //final TextView textView = binding.textProfile;
        //profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        // Custom Toolbar
        setFragmentToolbar(root);
        setToolbarMenu(root);
        return root;
    }


    // Sets the toolbar for the fragment
    private void setFragmentToolbar(View root) {
        // Find the Toolbar in the fragment's layout
        Toolbar toolbar = root.findViewById(R.id.custom_Toolbar);
        toolbar.setTitle("Profile");
        // Set the Toolbar as the ActionBar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
    }
    // Sets the menu for the toolbar
    private void setToolbarMenu(View root) {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.profile_toolbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_settings) {
                    // Handle action_filter click
                    showPopupMenu(root);
                    return true;
                }
                return false;
            }


        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void showPopupMenu(View root) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), requireView().findViewById(R.id.action_settings));
        popupMenu.getMenuInflater().inflate(R.menu.profile_popup_menu, popupMenu.getMenu());

        // Set the item click listener
        popupMenu.setOnMenuItemClickListener(menuItem -> {

            if (menuItem.getItemId() == R.id.r_popup_option1) {
                // Handle option 1
                logout();
            } else {
                return false;
            }
            // If click is handled, close the popup menu
            return true;

        });

        popupMenu.show();
    }

    // logout function
    private void logout() {
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your Web client ID from Firebase console
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso);

        //sign out from google
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(requireActivity(), task -> {
                    // Clear the active user
                    SessionManager.getInstance().clearActiveUser();
                    Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                });
    }


    private void setRoundedImage(String imageUrl, ImageView imageView) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .override(300, 450) // Set the desired width and height
                .centerCrop()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                        roundedBitmapDrawable.setCornerRadius(18.0f); // You can adjust the corner radius as needed
                        roundedBitmapDrawable.setAntiAlias(true); // This improves the quality of the rounding

                        imageView.setImageDrawable(roundedBitmapDrawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle clearing the placeholder if needed
                    }
                });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}