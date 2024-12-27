package com.myprograms.immunicare.healthworker.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.healthworker.update.ChildInfoActivity;

public class EnterRefFragment extends Fragment {

    private EditText immuniRef;
    private TextView immuniTxt;
    private MaterialButton proceedBtn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enter_ref, container, false);

        immuniRef = view.findViewById(R.id.immuniRef);
        immuniTxt = view.findViewById(R.id.immuniTxt);
        proceedBtn = view.findViewById(R.id.proceedBtn);

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputRef = immuniRef.getText().toString().trim();
                if (inputRef.isEmpty()) {
                    immuniRef.setError("Enter ImmuniCare Reference");
                    return;
                }

                String ref = immuniTxt.getText().toString().trim() + inputRef;

                db.collection("children")
                        .document(ref)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Proceed to next activity
                                Intent intent = new Intent(getActivity(), ChildInfoActivity.class);
                                intent.putExtra("documentId", ref);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "Reference not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Error checking reference: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        return view;
    }
}
