package com.myprograms.immunicare.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;

import java.util.HashMap;
import java.util.Map;


public class UserSignUpFragment extends Fragment {

    private Button validateButton;
    private EditText nameEditText, emailEditText, passwordEditText, addressEditText;
    private TextInputLayout passwordLayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersCollection = db.collection("users");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_sign_up, container, false);

        validateButton = view.findViewById(R.id.userValidateButton);

        mAuth = FirebaseAuth.getInstance();

        nameEditText = view.findViewById(R.id.userFullName);
        emailEditText = view.findViewById(R.id.userEmail);
        passwordEditText = view.findViewById(R.id.userPassword);
        addressEditText = view.findViewById(R.id.userAddress);

        passwordLayout = view.findViewById(R.id.passwordLayout);

        passwordEditText = passwordLayout.getEditText();
        passwordLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3. Toggle the visibility of the EditText content
                if (passwordEditText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                   // passwordLayout.setEndIconDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_visibility_off)); // Replace with your visibility off icon
                } else {
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                   // passwordLayout.setEndIconDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_visibility)); // Replace with your visibility icon
                }
                passwordEditText.setSelection(passwordEditText.getText().length()); // Set cursor to the end
            }
        });



        // Now you can work with your views, like setting click listeners
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
                    // Handle empty fields (e.g., show an error message)
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign-up success, update UI with the signed-in user's information
                                    currentUser = mAuth.getCurrentUser();
                                    // ... (save user data to database or perform other actions)
                                    assert currentUser != null;
                                    sendEmailVerification(currentUser);

                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("uid", currentUser.getUid());
                                    userData.put("name", name);
                                    userData.put("email", email);
                                    userData.put("address", address);
                                    userData.put("status", "pending");
                                    userData.put("isHw", false);

                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(currentUser.getUid())
                                            .set(userData)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(getActivity(), "User registered successfully", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "Failed to register user", Toast.LENGTH_SHORT).show();
                                            });

                                    Intent intent = new Intent(getActivity(), SuccessfulActivity.class);
                                    startActivity(intent);
                                } else {
                                    // If sign-up fails, display a message to the user.
                                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });

        return view;

    }
    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        // You might want to redirect the user to a verification pending screen
                    } else {
                        Toast.makeText(getActivity(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
                        // Handle the error appropriately (e.g., log the error)
                    }
                });

    }
    }