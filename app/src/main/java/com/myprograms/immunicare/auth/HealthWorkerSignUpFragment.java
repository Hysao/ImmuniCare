package com.myprograms.immunicare.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HealthWorkerSignUpFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference hwRef = db.collection("users");
    private FirebaseAuth mAuth;

    private EditText hwFullName, hwEmail, hwPhoneNumber, hwAddress, hwPassword;
    private ImageView photoImageView;
    private MaterialCardView photoAdded;
    private MaterialButton submitBtn;
    private String encodedImage;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_worker_sign_up, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Link UI elements
        hwFullName = view.findViewById(R.id.hwFullName);
        hwEmail = view.findViewById(R.id.hwEmail);
        hwPhoneNumber = view.findViewById(R.id.hwPhoneNumber);
        hwAddress = view.findViewById(R.id.hwAddress);
        hwPassword = view.findViewById(R.id.hwPassword);
        photoImageView = view.findViewById(R.id.addPhoto);
        submitBtn = view.findViewById(R.id.submitBtn);
        photoAdded = view.findViewById(R.id.photoAdded);

        // Add Photo Click Listener
        photoImageView.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        });

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving changes...");


        // Submit Button Click Listener
        view.findViewById(R.id.submitBtn).setOnClickListener(v -> {
            if (isWithinAllowedTime()) {
                progressDialog.show();
                String email = hwEmail.getText().toString();
                String password = hwPassword.getText().toString();

                if (validateInputs()) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        sendEmailVerification(user);
                                        saveDataToFirestore(user.getUid());
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(getActivity(), "Sign-ups are only allowed between 8:00 AM and 5:00 PM", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private boolean validateInputs() {
        if (hwFullName.getText().toString().isEmpty() ||
                hwEmail.getText().toString().isEmpty() ||
                hwPhoneNumber.getText().toString().isEmpty() ||
                hwAddress.getText().toString().isEmpty() ||
                hwPassword.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                photoImageView.setBackground(null);
                photoImageView.setImageBitmap(imageBitmap);
                encodedImage = encodeImageToBase64(imageBitmap);
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveDataToFirestore(String userId) {
        String name = hwFullName.getText().toString();
        String email = hwEmail.getText().toString();
        String phone = hwPhoneNumber.getText().toString();
        String address = hwAddress.getText().toString();

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("address", address);
        userData.put("isHw", true);
        userData.put("isVerified", "pending");
        if (encodedImage != null) {
            userData.put("photo", encodedImage);
        }

        hwRef.document(userId).set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent i = new Intent(getActivity(), SuccessfulActivity.class);
                        Toast.makeText(getActivity(), "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(i);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean isWithinAllowedTime() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        // Allowed hours: 8 AM to 5 PM (17 in 24-hour format)
        return currentHour >= 8 && currentHour < 17;
    }
}
