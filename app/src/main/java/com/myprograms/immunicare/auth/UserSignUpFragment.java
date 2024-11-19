package com.myprograms.immunicare.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.auth.model.Users;

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
                int inputType = passwordEditText.getInputType(); // Get the current input type

                switch (inputType) {
                    case InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        // Uncomment and use the appropriate drawable for visibility off icon
                        // passwordLayout.setEndIconDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_visibility_off));
                        break;

                    case InputType.TYPE_CLASS_TEXT:
                        passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        // Uncomment and use the appropriate drawable for visibility on icon
                        // passwordLayout.setEndIconDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_visibility));
                        break;

                    default:
                        passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }

                // Set the cursor to the end of the text
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });



        // Now you can work with your views, like setting click listeners
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (passwordEditText.length() >= 6){
                    CreateUserEmailAccount(
                            nameEditText.getText().toString(),
                            emailEditText.getText().toString(),
                            addressEditText.getText().toString(),
                            passwordEditText.getText().toString()
                    );
                }else {
                    passwordLayout.setError("Password must be at least 6 characters");
                }

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

    private void CreateUserEmailAccount(String name, String email,  String address, String password)  {
        if (!TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(address)
                && !TextUtils.isEmpty(password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()){
                            currentUser = mAuth.getCurrentUser();

                            if (currentUser != null){
                                sendEmailVerification(currentUser);
                            }else{
                                Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                            }

                            String uid = currentUser.getUid();
                            String status = "pending";
                            Boolean isHw = false;

                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("uid", uid);
                            userMap.put("name", name);
                            userMap.put("email", email);
                            userMap.put("address", address);
                            userMap.put("status", status);
                            userMap.put("isHw", isHw);

                            usersCollection.document(uid).set(userMap);



//                            Users user = new Users(uid, name, email, address, status, isHw);
//
//                            usersCollection.document(uid).set(user);


                        }else {
                            Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }


                    }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent i = new Intent(getActivity(), SuccessfulActivity.class);
                            startActivity(i);
                        }
                    });
        }
    }


    }