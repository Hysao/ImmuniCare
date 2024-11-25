package com.myprograms.immunicare.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;

import com.myprograms.immunicare.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ChildQRCodeActivity extends AppCompatActivity {

    private ImageView qrCodeImage;
    private TextView childID, qrChildName,
            qrChildSex, qrChildBirthDate,
            qrChildPlaceOfBirth, qrChildAddress,
            qrChildBarangay, qrChildMotherName,
            qrChildFatherName, qrChildWeight, qrChildHeight;

    private ImageButton qrBackBtn;
    private Button downloadBtn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference childRef = db.collection("children");
    private DocumentReference documentReference = childRef.document();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private String documentId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_child_qrcode);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        qrBackBtn = findViewById(R.id.qrBackBtn);
        qrBackBtn.setOnClickListener(v -> finish());

        qrCodeImage = findViewById(R.id.qrCodeImage);
        childID = findViewById(R.id.childID);
        qrChildName = findViewById(R.id.qrChildName);
        qrChildSex = findViewById(R.id.qrChildSex);
        qrChildBirthDate = findViewById(R.id.qrChildBirthDate);
        qrChildPlaceOfBirth = findViewById(R.id.qrChildPlaceOfBirth);
        qrChildAddress = findViewById(R.id.qrChildAddress);
        qrChildBarangay = findViewById(R.id.qrChildBarangay);
        qrChildMotherName = findViewById(R.id.qrChildMotherName);
        qrChildFatherName = findViewById(R.id.qrChildFatherName);
        qrChildWeight = findViewById(R.id.qrChildWeight);
        qrChildHeight = findViewById(R.id.qrChildHeight);

        downloadBtn = findViewById(R.id.downloadQrCode);


        documentId = getIntent().getStringExtra("documentId");
        String fileName = documentId + ".png";

        Bitmap qrCodeBitmap = loadQrCode(fileName);

        if (qrCodeBitmap == null) {

            qrCodeBitmap = generateQrCode(documentId);

            if (qrCodeBitmap != null) {
                saveQrCode(qrCodeBitmap, fileName); // Save the QR code for future use
            }
        }


        if (qrCodeBitmap != null) {
            qrCodeImage.setImageBitmap(qrCodeBitmap);
        }

        DocumentReference docChild = db.collection("children").document(documentId);

        docChild.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String childName = documentSnapshot.getString("childName");

                String childSex = documentSnapshot.getString("childGender");

                String childBirthDate = documentSnapshot.getString("childDateOfBirth");

                String childPlaceOfBirth = documentSnapshot.getString("childPlaceOfBirth");

                String childAddress = documentSnapshot.getString("childAddress");

                String childBarangay = documentSnapshot.getString("childBarangay");

                String childMotherName = documentSnapshot.getString("childMotherName");

                String childFatherName = documentSnapshot.getString("childFatherName");

                String childWeight = documentSnapshot.getString("childWeight");
                String childHeight = documentSnapshot.getString("childHeight");

                childID.setText(documentId);
                qrChildName.setText(childName);
                qrChildSex.setText(childSex);
                qrChildBirthDate.setText(childBirthDate);
                qrChildPlaceOfBirth.setText(childPlaceOfBirth);
                qrChildAddress.setText(childAddress);
                qrChildBarangay.setText(childBarangay);
                qrChildMotherName.setText(childMotherName);
                qrChildFatherName.setText(childFatherName);
                qrChildWeight.setText(childWeight + "kg");
                qrChildHeight.setText(childHeight + "cm");

            }
        });

        Bitmap finalQrCodeBitmap = qrCodeBitmap;
        downloadBtn.setOnClickListener(v -> {
            if (finalQrCodeBitmap != null) {
                saveToGallery(finalQrCodeBitmap, "ChildQRCode_" + documentId + ".png");
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }
        }




    }

    private Bitmap generateQrCode(String data) {
        try {
            com.google.zxing.Writer writer = new MultiFormatWriter();
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 250, 250); // 250x250 dimensions
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveQrCode(Bitmap qrCodeBitmap, String fileName) {
        try {
            // Save the file to the app's internal storage
            File directory = new File(getFilesDir(), "qrcodes");
            if (!directory.exists()) {
                directory.mkdirs(); // Create directory if it doesn't exist
            }

            File file = new File(directory, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadQrCode(String fileName) {
        File directory = new File(getFilesDir(), "qrcodes");
        File file = new File(directory, fileName);
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null; // Return null if the file doesn't exist
    }

    private void saveToGallery(Bitmap bitmap, String fileName) {
        OutputStream fos;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // For Android Q and above
                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ImmuniCare");

                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    showToast("QR Code saved to gallery.");
                }
            } else {
                // For Android versions below Q
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/ImmuniCare";
                File dir = new File(directory);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, fileName);
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();


                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
                sendBroadcast(intent);
                showToast("QR Code saved to gallery.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Failed to save QR Code.");
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



}
