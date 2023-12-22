package com.eloix.anikiwi.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.eloix.anikiwi.MainActivity;
import com.eloix.anikiwi.R;
import com.eloix.anikiwi.databinding.ActivityLoginBinding;
import com.eloix.anikiwi.networking.SessionManager;
import com.eloix.anikiwi.networking.User;
import com.eloix.anikiwi.repositories.UserRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001; // Request code for Google Sign-In

    private FirebaseAuth mAuth;
    private AuthStateListener mAuthStateListener;

    private UserRepository userRepository;

    private ActivityLoginBinding binding;
    private SignInButton btnLoginWithGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize UserRepository
        userRepository = UserRepository.getInstance();
        userRepository.wakeUp();

        // Initialize views
        btnLoginWithGoogle = binding.googleButton;

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your Web client ID from Firebase console
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set click listener for Google Sign-In button
        btnLoginWithGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        // Add AuthStateListener to handle sign-in events
        mAuthStateListener = new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Check if the user is already created in the database
                    User activeUser = SessionManager.getInstance().getActiveUser();

                    if (activeUser == null) {
                        // User is signed in, but not necessarily created in the database yet
                        // Make an API call to create the user in the database
                        UserRepository.createUserInDatabase(user.getDisplayName(), user.getEmail(), new UserRepository.OnUserCreatedListener() {
                            @Override
                            public void onUserCreated(User user) {
                                // User is created in the database, set as active user
                                SessionManager.getInstance().setActiveUser(user);
                                // Proceed to main activity
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish(); // Close the login activity
                            }
                        });
                    } else {
                        // User is already created in the database, proceed to main activity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish(); // Close the login activity
                    }
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, now authenticate with Firebase
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // Sign-in failed, handle the error
            Toast toast = Toast.makeText(getApplicationContext(), "Sign in failed", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign-in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Make an API call to create the user in the database
                            if (user != null) {
                                User activeUser = SessionManager.getInstance().getActiveUser();
                                if (activeUser == null) {
                                    UserRepository.createUserInDatabase(user.getDisplayName(), user.getEmail(), new UserRepository.OnUserCreatedListener() {
                                        @Override
                                        public void onUserCreated(User user) {
                                            // User is created in the database, set as active user
                                            SessionManager.getInstance().setActiveUser(user);
                                            // Proceed to main activity
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish(); // Close the login activity
                                        }
                                    });
                                }
                                else {
                                    // User is already created in the database, proceed to main activity
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish(); // Close the login activity
                                }
                            }
                        } else {
                            // Sign-in failed, handle the error
                            // Show an error message
                            Toast toast = Toast.makeText(getApplicationContext(), "Sign in failed", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
    }
}
