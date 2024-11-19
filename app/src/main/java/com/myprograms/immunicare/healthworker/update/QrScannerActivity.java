package com.myprograms.immunicare.healthworker.update;


import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
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
import com.google.mlkit.vision.barcode.common.Barcode;
import com.myprograms.immunicare.R;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QrScannerActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private MyImageAnalyzer analyzer;

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

        previewView = findViewById(R.id.previewView);
        this.getWindow().setFlags(1024, 1024);

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        analyzer = new MyImageAnalyzer(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, analyzer);

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Inner class for image analysis
    public static class MyImageAnalyzer implements ImageAnalysis.Analyzer {

        private final QrScannerActivity activity;

        public MyImageAnalyzer(QrScannerActivity activity) {
            this.activity = activity;
        }

        @Override
        public void analyze(@NonNull ImageProxy image) {
            @SuppressLint("UnsafeOptInUsageError") Image mediaImage = image.getImage();
            if (mediaImage != null) {
                com.google.mlkit.vision.common.InputImage inputImage = com.google.mlkit.vision.common.InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());
                com.google.mlkit.vision.barcode.BarcodeScanning.getClient()
                        .process(inputImage)
                        .addOnSuccessListener(barcodes -> {
                            for (Barcode barcode : barcodes) {
                                String rawValue = barcode.getRawValue();
                                // Process the scanned QR code data (rawValue) here
                                // For example, display it in a Toast or navigate to another activity
                                activity.runOnUiThread(() -> {
                                    Toast.makeText(activity, rawValue, Toast.LENGTH_SHORT).show();
                                });
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle any errors
                        })
                        .addOnCompleteListener(task -> image.close());
            }
        }
    }
}
