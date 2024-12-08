package com.myprograms.immunicare.healthworker.update;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.myprograms.immunicare.R;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QrScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private MyImageAnalyzer analyzer;

    private ImageView qrOverlay;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr_scanner);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        previewView = findViewById(R.id.previewView);

        qrOverlay = findViewById(R.id.qrOverlay);

        qrOverlay.setClickable(false);

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            initializeCamera();
        }
    }

    private void initializeCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        analyzer = new MyImageAnalyzer(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Camera preview setup
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Image analysis for QR code scanning
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, analyzer);

                // Bind lifecycle
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to initialize camera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void handleScannedQrCode(String documentId) {
        if (documentId != null && !documentId.isEmpty()) {
            Intent intent = new Intent(this, ChildInfoActivity.class);
            intent.putExtra("documentId", documentId); // Pass the documentId to the next activity
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes. Please enable it in settings.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    // Custom Image Analyzer class
    public static class MyImageAnalyzer implements ImageAnalysis.Analyzer {

        private final QrScannerActivity activity;

        public MyImageAnalyzer(QrScannerActivity activity) {
            this.activity = activity;
        }

        @Override
        public void analyze(@NonNull ImageProxy image) {
            @SuppressLint("UnsafeOptInUsageError") Image mediaImage = image.getImage();
            if (mediaImage != null) {
                InputImage inputImage = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());

                BarcodeScanning.getClient()
                        .process(inputImage)
                        .addOnSuccessListener(barcodes -> {
                            for (Barcode barcode : barcodes) {
                                if (barcode.getBoundingBox() != null && isBarcodeInOverlay(barcode.getBoundingBox())) {
                                    String rawValue = barcode.getRawValue();
                                    if (rawValue != null && !rawValue.isEmpty()) {
                                        activity.handleScannedQrCode(rawValue);
                                        break;
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            Toast.makeText(activity, "Failed to process QR code", Toast.LENGTH_SHORT).show();
                        })
                        .addOnCompleteListener(task -> image.close());
            } else {
                image.close(); // Always release the resource
            }
        }

        private boolean isBarcodeInOverlay(Rect barcodeBounds) {
            // Get the overlay bounds in screen coordinates
            int[] overlayPosition = new int[2];
            activity.qrOverlay.getLocationOnScreen(overlayPosition);

            Rect overlayBounds = new Rect(
                    overlayPosition[0],
                    overlayPosition[1],
                    overlayPosition[0] + activity.qrOverlay.getWidth(),
                    overlayPosition[1] + activity.qrOverlay.getHeight()
            );

            // Check if the barcode bounds are within the overlay bounds
            return overlayBounds.contains(barcodeBounds);
        }
    }


}
