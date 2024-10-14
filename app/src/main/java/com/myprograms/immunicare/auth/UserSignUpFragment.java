package com.myprograms.immunicare.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.myprograms.immunicare.R;


public class UserSignUpFragment extends Fragment {

    private Button validateButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_sign_up, container, false);

        validateButton = view.findViewById(R.id.userValidateButton);

        // Now you can work with your views, like setting click listeners
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Handle button click here
                Intent intent = new Intent(getActivity(), SuccessfulActivity.class);

                startActivity(intent);
            }
        });

        return view; // Return the inflated view

    }
}