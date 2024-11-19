package com.myprograms.admin.validating.hw;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myprograms.admin.R;
import com.myprograms.admin.validating.AccountViewAdapter;
import com.myprograms.admin.validating.Users;

import java.util.ArrayList;
import java.util.List;


public class HwApprovedFragment extends Fragment {

    private RecyclerView approvedRecycler;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    private AccountViewAdapter adapter;
    private List<Users> approvedAccountList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hw_approved, container, false);

        approvedRecycler = view.findViewById(R.id.hwApprovedRecycler);

        approvedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        approvedRecycler.setHasFixedSize(true);

        Query query = usersRef.whereEqualTo("status", "approved").whereEqualTo("isHw", true);

        approvedAccountList = new ArrayList<>();

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Users users = documentSnapshot.toObject(Users.class);
                        approvedAccountList.add(users);
                    }

            if (approvedRecycler != null) { // Null check
                approvedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                approvedRecycler.setHasFixedSize(true);

                adapter = new AccountViewAdapter(approvedAccountList, getContext());
                approvedRecycler.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Something went wrong Fetching Data", Toast.LENGTH_SHORT).show();
            }

                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Loading Data Failed", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}