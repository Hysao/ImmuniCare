package com.myprograms.immunicare.healthworker.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.myprograms.immunicare.R;
import com.myprograms.immunicare.healthworker.update.QrScannerActivity;

public class QRFragment extends Fragment {

    private Button btnScan;
    private ImageButton qrImageBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_q_r, container, false);

        btnScan = view.findViewById(R.id.qrBtn);
        qrImageBtn = view.findViewById(R.id.qrImageBtn);


        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), QrScannerActivity.class);
                startActivity(i);
            }
        });



        return view;
    }



}