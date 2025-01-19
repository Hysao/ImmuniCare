package com.myprograms.immunicare.user;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.auth.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class ChildInputActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private Button submitButton, dateButton, cancelButton;
    private EditText childName, childPlaceOfBirth, childAddress, childMotherName, childFatherName,
            childHeight, childWeight, childBarangay;
    private RadioButton male, female;

   private ImageView childAddphoto;
   private ImageButton helpBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference childRef = db.collection("children");
    private String encodedImage;
    private ProgressDialog progressDialog;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_child_input);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        LottieAnimationView animationView = findViewById(R.id.lottieAnimationView);


        animationView.playAnimation();



        animationView.setSpeed(1f); // Speed up

        showTermsAndConditionsDialog();

        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        submitButton = findViewById(R.id.submitBtn);
        cancelButton = findViewById(R.id.cancelBtn);
        childName = findViewById(R.id.childName);
        childPlaceOfBirth = findViewById(R.id.childPlaceOfBirth);
        childAddress = findViewById(R.id.childAddress);
        childMotherName = findViewById(R.id.childMotherName);
        childFatherName = findViewById(R.id.childFatherName);
        childHeight = findViewById(R.id.childHeight);
        childWeight = findViewById(R.id.childWeight);
        childBarangay = findViewById(R.id.childBarangay);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        childAddphoto = findViewById(R.id.childPhoto);
        helpBtn = findViewById(R.id.helpBtn);

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showHelpDialog();

            }
        });

        dateButton.setText(getTodaysDate());

        cancelButton.setOnClickListener(v -> finish());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding child data...");


        childAddphoto.setOnClickListener(v -> showPhotoOptionsDialog());

        submitButton.setOnClickListener(v -> addChild(
                childName.getText().toString(),
                male.isChecked() ? "Male" : "Female",
                childPlaceOfBirth.getText().toString(),
                dateButton.getText().toString(),
                childMotherName.getText().toString(),
                childFatherName.getText().toString(),
                childHeight.getText().toString(),
                childWeight.getText().toString()
        ));
    }

    private void showHelpDialog(){

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Height and Weight Detail");
        builder.setMessage(getString(R.string.help_info));
        builder.setCancelable(true);


        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showTermsAndConditionsDialog() {
        // Use ScrollView for large text content
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Data Privacy Policy");
        //builder.setMessage(getString(R.string.term_condition)); // Load from strings.xml
        builder.setCancelable(true);
        View termsView = getLayoutInflater().inflate(R.layout.terms_condition_dialog, null);
        builder.setView(termsView);

        builder.setPositiveButton("Agree", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.setNegativeButton("Disagree", (dialog, which) -> {
            Intent intent = new Intent(ChildInputActivity.this, AddMoreActivity.class);
            startActivity(intent);
            finish();
            dialog.dismiss();
        });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addChild(
            String childName,
            String childGender,
            String childPlaceOfBirth,
            String childDateOfBirth,
            String childMotherName,
            String childFatherName,
            String childHeight,
            String childWeight
    ) {

        if (childName.isEmpty() || childPlaceOfBirth.isEmpty() || childDateOfBirth.isEmpty()  ||
                childMotherName.isEmpty() || childFatherName.isEmpty() || childHeight.isEmpty() || childWeight.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        Random random = new Random();
        
        int randomNum1 = 1000 + random.nextInt(9000);
        int randomNum2 = 100 + random.nextInt(900);
        char randomLetter1 = (char) ('A' + random.nextInt(26));
        char randomLetter2 = (char) ('A' + random.nextInt(26));
        String childRefNumber = "IMMUNI" + randomNum1 + randomNum2 + randomLetter1 + randomLetter2;

        HashMap<String, Object> child = new HashMap<>();
        child.put("accountUid", mUser.getUid());
        child.put("childName", childName);
        child.put("childGender", childGender);
        child.put("childPlaceOfBirth", childPlaceOfBirth);
        child.put("childDateOfBirth", childDateOfBirth);
        child.put("childAddress", childAddress);
        child.put("childMotherName", childMotherName);
        child.put("childFatherName", childFatherName);
        child.put("childHeight", childHeight);
        child.put("childWeight", childWeight);
        child.put("childBarangay", childBarangay);
        if (encodedImage != null) {
            child.put("childPhoto", encodedImage);
        }


        // Default vaccine status
        String[] vaccines = {"aBBcgVaccine", "aBHepatitisBVaccine", "fVPentavalentVaccine", "fVOpvVaccine",
                "fVpneumococcalVaccine", "sVPentavalentVaccine", "sVOpvVaccine", "sVpneumococcalVaccine",
                "tVPentavalentVaccine", "tVOpvVaccine", "tVinnactivatePolioVaccine", "tVpneumococcalVaccine",
                "foVinactivatedPolio", "foVmeasslesMumpsRubella", "fiVmeasslesMumpsRubella"};

        for (String vaccine : vaccines) {
            child.put(vaccine, false);
        }

        childRef.document(childRefNumber).set(child)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(ChildInputActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ChildInputActivity.this, UserMainActivity.class);
                    startActivity(i);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChildInputActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        return months[month - 1];
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    //add photo
    private void showPhotoOptionsDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Take photo
                        openCamera();
                    } else if (which == 1) {
                        // Choose from gallery
                        openGallery();
                    }
                })
                .show();
    }

    private void openCamera() {
        // Check for camera permissions
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return;
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void openGallery() {
        // Check for read storage permissions
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
            return;
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                childAddphoto.setImageBitmap(imageBitmap);
                childAddphoto.setBackground(null);
                encodedImage = encodeImageToBase64(imageBitmap);
            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    childAddphoto.setImageBitmap(imageBitmap);
                    childAddphoto.setBackground(null);
                    encodedImage = encodeImageToBase64(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String encodeImageToBase64(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                openGallery();
            } else {
                Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
