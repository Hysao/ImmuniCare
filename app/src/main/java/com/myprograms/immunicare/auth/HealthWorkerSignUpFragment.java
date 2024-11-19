package com.myprograms.immunicare.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.Log;

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


public class HealthWorkerSignUpFragment extends Fragment {

    private EditText hwName, hwEmail, hwAddress, hwPassword;
    private TextInputLayout passwordLayout;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference hwRef = db.collection("users");

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private Button validateBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_health_worker_sign_up, container, false);


        hwName = view.findViewById(R.id.hwFullName);
        hwEmail = view.findViewById(R.id.hwEmail);
        hwAddress = view.findViewById(R.id.hwAddress);
        hwPassword = view.findViewById(R.id.hwPassword);

        passwordLayout = view.findViewById(R.id.passwordLayoutHw);

        validateBtn = view.findViewById(R.id.hwValidateButton);

        mAuth = FirebaseAuth.getInstance();


        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = hwName.getText().toString();
                String email = hwEmail.getText().toString();
                String address = hwAddress.getText().toString();
                String password = hwPassword.getText().toString();


                if (name.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill up all the fields", Toast.LENGTH_SHORT).show();
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign-up success, update UI with the signed-in user's information
                                    mUser = mAuth.getCurrentUser();
                                    // ... (save user data to database or perform other actions)
                                    assert mUser != null;
                                    sendEmailVerification(mUser);

                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("uid", mUser.getUid());
                                    userData.put("name", name);
                                    userData.put("email", email);
                                    userData.put("address", address);
                                    userData.put("status", "pending");
                                    userData.put("isHw", true);

                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(mUser.getUid())
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
                        }).addOnFailureListener(e -> {
                            Log.e("RegistrationError", "Failed to register user", e);
                            Toast.makeText(getActivity(), "Failed to register user", Toast.LENGTH_SHORT).show();
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